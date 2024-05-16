package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapterFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasMappingProvider;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.internal.PageGridConfiguration;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.caas.augmentation.adapter.AugmentationPageGridAdapterFactoryCmsOnly;
import com.coremedia.blueprint.caas.augmentation.adapter.CommerceRefAdapterCmsOnly;
import com.coremedia.blueprint.caas.augmentation.connection.CmsOnlyCommerceConnectionFinder;
import com.coremedia.blueprint.caas.augmentation.model.AugmentationContext;
import com.coremedia.blueprint.caas.augmentation.model.AugmentationFacadeCmsOnly;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbContentTreeRelationFactory;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbContentTreeRelationUtil;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbTreeRelation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.pagegrid.ContentAugmentedPageGridServiceImpl;
import com.coremedia.livecontext.pagegrid.ContentAugmentedProductPageGridServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords.PAGE_GRID_STRUCT_PROPERTY;
import static com.coremedia.blueprint.caas.augmentation.adapter.AugmentationPageGridAdapterFactory.PDP_PAGEGRID_PROPERTY_NAME;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        CaasAssetSearchServiceConfigProperties.class,
})
public class HeadlessAugmentationCmsOnlyConfiguration {

  @Bean
  public AugmentationFacadeCmsOnly augmentationFacadeCmsOnly(AugmentationService categoryAugmentationService,
                                                             AugmentationService productAugmentationService,
                                                             SitesService sitesService,
                                                             CommerceSettingsHelper commerceSettingsHelper,
                                                             ByPathAdapterFactory byPathAdapterFactory,
                                                             ObjectProvider<AugmentationContext> augmentationContextProvider, CatalogAliasMappingProvider catalogAliasMappingProvider) {
    return new AugmentationFacadeCmsOnly(categoryAugmentationService, productAugmentationService, sitesService, commerceSettingsHelper, byPathAdapterFactory, augmentationContextProvider, catalogAliasMappingProvider);
  }

  @Bean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  ExternalBreadcrumbTreeRelation externalBreadcrumbTreeRelation() {
    return new ExternalBreadcrumbTreeRelation(List.of());
  }

  @Bean
  ExternalBreadcrumbContentTreeRelationFactory externalBreadcrumbContentTreeRelationFactory(AugmentationService categoryAugmentationService,
                                                                                            SitesService sitesService) {
    return new ExternalBreadcrumbContentTreeRelationFactory(categoryAugmentationService, sitesService);
  }

  @Bean
  public AugmentationPageGridAdapterFactoryCmsOnly categoryPageGridAdapterDelegateCmsOnly(
          AugmentationService categoryAugmentationService,
          ContentBackedPageGridService categoryContentBackedPageGridServiceCmsOnly,
          SitesService sitesService,
          ExternalBreadcrumbContentTreeRelationFactory externalBreadcrumbContentTreeRelationFactory,
          CommerceSettingsHelper commerceSettingsHelper) {
    return new AugmentationPageGridAdapterFactoryCmsOnly(
            PAGE_GRID_STRUCT_PROPERTY,
            categoryAugmentationService,
            categoryContentBackedPageGridServiceCmsOnly,
            sitesService,
            externalBreadcrumbContentTreeRelationFactory,
            commerceSettingsHelper);
  }

  @Bean
  public AugmentationPageGridAdapterFactoryCmsOnly productPageGridAdapterDelegateCmsOnly(
          AugmentationService productAugmentationService,
          ContentBackedPageGridService pdpContentBackedPageGridServiceCmsOnly,
          SitesService sitesService,
          ExternalBreadcrumbContentTreeRelationFactory externalBreadcrumbContentTreeRelationFactory,
          CommerceSettingsHelper commerceSettingsHelper) {
    return new AugmentationPageGridAdapterFactoryCmsOnly(
            PDP_PAGEGRID_PROPERTY_NAME,
            productAugmentationService,
            pdpContentBackedPageGridServiceCmsOnly,
            sitesService,
            externalBreadcrumbContentTreeRelationFactory,
            commerceSettingsHelper);
  }

  @Bean
  public ContentBackedPageGridService categoryContentBackedPageGridServiceCmsOnly(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration) {
    ContentAugmentedPageGridServiceImpl pageGridService = new ContentAugmentedPageGridServiceImpl();
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setFallbackStructPropertyName(PAGE_GRID_STRUCT_PROPERTY);
    pageGridService.setRootCategoryContentSupplier(ExternalBreadcrumbContentTreeRelationUtil::getContentForRootCategory);
    return pageGridService;
  }

  @Bean
  public ContentBackedPageGridService pdpContentBackedPageGridServiceCmsOnly(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration) {
    ContentAugmentedProductPageGridServiceImpl pageGridService = new ContentAugmentedProductPageGridServiceImpl();
    pageGridService.setStructPropertyName(PDP_PAGEGRID_PROPERTY_NAME);
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setFallbackStructPropertyName(PAGE_GRID_STRUCT_PROPERTY);
    pageGridService.setNearestCategoryContentSupplier(ExternalBreadcrumbContentTreeRelationUtil::getNearestContentForLeafCategory);
    return pageGridService;
  }

  @Bean
  public CommerceSettingsHelper liveContextSettingsHelper(SettingsService settingsService) {
    return new CommerceSettingsHelper(settingsService);
  }

  @Bean
  public CommerceRefHelper commerceRefHelper(SitesService siteService, CommerceSettingsHelper commerceSettingsHelpder) {
    return new CommerceRefHelper(siteService, commerceSettingsHelpder);
  }

  @Bean
  public CommerceRefAdapterCmsOnly commerceRefAdapterDelegateCmsOnly(SitesService sitesService, CommerceSettingsHelper commerceSettingsHelper) {
    return new CommerceRefAdapterCmsOnly(sitesService, commerceSettingsHelper);
  }

  @Bean
  public CmsOnlyCommerceConnectionFinder cmsOnlyCommerceConnectionFinder(CommerceSettingsHelper commerceSettingsHelper) {
    // fallback: look for connection after generic commerce connection
    return new CmsOnlyCommerceConnectionFinder(commerceSettingsHelper);
  }

}
