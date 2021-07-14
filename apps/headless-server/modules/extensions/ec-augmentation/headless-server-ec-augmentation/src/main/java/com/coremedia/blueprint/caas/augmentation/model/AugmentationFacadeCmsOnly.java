package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapter;
import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapterFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceSettingsHelper;
import com.coremedia.blueprint.caas.augmentation.adapter.AugmentationPageGridAdapterFactoryCmsOnly;
import com.coremedia.blueprint.caas.augmentation.error.InvalidCommerceId;
import com.coremedia.blueprint.caas.augmentation.error.InvalidSiteId;
import com.coremedia.blueprint.caas.augmentation.error.InvalidSiteRootSegment;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbTreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import graphql.execution.DataFetcherResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;
import static com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbContentTreeRelation.ROOT_CATEGORY_ID;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class AugmentationFacadeCmsOnly {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());
  public static final String CATALOG = "catalog";

  private final AugmentationService categoryAugmentationService;
  private final AugmentationService productAugmentationService;
  private final SitesService sitesService;
  private final ObjectProvider<ExternalBreadcrumbTreeRelation> externalBreadcrumbTreeRelationProvider;
  private final CommerceSettingsHelper commerceSettingsHelper;
  private final ByPathAdapter byPathAdapter;
  private final ObjectProvider<AugmentationContext> augmentationContextProvider;

  public AugmentationFacadeCmsOnly(
          AugmentationService categoryAugmentationService,
          AugmentationService productAugmentationService,
          SitesService sitesService,
          ObjectProvider<ExternalBreadcrumbTreeRelation> externalBreadcrumbTreeRelationProvider,
          CommerceSettingsHelper commerceSettingsHelper,
          ByPathAdapterFactory byPathAdapterFactory,
          ObjectProvider<AugmentationContext> augmentationContextProvider) {
    this.categoryAugmentationService = categoryAugmentationService;
    this.productAugmentationService = productAugmentationService;
    this.sitesService = sitesService;
    this.externalBreadcrumbTreeRelationProvider = externalBreadcrumbTreeRelationProvider;
    this.commerceSettingsHelper = commerceSettingsHelper;
    this.byPathAdapter = byPathAdapterFactory.to();
    this.augmentationContextProvider = augmentationContextProvider;
  }

  @SuppressWarnings("unused")
  public DataFetcherResult<ProductAugmentationCmsOnly> getProductAugmentationBySite(String externalId, String[] breadcrumb, @Nullable String catalogAlias, String siteId) {
    DataFetcherResult.Builder<ProductAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return builder.error(InvalidSiteId.getInstance()).build();
    }

    augmentationContextProvider.getObject().setCmsOnly(true);

    return getProductAugmentationForSiteInternal(externalId, breadcrumb, catalogAlias, site);
  }

  @SuppressWarnings("unused")
  public DataFetcherResult<ProductAugmentationCmsOnly> getProductAugmentationBySegment(String externalId, String[] breadcrumb, @Nullable String catalogAlias, String rootSegment) {
    DataFetcherResult.Builder<ProductAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    Site site = resolveSite(rootSegment);
    if (site == null) {
      return builder.error(InvalidSiteRootSegment.getInstance()).build();
    }

    return getProductAugmentationForSiteInternal(externalId, breadcrumb, catalogAlias, site);
  }

  private DataFetcherResult<ProductAugmentationCmsOnly> getProductAugmentationForSiteInternal(String externalId, String[] breadcrumb, String catalogAlias, Site site) {
    DataFetcherResult.Builder<ProductAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    CommerceIdBuilder idBuilder = CommerceIdBuilder.builder(Vendor.of(commerceSettingsHelper.getVendor(site)), CATALOG, PRODUCT)
            .withExternalId(externalId);

    //update catalogAlias if given
    if (catalogAlias != null) {
      idBuilder.withCatalogAlias(CatalogAlias.of(catalogAlias));
    }

    CommerceId productId = idBuilder.build();
    Content content = productAugmentationService.getContentByExternalId(format(productId), site);

    //initialize tree relation
    initializeBreadcrumbTreeRelation(breadcrumb, Vendor.of(commerceSettingsHelper.getVendor(site)));

    CommerceRef commerceRef = getCommerceRef(productId, Arrays.asList(breadcrumb), catalogAlias, site);

    return builder.data(new ProductAugmentationCmsOnly(commerceRef, content)).build();
  }

  @SuppressWarnings("unused")
  public DataFetcherResult<CategoryAugmentationCmsOnly> getCategoryAugmentationBySite(String externalId, String[] breadcrumb, @Nullable String catalogAlias, String siteId) {
    DataFetcherResult.Builder<CategoryAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return builder.error(InvalidSiteId.getInstance()).build();
    }

    return getCategoryAugmentationForSiteInternal(externalId, breadcrumb, catalogAlias, site);
  }

  @SuppressWarnings("unused")
  public DataFetcherResult<CategoryAugmentationCmsOnly> getCategoryAugmentationBySegment(String externalId, String[] breadcrumb, @Nullable String catalogAlias, String rootSegment) {
    DataFetcherResult.Builder<CategoryAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    Site site = resolveSite(rootSegment);
    if (site == null) {
      return builder.error(InvalidSiteRootSegment.getInstance()).build();
    }

    return getCategoryAugmentationForSiteInternal(externalId, breadcrumb, catalogAlias, site);
  }

  private DataFetcherResult<CategoryAugmentationCmsOnly> getCategoryAugmentationForSiteInternal(String externalId, String[] breadcrumb, @Nullable String catalogAlias, Site site) {
    DataFetcherResult.Builder<CategoryAugmentationCmsOnly> builder = DataFetcherResult.newResult();
    Vendor vendor = Vendor.of(commerceSettingsHelper.getVendor(site));
    CommerceIdBuilder idBuilder = CommerceIdBuilder.builder(vendor, CATALOG, CATEGORY)
            .withExternalId(externalId);

    //update catalogAlias if given
    if (catalogAlias != null) {
      idBuilder.withCatalogAlias(CatalogAlias.of(catalogAlias));
    }

    CommerceId categoryId = idBuilder.build();

    Content content = categoryAugmentationService.getContentByExternalId(format(categoryId), site);

    initializeBreadcrumbTreeRelation(breadcrumb, vendor);

    CommerceRef commerceRef = getCommerceRef(categoryId, Arrays.asList(breadcrumb), null, site);

    return builder.data(new CategoryAugmentationCmsOnly(commerceRef, content)).build();
  }

  @Nullable
  private Site resolveSite(String rootSegment){
    Content homepage = byPathAdapter.getPageByPath("", rootSegment);
    return (homepage != null) ? sitesService.getContentSiteAspect(homepage).getSite() : null;
  }

  /**
   * Initializes an {@link ExternalBreadcrumbTreeRelation} in request scope.
   * This instance is used later on to resolve inherited page grids. This is done in
   * {@link AugmentationPageGridAdapterFactoryCmsOnly}.
   *
   * @param breadcrumb array of external ids
   * @param vendor commerce vendor for the current site
   */
  void initializeBreadcrumbTreeRelation(String[] breadcrumb, Vendor vendor) {
    //set breadcrumb in treerelation, which is a request scoped bean
    if (breadcrumb.length > 0) {
      List<String> extendedBreadcrumb = new ArrayList();
      //check if first element is root category, otherwise add (possibly virtual) root
      if (!breadcrumb[0].equals(ROOT_CATEGORY_ID)) {
        extendedBreadcrumb.add(ROOT_CATEGORY_ID);
      }
      extendedBreadcrumb.addAll(Arrays.asList(breadcrumb));

      CommerceIdBuilder categoryIdBuilder = CommerceIdBuilder.builder(vendor, CATALOG, CATEGORY);
      List<String> listOfCommerceIdStrings = extendedBreadcrumb.stream()
              .map(bc -> categoryIdBuilder.withExternalId(bc).build())
              .map(CommerceIdFormatterHelper::format)
              .collect(Collectors.toList());

      ExternalBreadcrumbTreeRelation breadcrumbTreeRelation = externalBreadcrumbTreeRelationProvider.getObject();
      breadcrumbTreeRelation.setBreadcrumb(listOfCommerceIdStrings);
    }
  }

  @SuppressWarnings("unused")
  public DataFetcherResult<? extends Augmentation> getAugmentationBySite(String commerceIdStr, String[] breadcrumbs, @Nullable String catalogAlias, String siteId) {
    DataFetcherResult.Builder<Augmentation> builder = DataFetcherResult.newResult();
    Optional<CommerceId> commerceIdOptional = CommerceIdParserHelper.parseCommerceId(commerceIdStr);
    if (commerceIdOptional.isEmpty()) {
      return builder.error(InvalidCommerceId.getInstance()).build();
    }
    Site site = sitesService.getSite(siteId);
    if (site == null) {
      return builder.error(InvalidSiteId.getInstance()).build();
    }

    return commerceIdOptional.map(commerceId -> getDataForCommerceId(commerceId, breadcrumbs, catalogAlias, site))
            .orElse(null);
  }

  @Nullable
  private DataFetcherResult<? extends Augmentation> getDataForCommerceId(CommerceId commerceId, String[] breadcrumbs, String catalogAlias, Site site) {
    DataFetcherResult.Builder<Augmentation> builder = DataFetcherResult.newResult();

    Optional<String> externalId = commerceId.getExternalId();
    if (externalId.isEmpty()) {
      return builder.error(InvalidCommerceId.getInstance()).build();
    }

    CommerceBeanType commerceBeanType = commerceId.getCommerceBeanType();
    if (commerceBeanType.equals(PRODUCT)) {
      return getProductAugmentationForSiteInternal(externalId.get(), breadcrumbs, catalogAlias, site);
    } else if (commerceBeanType.equals(CATEGORY)) {
      return getCategoryAugmentationForSiteInternal(externalId.get(), breadcrumbs, catalogAlias, site);
    }

    return null;
  }

  CommerceRef getCommerceRef(CommerceId commerceId, List<String> breadcrumb, @Nullable String catalogId, Site site) {
    return CommerceRefFactory.from(
            commerceId,
            catalogId != null ? CatalogId.of(catalogId) : CatalogId.of(commerceSettingsHelper.getCatalogId(site)),
            commerceSettingsHelper.getStoreId(site),
            commerceSettingsHelper.getLocale(site),
            site.getId(),
            breadcrumb);
  }
}
