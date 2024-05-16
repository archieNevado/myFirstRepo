package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapter;
import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapterFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasMappingProvider;
import com.coremedia.blueprint.caas.augmentation.CommerceIdUtils;
import com.coremedia.blueprint.caas.augmentation.CommerceSettingsHelper;
import com.coremedia.blueprint.caas.augmentation.error.InvalidCommerceId;
import com.coremedia.blueprint.caas.augmentation.error.InvalidSiteId;
import com.coremedia.blueprint.caas.augmentation.error.InvalidSiteRootSegment;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;
import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class AugmentationFacadeCmsOnly {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());
  private static final String BREADCRUMB_SEPARATOR = "/";

  private final AugmentationService categoryAugmentationService;
  private final AugmentationService productAugmentationService;
  private final SitesService sitesService;
  private final CommerceSettingsHelper commerceSettingsHelper;
  private final ByPathAdapter byPathAdapter;
  private final ObjectProvider<AugmentationContext> augmentationContextProvider;
  private final CatalogAliasMappingProvider catalogAliasMappingProvider;

  public AugmentationFacadeCmsOnly(
          AugmentationService categoryAugmentationService,
          AugmentationService productAugmentationService,
          SitesService sitesService,
          CommerceSettingsHelper commerceSettingsHelper,
          ByPathAdapterFactory byPathAdapterFactory,
          ObjectProvider<AugmentationContext> augmentationContextProvider,
          CatalogAliasMappingProvider catalogAliasMappingProvider) {
    this.categoryAugmentationService = categoryAugmentationService;
    this.productAugmentationService = productAugmentationService;
    this.sitesService = sitesService;
    this.commerceSettingsHelper = commerceSettingsHelper;
    this.byPathAdapter = byPathAdapterFactory.to();
    this.augmentationContextProvider = augmentationContextProvider;
    this.catalogAliasMappingProvider = catalogAliasMappingProvider;
  }

  @SuppressWarnings("unused") // called from ContentRoot#productAugmentationBySite
  public DataFetcherResult<ProductAugmentationCmsOnly> getProductAugmentationBySite(String externalId,
                                                                                    String[] breadcrumbParam,
                                                                                    @Nullable String catalogAlias,
                                                                                    String siteId) {
    DataFetcherResult.Builder<ProductAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return builder.error(InvalidSiteId.getInstance()).build();
    }

    augmentationContextProvider.getObject().setCmsOnly(true);

    var commerceId = parseOrBuild(externalId, site, PRODUCT, catalogAlias);
    var breadcrumb = splitBreadcrumbParameter(breadcrumbParam);
    return getProductAugmentationForSiteInternal(commerceId, breadcrumb, site);
  }

  /**
   * The whole breadcrumb is passed as a single element separated with "/" when used via rest mapping.
   * We need to split into its segments afterwards.
   *
   * @param breadcrumbElements the parameter as is. May contain a single element with separators or multiple elements.
   *                           Array with multiple elements stay untouched.
   * @return separate breadcrumb elements or the original elements if nothing to do
   */
  static List<String> splitBreadcrumbParameter(String[] breadcrumbElements) {
    if (breadcrumbElements.length == 1 && breadcrumbElements[0].contains(BREADCRUMB_SEPARATOR)) {
      return List.of(breadcrumbElements[0].split(BREADCRUMB_SEPARATOR));
    }
    return List.of(breadcrumbElements);
  }

  @SuppressWarnings("unused") // called from ContentRoot#productAugmentationBySegment
  public DataFetcherResult<ProductAugmentationCmsOnly> getProductAugmentationBySegment(String externalId,
                                                                                       String[] breadcrumbParam,
                                                                                       @Nullable String catalogAlias,
                                                                                       String rootSegment) {
    Site site = byPathAdapter.getSite(null, rootSegment).getData();
    if (site == null) {
      var builder = DataFetcherResult.<ProductAugmentationCmsOnly>newResult();
      return builder.error(InvalidSiteRootSegment.getInstance()).build();
    }

    var commerceId = parseOrBuild(externalId, site, PRODUCT, catalogAlias);
    var breadcrumb = splitBreadcrumbParameter(breadcrumbParam);
    return getProductAugmentationForSiteInternal(commerceId, breadcrumb, site);
  }

  private DataFetcherResult<ProductAugmentationCmsOnly> getProductAugmentationForSiteInternal(CommerceId productId,
                                                                                              List<String> breadcrumb,
                                                                                              Site site) {
    DataFetcherResult.Builder<ProductAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    Content content = productAugmentationService.getContentByExternalId(format(productId), site);

    var catalogAlias = productId.getCatalogAlias();
    var catalogId = catalogAliasMappingProvider.findCatalogIdForAlias(catalogAlias, site.getId())
            .orElse(null);

    CommerceRef commerceRef = getCommerceRef(productId, breadcrumb, catalogId, site);
    return builder.data(new ProductAugmentationCmsOnly(commerceRef, content)).build();
  }

  @SuppressWarnings("unused") // called from ContentRoot#categoryAugmentationBySite
  public DataFetcherResult<CategoryAugmentationCmsOnly> getCategoryAugmentationBySite(@Nullable String externalIdParam,
                                                                                      String[] breadcrumbParam,
                                                                                      @Nullable String catalogAlias,
                                                                                      String siteId) {
    DataFetcherResult.Builder<CategoryAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return builder.error(InvalidSiteId.getInstance()).build();
    }

    return getCategoryAugmentationForSiteInternal(externalIdParam, breadcrumbParam, catalogAlias, builder, site);
  }

  @SuppressWarnings("unused") // called from ContentRoot#categoryAugmentationBySegment
  public DataFetcherResult<CategoryAugmentationCmsOnly> getCategoryAugmentationBySegment(@Nullable String externalIdParam,
                                                                                         String[] breadcrumbParam,
                                                                                         @Nullable String catalogAlias,
                                                                                         String rootSegment) {
    var builder = DataFetcherResult.<CategoryAugmentationCmsOnly>newResult();
    var site = byPathAdapter.getSite(null, rootSegment).getData();
    if (site == null) {
      return builder.error(InvalidSiteRootSegment.getInstance()).build();
    }

    return getCategoryAugmentationForSiteInternal(externalIdParam, breadcrumbParam, catalogAlias, builder, site);
  }

  private DataFetcherResult<CategoryAugmentationCmsOnly> getCategoryAugmentationForSiteInternal(@Nullable String externalIdParam,
                                                                                                String[] breadcrumbParam,
                                                                                                String catalogAlias,
                                                                                                DataFetcherResult.Builder<CategoryAugmentationCmsOnly> builder,
                                                                                                Site site) {
    var breadcrumb = splitBreadcrumbParameter(breadcrumbParam);
    var externalId = externalIdParam;
    if (externalId == null && !breadcrumb.isEmpty()) {
      externalId = breadcrumb.get(breadcrumb.size() - 1);
    }
    if (externalId == null) {
      return builder.error(InvalidCommerceId.getInstance()).build();
    }
    var commerceId = parseOrBuild(externalId, site, CATEGORY, catalogAlias);
    Content content = categoryAugmentationService.getContentByExternalId(format(commerceId), site);
    CommerceRef commerceRef = getCommerceRef(commerceId, breadcrumb, null, site);
    return DataFetcherResult.<CategoryAugmentationCmsOnly>newResult()
            .data(new CategoryAugmentationCmsOnly(commerceRef, content)).build();
  }

  @SuppressWarnings("unused") // called from CommerceRootRoot#augmentationForCommerceIdBySite
  public DataFetcherResult<? extends Augmentation> getAugmentationBySite(String commerceIdStr, String[] breadcrumbParam, String siteId) {
    DataFetcherResult.Builder<Augmentation> builder = DataFetcherResult.newResult();
    Optional<CommerceId> commerceIdOptional = parseCommerceId(commerceIdStr);
    if (commerceIdOptional.isEmpty()) {
      return builder.error(InvalidCommerceId.getInstance()).build();
    }

    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return builder.error(InvalidSiteId.getInstance()).build();
    }

    var breadcrumb = splitBreadcrumbParameter(breadcrumbParam);
    return commerceIdOptional.map(commerceId -> getDataForCommerceId(commerceId, breadcrumb, site))
            .orElse(null);
  }

  private DataFetcherResult<? extends Augmentation> getDataForCommerceId(CommerceId commerceId, List<String> breadcrumb, Site site) {
    DataFetcherResult.Builder<Augmentation> builder = DataFetcherResult.newResult();

    Optional<String> externalId = commerceId.getExternalId();
    if (externalId.isEmpty()) {
      return builder.error(InvalidCommerceId.getInstance()).build();
    }

    CommerceBeanType commerceBeanType = commerceId.getCommerceBeanType();
    if (commerceBeanType.equals(PRODUCT)) {
      return getProductAugmentationForSiteInternal(commerceId, breadcrumb, site);
    } else if (commerceBeanType.equals(CATEGORY)) {
      Content content = categoryAugmentationService.getContentByExternalId(format(commerceId), site);
      CommerceRef commerceRef = getCommerceRef(commerceId, breadcrumb, null, site);
      return DataFetcherResult.<CategoryAugmentationCmsOnly>newResult()
              .data(new CategoryAugmentationCmsOnly(commerceRef, content)).build();
    }
    //in contrast to AugmentationFacade#getDataForCommerceId SKUs are not supported without underlying commerce connection

    LOG.debug("Type {} is not supported.", commerceBeanType);
    GraphQLError error = GraphqlErrorBuilder.newError()
            .message("Type {} is not supported.", commerceBeanType)
            .errorType(ErrorType.DataFetchingException)
            .build();

    return builder.error(error).build();
  }

  CommerceId parseOrBuild(String string, Site site, CommerceBeanType type, @Nullable String catalogAlias) {
    return parseCommerceId(string).orElseGet(() -> buildCommerceId(string, site, type, catalogAlias));
  }

  private CommerceId buildCommerceId(String string, Site site, CommerceBeanType type, String catalogAlias) {
    var vendor = commerceSettingsHelper.getVendor(site);
    var alias = catalogAlias != null ? CatalogAlias.of(catalogAlias) : null;
    return CommerceIdUtils.buildCommerceId(string, type, vendor, alias);
  }

  CommerceRef getCommerceRef(CommerceId commerceId, List<String> breadcrumb, @Nullable CatalogId catalogId, Site site) {
    return CommerceRefFactory.from(
            commerceId,
            catalogId != null ? catalogId : commerceSettingsHelper.getCatalogId(site),
            commerceSettingsHelper.getStoreId(site),
            commerceSettingsHelper.getLocale(site),
            site.getId(),
            breadcrumb);
  }
}
