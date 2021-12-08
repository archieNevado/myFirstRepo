package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.caas.model.adapter.PageGridAdapter;
import com.coremedia.blueprint.base.caas.model.adapter.PageGridAdapterFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceSettingsHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbContentTreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class AugmentationPageGridAdapterFactoryCmsOnly extends PageGridAdapterFactory {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());
  private static final String CATALOG = "catalog";

  private final SitesService sitesService;
  private final String propertyName;
  private final AugmentationService augmentationService;
  private final ExternalBreadcrumbContentTreeRelation externalBreadcrumbContentTreeRelation;
  private final CommerceEntityHelper commerceEntityHelper;
  private final CommerceSettingsHelper commerceSettingsHelper;

  public AugmentationPageGridAdapterFactoryCmsOnly(String propertyName,
                                                   AugmentationService augmentationService,
                                                   ContentBackedPageGridService contentBackedPageGridService,
                                                   SitesService sitesService,
                                                   ExternalBreadcrumbContentTreeRelation externalBreadcrumbContentTreeRelation,
                                                   CommerceEntityHelper commerceEntityHelper,
                                                   CommerceSettingsHelper commerceSettingsHelper) {
    super(contentBackedPageGridService);
    this.propertyName = propertyName;
    this.augmentationService = augmentationService;
    this.sitesService = sitesService;
    this.externalBreadcrumbContentTreeRelation = externalBreadcrumbContentTreeRelation;
    this.commerceEntityHelper = commerceEntityHelper;
    this.commerceSettingsHelper = commerceSettingsHelper;
  }

  public PageGridAdapter to(CommerceRef commerceRef, DataFetchingEnvironment dataFetchingEnvironment) {
    return to(getContent(commerceRef, sitesService.getSite(commerceRef.getSiteId())),
            propertyName, dataFetchingEnvironment);
  }

  private CommerceId getCommerceId(CommerceRef commerceRef) {
    return commerceEntityHelper.getCommerceId(commerceRef);
  }

  @SuppressWarnings("OverlyComplexMethod")
  private Content getContent(CommerceRef commerceRef, Site site) {
    CommerceId id = getCommerceId(commerceRef);
    Content content = augmentationService.getContentByExternalId(CommerceIdFormatterHelper.format(id), site);
    if (content != null) {
      return content;
    }

    //Raise Exception, if commerce bean type is not supported (e.g. SKU)
    if (!isCommerceBeanTypSupported(id.getCommerceBeanType())) {
      LOG.debug("Wrong bean type. PageGrid lookup not supported for {}", id);
      throw new IllegalArgumentException(String.format("Wrong bean type. PageGrid lookup not supported for %s", id));
    }

    //Category-Fallback for non augmented products
    if (!id.getCommerceBeanType().equals(CATEGORY)) {
      List<String> breadcrumb = commerceRef.getBreadcrumb();
      if (!breadcrumb.isEmpty()) {
        String lastCategoryExternalId = breadcrumb.get(breadcrumb.size() - 1);
        CommerceId categoryId = CommerceIdBuilder.builder(Vendor.of(commerceSettingsHelper.getVendor(site)), CATALOG, CATEGORY)
                .withExternalId(lastCategoryExternalId)
                .withCatalogAlias(id.getCatalogAlias())
                .build();

        id = categoryId;
      }
    }

    content = externalBreadcrumbContentTreeRelation.getNearestContentForCategory(id, site);
    if (content != null) {
      return content;
    }

    //if no parent available, fallback to site root
    content = site.getSiteRootDocument();
    if (content != null) {
      LOG.debug("Falling back to page grid of site root for {}.", commerceRef.getId());
      return content;
    }

    throw new IllegalArgumentException("cannot find content for " + id);
  }

  private static boolean isCommerceBeanTypSupported(CommerceBeanType commerceBeanType) {
    return commerceBeanType.equals(PRODUCT) || commerceBeanType.equals(CATEGORY);
  }
}
