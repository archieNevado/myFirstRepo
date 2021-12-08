package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapterFactory;
import com.coremedia.blueprint.base.livecontext.augmentation.AugmentationAutoConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasMappingProvider;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.internal.PageGridConfiguration;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.caas.augmentation.adapter.AugmentationPageGridAdapterFactoryCmsOnly;
import com.coremedia.blueprint.caas.augmentation.adapter.CommerceRefAdapterCmsOnly;
import com.coremedia.blueprint.caas.augmentation.model.AugmentationContext;
import com.coremedia.blueprint.caas.augmentation.model.AugmentationFacadeCmsOnly;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbContentTreeRelation;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbTreeRelation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.pagegrid.ContentAugmentedPageGridServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
@Import({
        AugmentationAutoConfiguration.class,
})
public class HeadlessAugmentationCmsOnlyConfiguration {

  @Bean
  public AugmentationFacadeCmsOnly augmentationFacadeCmsOnly(AugmentationService categoryAugmentationService,
                                                             AugmentationService productAugmentationService,
                                                             SitesService sitesService,
                                                             ObjectProvider<ExternalBreadcrumbTreeRelation> externalBreadcrumbTreeRelationProvider,
                                                             CommerceSettingsHelper commerceSettingsHelper,
                                                             ByPathAdapterFactory byPathAdapterFactory,
                                                             ObjectProvider<AugmentationContext> augmentationContextProvider, CatalogAliasMappingProvider catalogAliasMappingProvider) {
    return new AugmentationFacadeCmsOnly(categoryAugmentationService, productAugmentationService, sitesService, externalBreadcrumbTreeRelationProvider, commerceSettingsHelper, byPathAdapterFactory, augmentationContextProvider, catalogAliasMappingProvider);
  }

  @Bean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  ExternalBreadcrumbTreeRelation externalBreadcrumbTreeRelation() {
    return new ExternalBreadcrumbTreeRelation(List.of());
  }

  @Bean
  ExternalBreadcrumbContentTreeRelation externalBreadcrumbContentTreeRelation(AugmentationService categoryAugmentationService, ObjectProvider<ExternalBreadcrumbTreeRelation> breadcrumbTreeRelationProvider, SitesService sitesService) {
    return new ExternalBreadcrumbContentTreeRelation(categoryAugmentationService, breadcrumbTreeRelationProvider, sitesService);
  }

  @Bean
  public AugmentationPageGridAdapterFactoryCmsOnly categoryPageGridAdapterDelegateCmsOnly(
          AugmentationService categoryAugmentationService,
          ContentBackedPageGridService categoryContentBackedPageGridServiceCmsOnly,
          SitesService sitesService,
          CommerceEntityHelper commerceEntityHelper,
          ExternalBreadcrumbContentTreeRelation externalBreadcrumbContentTreeRelation,
          CommerceSettingsHelper commerceSettingsHelper) {
    return new AugmentationPageGridAdapterFactoryCmsOnly(
            PAGE_GRID_STRUCT_PROPERTY,
            categoryAugmentationService,
            categoryContentBackedPageGridServiceCmsOnly,
            sitesService,
            externalBreadcrumbContentTreeRelation,
            commerceEntityHelper,
            commerceSettingsHelper);
  }

  @Bean
  public AugmentationPageGridAdapterFactoryCmsOnly productPageGridAdapterDelegateCmsOnly(
          AugmentationService productAugmentationService,
          ContentBackedPageGridService pdpContentBackedPageGridServiceCmsOnly,
          SitesService sitesService,
          CommerceEntityHelper commerceEntityHelper,
          ExternalBreadcrumbContentTreeRelation externalBreadcrumbContentTreeRelation,
          CommerceSettingsHelper commerceSettingsHelper) {
    return new AugmentationPageGridAdapterFactoryCmsOnly(
            PDP_PAGEGRID_PROPERTY_NAME,
            productAugmentationService,
            pdpContentBackedPageGridServiceCmsOnly,
            sitesService,
            externalBreadcrumbContentTreeRelation,
            commerceEntityHelper,
            commerceSettingsHelper);
  }

  @Bean
  public ContentAugmentedPageGridServiceImpl categoryContentBackedPageGridServiceCmsOnly(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration,
          ExternalBreadcrumbContentTreeRelation externalBreadcrumbContentTreeRelation) {
    ContentAugmentedPageGridServiceImpl pageGridService = new ContentAugmentedPageGridServiceImpl();
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setTreeRelation(externalBreadcrumbContentTreeRelation);
    return pageGridService;
  }

  @Bean
  public ContentAugmentedPageGridServiceImpl pdpContentBackedPageGridServiceCmsOnly(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration,
          ExternalBreadcrumbContentTreeRelation externalBreadcrumbContentTreeRelation) {
    ContentAugmentedPageGridServiceImpl pageGridService = new ContentAugmentedPageGridServiceImpl();
    pageGridService.setStructPropertyName(PDP_PAGEGRID_PROPERTY_NAME);
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setTreeRelation(externalBreadcrumbContentTreeRelation);
    pageGridService.setFallbackStructPropertyName(PAGE_GRID_STRUCT_PROPERTY);
    return pageGridService;
  }

  @Bean
  public CommerceSettingsHelper liveContextSettingsHelper(SitesService sitesService, SettingsService settingsService){
    return new CommerceSettingsHelper(sitesService, settingsService);
  }

  @Bean
  public CommerceRefHelper commerceRefHelper(SitesService siteService, CommerceSettingsHelper commerceSettingsHelpder){
    return new CommerceRefHelper(siteService, commerceSettingsHelpder);
  }

  @Bean
  public CommerceRefAdapterCmsOnly commerceRefAdapterDelegateCmsOnly(SitesService sitesService, CommerceSettingsHelper commerceSettingsHelper){
    return new CommerceRefAdapterCmsOnly(sitesService, commerceSettingsHelper);
  }

}
