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
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;

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
    var connection = commerceEntityHelper.getCommerceConnection(siteId);
    if (connection == null) {
      return builder.error(CommerceConnectionUnavailable.getInstance()).build();
    }

    var storeContext = connection.getInitialStoreContext();
    if (catalogId != null) {
      storeContext = cloneStoreContextForCatalogId(catalogId, connection);
    }

    CommerceId productId = CommerceEntityHelper.getProductId(externalId, storeContext.getCatalogAlias(), connection);

    var content = productAugmentationService.getContentByExternalId(format(productId), sitesService.getSite(siteId));
    var commerceRef = CommerceRefFactory.from(externalId, PRODUCT, storeContext);

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
    var connection = commerceEntityHelper.getCommerceConnection(siteId);
    if (connection == null) {
      return builder.error(CommerceConnectionUnavailable.getInstance()).build();
    }

    var storeContext = connection.getInitialStoreContext();
    if (catalogId != null) {
      storeContext = cloneStoreContextForCatalogId(catalogId, connection);
    }

    CommerceId categoryId = CommerceEntityHelper.getCategoryId(externalId, storeContext.getCatalogAlias(), connection);

    var content = categoryAugmentationService.getContentByExternalId(format(categoryId),
            sitesService.getSite(siteId));
    var commerceRef = CommerceRefFactory.from(externalId, CATEGORY, storeContext);

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

    return commerceIdOptional.map(commerceId -> getDataForCommerceId(commerceId, connection, siteId, builder))
            .orElse(null);
  }

  private DataFetcherResult<? extends Augmentation> getDataForCommerceId(CommerceId commerceId, CommerceConnection connection, String siteId, DataFetcherResult.Builder<Augmentation> builder) {
    StoreContext initialStoreContext = connection.getInitialStoreContext();
    String externalId = commerceId.getExternalId()
            .orElseGet(() -> connection.getCommerceBeanFactory().createBeanFor(commerceId, initialStoreContext).getExternalId()
            );
    CatalogAlias catalogAlias = commerceId.getCatalogAlias();
    Optional<CatalogId> catalogId = catalogAliasTranslationService.getCatalogIdForAlias(catalogAlias, initialStoreContext);
    CommerceBeanType commerceBeanType = commerceId.getCommerceBeanType();
    StoreContextBuilder storeContextBuilder = connection.getStoreContextProvider().buildContext(initialStoreContext)
            .withCatalogAlias(catalogAlias);
    catalogId.ifPresent(storeContextBuilder::withCatalogId);
    StoreContext storeContext = storeContextBuilder.build();

    if (commerceBeanType.equals(PRODUCT)) {
      return getProductAugmentationData(commerceId, siteId, externalId, builder, storeContext);
    } else if (commerceBeanType.equals(CATEGORY)) {
      Content content = categoryAugmentationService.getContentByExternalId(format(commerceId), sitesService.getSite(siteId));
      var commerceRef = CommerceRefFactory.from(externalId, CATEGORY, storeContext);
      return builder.data(new CategoryAugmentation(commerceRef, content)).build();
    } else if (commerceBeanType.equals(SKU)){
      CommerceBean commerceBean = connection.getCommerceBeanFactory().createBeanFor(commerceId, storeContext);
      Product parent = ((ProductVariant) commerceBean).getParent();
      if (parent != null){
        return getProductAugmentationData(parent.getId(), siteId, externalId, builder, storeContext);
      }
    }

    LOG.debug( "Type {} is not supported.", commerceBeanType);
    GraphQLError error = GraphqlErrorBuilder.newError()
            .message("Type '%s' is not supported.", commerceBeanType)
            .errorType(ErrorType.DataFetchingException)
            .build();

    return DataFetcherResult.<Augmentation>newResult().error(error).build();
  }

  private DataFetcherResult<? extends Augmentation> getProductAugmentationData(CommerceId commerceId,
                                                                               String siteId,
                                                                               String externalId,
                                                                               DataFetcherResult.Builder<Augmentation> builder,
                                                                               StoreContext storeContextForCommerceId) {
    Content content = productAugmentationService.getContentByExternalId(format(commerceId), sitesService.getSite(siteId));
    var commerceRef = CommerceRefFactory.from(externalId, PRODUCT, storeContextForCommerceId);

    return builder.data(new ProductAugmentation(commerceRef, content)).build();
  }

  private StoreContext cloneStoreContextForCatalogId(String catalogId, CommerceConnection connection) {
    var storeContext = connection.getInitialStoreContext();
    if (storeContext.getCatalogId().map(CatalogId::value).filter(c -> !c.equals(catalogId)).isPresent()) {
      LOG.debug("Creating local store context for catalog Id '{}'.", catalogId);
      StoreContextBuilder storeContextBuilder = connection.getStoreContextProvider().buildContext(storeContext)
              .withCatalogId(CatalogId.of(catalogId));
      catalogAliasTranslationService.getCatalogAliasForId(CatalogId.of(catalogId), storeContext)
              .ifPresent(storeContextBuilder::withCatalogAlias);

      return storeContextBuilder.build();
    }
    return storeContext;
  }

  @Nullable
  public CommerceBean getCommerceBean(CommerceId commerceId, String siteId) {
    CommerceConnection connection = commerceEntityHelper.getCommerceConnection(siteId);
    if (connection == null) {
      return null;
    }
    return connection.getCommerceBeanFactory().createBeanFor(commerceId, connection.getInitialStoreContext());
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

    StoreContext storeContext = commerceConnection.getInitialStoreContext();
    CatalogId catalogId = catalogAliasTranslationService.getCatalogIdForAlias(commerceId.getCatalogAlias(), storeContext)
            .orElse(null);

    return CommerceRefFactory.from(commerceId, catalogId, storeContext.getStoreId(), site, List.of())
            .orElse(null);
  }
}
