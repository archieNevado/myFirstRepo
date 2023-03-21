package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceSiteFinder;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceConnectionHelper;
import com.coremedia.blueprint.caas.augmentation.error.CommerceConnectionUnavailable;
import com.coremedia.blueprint.caas.augmentation.error.InvalidCommerceId;
import com.coremedia.blueprint.caas.augmentation.error.InvalidSiteId;
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
  private final CommerceConnectionHelper commerceConnectionHelper;
  private final CatalogAliasTranslationService catalogAliasTranslationService;
  private final CommerceSiteFinder commerceSiteFinder;

  public AugmentationFacade(
          AugmentationService categoryAugmentationService,
          AugmentationService productAugmentationService,
          SitesService sitesService,
          CommerceConnectionHelper commerceConnectionHelper,
          CatalogAliasTranslationService catalogAliasTranslationService,
          CommerceSiteFinder commerceSiteFinder) {
    this.categoryAugmentationService = categoryAugmentationService;
    this.productAugmentationService = productAugmentationService;
    this.sitesService = sitesService;
    this.commerceConnectionHelper = commerceConnectionHelper;
    this.catalogAliasTranslationService = catalogAliasTranslationService;
    this.commerceSiteFinder = commerceSiteFinder;
  }

  @SuppressWarnings("unused")
  public DataFetcherResult<ProductAugmentation> getProductAugmentationByStore(String externalId, @Nullable String catalogId, String storeId, String locale) {
    return commerceSiteFinder.findSiteFor(storeId, Locale.forLanguageTag(locale))
            .map(site -> doGetProductAugmentationBySite(externalId, catalogId, site))
            .orElseGet(() -> {
              DataFetcherResult.Builder<ProductAugmentation> builder = DataFetcherResult.newResult();
              return builder.error(CommerceConnectionUnavailable.getInstance()).build();
            });
  }

  public DataFetcherResult<ProductAugmentation> getProductAugmentationBySite(String externalId, @Nullable String catalogId, String siteId) {
    var site = sitesService.getSite(siteId);
    if (site == null) {
      return DataFetcherResult.<ProductAugmentation>newResult().error(InvalidSiteId.getInstance()).build();
    }
    return doGetProductAugmentationBySite(externalId, catalogId, site);
  }

  private DataFetcherResult<ProductAugmentation> doGetProductAugmentationBySite(String externalId, String catalogId, Site site) {
    DataFetcherResult.Builder<ProductAugmentation> builder = DataFetcherResult.newResult();
    var connection = commerceConnectionHelper.getCommerceConnection(site);
    var storeContext = connection.getInitialStoreContext();
    if (catalogId != null) {
      storeContext = cloneStoreContextForCatalogId(catalogId, connection);
    }

    var catalogAlias = storeContext.getCatalogAlias();
    var productId = parseOrBuild(externalId, connection, catalogAlias, PRODUCT);
    var commerceRef = CommerceRefFactory.from(productId, storeContext)
            .orElse(null);
    if (commerceRef == null) {
      return builder.error(InvalidCommerceId.getInstance()).build();
    }

    var content = productAugmentationService.getContentByExternalId(format(productId), site);
    return builder.data(new ProductAugmentation(commerceRef, content)).build();
  }

  @SuppressWarnings("unused")
  public DataFetcherResult<CategoryAugmentation> getCategoryAugmentationByStore(String externalId, @Nullable String catalogId, String storeId, String locale) {
    return commerceSiteFinder.findSiteFor(storeId, Locale.forLanguageTag(locale))
            .map(site -> doGetCategoryAugmentationBySite(externalId, catalogId, site))
            .orElseGet(() -> {
              DataFetcherResult.Builder<CategoryAugmentation> builder = DataFetcherResult.newResult();
              return builder.error(CommerceConnectionUnavailable.getInstance()).build();
            });
  }

  @Nullable
  @SuppressWarnings("unused")
  public DataFetcherResult<CategoryAugmentation> getCategoryAugmentationBySite(String externalId, @Nullable String catalogId, String siteId) {
    var site = sitesService.getSite(siteId);
    if (site == null) {
      return DataFetcherResult.<CategoryAugmentation>newResult().error(InvalidSiteId.getInstance()).build();
    }
    return doGetCategoryAugmentationBySite(externalId, catalogId, site);
  }

  private DataFetcherResult<CategoryAugmentation> doGetCategoryAugmentationBySite(String externalId, String catalogId, Site site) {
    var builder = DataFetcherResult.<CategoryAugmentation>newResult();
    var connection = commerceConnectionHelper.getCommerceConnection(site);
    var storeContext = connection.getInitialStoreContext();
    if (catalogId != null) {
      storeContext = cloneStoreContextForCatalogId(catalogId, connection);
    }

    var catalogAlias = storeContext.getCatalogAlias();
    var categoryId = parseOrBuild(externalId, connection, catalogAlias, CATEGORY);
    var commerceRef = CommerceRefFactory.from(categoryId, storeContext)
            .orElse(null);
    if (commerceRef == null) {
      return builder.error(InvalidCommerceId.getInstance()).build();
    }

    var content = categoryAugmentationService.getContentByExternalId(format(categoryId), site);
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

    var site = sitesService.getSite(siteId);
    if (site == null) {
      return DataFetcherResult.<ProductAugmentation>newResult().error(InvalidSiteId.getInstance()).build();
    }
    CommerceConnection connection = commerceConnectionHelper.getCommerceConnection(site);
    return commerceIdOptional.map(commerceId -> getDataForCommerceId(commerceId, connection, site, builder))
            .orElse(null);
  }

  private DataFetcherResult<? extends Augmentation> getDataForCommerceId(CommerceId commerceId, CommerceConnection connection, Site site, DataFetcherResult.Builder<Augmentation> builder) {
    StoreContext initialStoreContext = connection.getInitialStoreContext();
    CatalogAlias catalogAlias = commerceId.getCatalogAlias();
    Optional<CatalogId> catalogId = catalogAliasTranslationService.getCatalogIdForAlias(catalogAlias, initialStoreContext);
    CommerceBeanType commerceBeanType = commerceId.getCommerceBeanType();
    StoreContextBuilder storeContextBuilder = connection.getStoreContextProvider().buildContext(initialStoreContext)
            .withCatalogAlias(catalogAlias);
    catalogId.ifPresent(storeContextBuilder::withCatalogId);
    StoreContext storeContext = storeContextBuilder.build();

    var commerceRef = getCommerceRef(commerceId, initialStoreContext);
    if (commerceBeanType.equals(PRODUCT)) {
      Content content = productAugmentationService.getContentByExternalId(format(commerceId), site);
      return builder.data(new ProductAugmentation(commerceRef, content)).build();
    } else if (commerceBeanType.equals(CATEGORY)) {
      Content content = categoryAugmentationService.getContentByExternalId(format(commerceId), site);
      return builder.data(new CategoryAugmentation(commerceRef, content)).build();
    } else if (commerceBeanType.equals(SKU)){
      CommerceBean commerceBean = connection.getCommerceBeanFactory().createBeanFor(commerceId, storeContext);
      Product parent = ((ProductVariant) commerceBean).getParent();
      if (parent != null){
        Content content = productAugmentationService.getContentByExternalId(format(parent.getId()), site);
        return builder.data(new ProductAugmentation(commerceRef, content)).build();
      }
    }

    LOG.debug( "Type {} is not supported.", commerceBeanType);
    GraphQLError error = GraphqlErrorBuilder.newError()
            .message("Type '%s' is not supported.", commerceBeanType)
            .errorType(ErrorType.DataFetchingException)
            .build();

    return DataFetcherResult.<Augmentation>newResult().error(error).build();
  }

  private static CommerceRef getCommerceRef(CommerceId commerceId, StoreContext storeContext) {
    return CommerceRefFactory.from(commerceId, storeContext)
            .orElseGet(() -> {
              var commerceBeanFactory = storeContext.getConnection().getCommerceBeanFactory();
              var commerceBean = commerceBeanFactory.createBeanFor(commerceId, storeContext);
              return CommerceRefFactory.from(commerceBean);
            });
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

  /**
   * Trys to parse the given ID into a commerce ID. Builds a commerce id using the given ID as external ID part
   * if parsing fails.
   * @param id                either a commerce ID or an external ID
   * @param connection        a commerce connection
   * @param catalogAlias      the catalog alias to use if a commerce ID is created
   * @param commerceBeanType  the commerce bean type to use if a commerce ID is created
   * @return a commerce id
   */
  private static CommerceId parseOrBuild(String id, CommerceConnection connection, CatalogAlias catalogAlias,
                                         CommerceBeanType commerceBeanType) {
    return CommerceIdParserHelper.parseCommerceId(id)
            .orElseGet(() ->
                    CommerceIdBuilder.buildCopyOf(connection.getIdProvider().format(commerceBeanType, catalogAlias, id))
                            // make sure that this is not a techID commerceID
                            .withExternalId(id).build());
  }
}
