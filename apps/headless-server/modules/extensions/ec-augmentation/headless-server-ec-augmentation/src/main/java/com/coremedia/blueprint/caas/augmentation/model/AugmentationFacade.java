package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceSiteFinder;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.blueprint.caas.augmentation.error.CommerceConnectionUnavailable;
import com.coremedia.blueprint.caas.augmentation.error.InvalidCommerceId;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import graphql.execution.DataFetcherResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;

@DefaultAnnotation(NonNull.class)
public class AugmentationFacade {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final AugmentationService categoryAugmentationService;
  private final AugmentationService productAugmentationService;
  private final SitesService sitesService;
  private final CommerceEntityHelper commerceEntityHelper;
  private final CatalogAliasTranslationService catalogAliasTranslationService;
  private final CommerceSiteFinder commerceSiteFinder;

  public AugmentationFacade(
          AugmentationService categoryAugmentationService,
          AugmentationService productAugmentationService,
          SitesService sitesService,
          CommerceEntityHelper commerceEntityHelper,
          CatalogAliasTranslationService catalogAliasTranslationService, CommerceSiteFinder commerceSiteFinder) {
    this.categoryAugmentationService = categoryAugmentationService;
    this.productAugmentationService = productAugmentationService;
    this.sitesService = sitesService;
    this.commerceEntityHelper = commerceEntityHelper;
    this.catalogAliasTranslationService = catalogAliasTranslationService;
    this.commerceSiteFinder = commerceSiteFinder;
  }

  @SuppressWarnings("unused")
  public DataFetcherResult<ProductAugmentation> getProductAugmentationByStore(String externalId, @Nullable String catalogId, String storeId, String locale) {

    return commerceSiteFinder.findSiteFor(storeId, Locale.forLanguageTag(locale))
            .map(Site::getId)
            .map(siteId -> getProductAugmentationBySite(externalId, catalogId, siteId))
            .orElseGet(() -> {
              DataFetcherResult.Builder<ProductAugmentation> builder = DataFetcherResult.newResult();
              return builder.error(CommerceConnectionUnavailable.getInstance()).build();
            });
  }

  public DataFetcherResult<ProductAugmentation> getProductAugmentationBySite(String externalId, @Nullable String catalogId, String siteId) {
    DataFetcherResult.Builder<ProductAugmentation> builder = DataFetcherResult.newResult();
    CommerceConnection connection = commerceEntityHelper.getCommerceConnection(siteId);
    if (connection == null) {
      return builder.error(CommerceConnectionUnavailable.getInstance()).build();
    }

    //update catalogId if given
    if (catalogId != null) {
      updateCatalogId(catalogId, connection);
    }

    StoreContext storeContext = connection.getStoreContext();
    CommerceId productId = CommerceEntityHelper.getProductId(externalId, connection);

    Content content = productAugmentationService.getContentByExternalId(format(productId), sitesService.getSite(siteId));
    CommerceRef commerceRef = CommerceRefFactory.from(externalId, PRODUCT, storeContext);

    return builder.data(new ProductAugmentation(commerceRef, content)).build();
  }


  @SuppressWarnings("unused")
  public DataFetcherResult<CategoryAugmentation> getCategoryAugmentationByStore(String externalId, @Nullable String catalogId, String storeId, String locale) {

    return commerceSiteFinder.findSiteFor(storeId, Locale.forLanguageTag(locale))
            .map(Site::getId)
            .map(siteId -> getCategoryAugmentationBySite(externalId, catalogId, siteId))
            .orElseGet(() -> {
              DataFetcherResult.Builder<CategoryAugmentation> builder = DataFetcherResult.newResult();
              return builder.error(CommerceConnectionUnavailable.getInstance()).build();
            });
  }

  @Nullable
  @SuppressWarnings("unused")
  public DataFetcherResult<CategoryAugmentation> getCategoryAugmentationBySite(String externalId, @Nullable String catalogId, String siteId) {
    DataFetcherResult.Builder<CategoryAugmentation> builder = DataFetcherResult.newResult();
    CommerceConnection connection = commerceEntityHelper.getCommerceConnection(siteId);
    if (connection == null) {
      return builder.error(CommerceConnectionUnavailable.getInstance()).build();
    }

    //update catalogId if given
    if (catalogId != null) {
      updateCatalogId(catalogId, connection);
    }

    StoreContext storeContext = connection.getStoreContext();
    CommerceId categoryId = CommerceEntityHelper.getCategoryId(externalId, connection);

    Content content = categoryAugmentationService.getContentByExternalId(format(categoryId),
            sitesService.getSite(siteId));
    CommerceRef commerceRef = CommerceRefFactory.from(externalId, CATEGORY, storeContext);

    return builder.data(new CategoryAugmentation(commerceRef, content)).build();
  }

  @Nullable
  @SuppressWarnings("unused")
  public DataFetcherResult<? extends Augmentation> getAugmentationBySite(String commerceIdStr, String siteId) {
    DataFetcherResult.Builder<Augmentation> builder = DataFetcherResult.newResult();
    Optional<CommerceId> commerceIdOptional = CommerceIdParserHelper.parseCommerceId(commerceIdStr);
    if (commerceIdOptional.isEmpty()){
      return builder.error(InvalidCommerceId.getInstance()).build();
    }

    CommerceConnection connection = commerceEntityHelper.getCommerceConnection(siteId);
    if (connection == null) {
      return builder.error(CommerceConnectionUnavailable.getInstance()).build();
    }

    return commerceIdOptional.map(commerceId -> getDataForCommerceId(commerceId, connection, siteId))
            .orElse(null);
  }

  @Nullable
  @SuppressWarnings("unused")
  private DataFetcherResult<? extends Augmentation> getDataForCommerceId(CommerceId commerceId, CommerceConnection connection, String siteId) {
    String externalId = commerceId.getExternalId()
            .orElseGet(() ->
                    connection.getCommerceBeanFactory().createBeanFor(commerceId, connection.getStoreContext()).getExternalId()
            );

    String catalogId = extractCatalogId(commerceId, connection);
    CommerceBeanType commerceBeanType = commerceId.getCommerceBeanType();

    if (commerceBeanType.equals(PRODUCT)) {
      return getProductAugmentationBySite(externalId, catalogId, siteId);
    } else if (commerceBeanType.equals(CATEGORY)) {
      return getCategoryAugmentationBySite(externalId, catalogId, siteId);
    }
    return null;
  }

  @Nullable
  private String extractCatalogId(CommerceId commerceId, CommerceConnection connection) {
    //get CatalogId from CommerceId
    CatalogAlias catalogAlias = commerceId.getCatalogAlias();
    String catalogId = null;
    if (!catalogAlias.equals(DEFAULT_CATALOG_ALIAS)){
      catalogId = catalogAliasTranslationService.getCatalogIdForAlias(catalogAlias, connection.getStoreContext())
              .map(CatalogId::value)
              .orElse(null);
    }
    return catalogId;
  }

  private static void updateCatalogId(String catalogId, CommerceConnection connection) {
    StoreContextBuilder storeContextBuilder = connection.getStoreContextProvider().buildContext(connection.getStoreContext())
            .withCatalogId(CatalogId.of(catalogId));
    connection.setInitialStoreContext(storeContextBuilder.build());
  }

  @Nullable
  public CommerceBean getCommerceBean(CommerceId commerceId, String siteId) {
    CommerceConnection connection = commerceEntityHelper.getCommerceConnection(siteId);
    if (connection == null) {
      return null;
    }
    return connection.getCommerceBeanFactory().createBeanFor(commerceId, connection.getStoreContext());
  }

  @Nullable
  public CommerceRef getCommerceRef(Content content, String externalReferencePropertyName){
    String commerceIdStr = content.getString(externalReferencePropertyName);

    CommerceId commerceId = CommerceIdParserHelper.parseCommerceId(commerceIdStr).orElse(null);
    if (commerceId == null || commerceId.getExternalId().isEmpty()){
      LOG.debug("externalId is null for {}", content.getId());
      return null;
    }

    Site site = sitesService.getContentSiteAspect(content).getSite();
    if (site == null) {
      LOG.debug("no site for {} {}", content.getId(), commerceIdStr);
      return null;
    }

    CommerceConnection commerceConnection = commerceEntityHelper.getCommerceConnection(site.getId());
    if (commerceConnection == null){
      LOG.debug("commerceConnection is null for {} {}", content.getId(), commerceIdStr);
      return null;
    }

    StoreContext storeContext = commerceConnection.getStoreContext();
    CatalogId catalogId = catalogAliasTranslationService.getCatalogIdForAlias(commerceId.getCatalogAlias(), storeContext)
            .orElse(null);

    return CommerceRefFactory.from(commerceId, catalogId, storeContext.getStoreId(), site, List.of())
            .orElse(null);
  }
}
