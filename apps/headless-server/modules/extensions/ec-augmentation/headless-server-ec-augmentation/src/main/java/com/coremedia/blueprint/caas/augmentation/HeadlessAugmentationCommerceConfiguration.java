package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.livecontext.augmentation.AugmentationAutoConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesAutoConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceSiteFinder;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.internal.PageGridConfiguration;
import com.coremedia.blueprint.caas.augmentation.adapter.AugmentationPageGridAdapterFactory;
import com.coremedia.blueprint.caas.augmentation.adapter.CommerceRefAdapter;
import com.coremedia.blueprint.caas.augmentation.model.AssetFacade;
import com.coremedia.blueprint.caas.augmentation.model.AugmentationFacade;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.pagegrid.ContentAugmentedPageGridServiceImpl;
import com.coremedia.livecontext.pagegrid.ContentAugmentedProductPageGridServiceImpl;
import com.coremedia.livecontext.tree.ExternalChannelContentTreeRelation;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import static com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords.PAGE_GRID_STRUCT_PROPERTY;
import static com.coremedia.blueprint.caas.augmentation.adapter.AugmentationPageGridAdapterFactory.PDP_PAGEGRID_PROPERTY_NAME;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        CaasAssetSearchServiceConfigProperties.class,
})
@Import({
        AugmentationAutoConfiguration.class,
})
@ImportResource(value = {
        "classpath:/META-INF/coremedia/lc-services.xml",
        "classpath:com/coremedia/blueprint/base/pagegrid/impl/bpbase-pagegrid-services.xml"},
        reader = ResourceAwareXmlBeanDefinitionReader.class)
@PropertySource("classpath:/META-INF/coremedia/headless-server-ec-augmentation-defaults.properties")
@AutoConfigureAfter({BaseCommerceServicesAutoConfiguration.class})
public class HeadlessAugmentationCommerceConfiguration {

  @Bean
  public AssetFacade assetFacade(AssetService assetService,
                                 CommerceRefHelper commerceEntityHelper) {
    return new AssetFacade(assetService, commerceEntityHelper);
  }

  @Bean
  public AugmentationFacade augmentationFacade(AugmentationService categoryAugmentationService,
                                               AugmentationService productAugmentationService,
                                               SitesService sitesService,
                                               CommerceEntityHelper commerceEntityHelper,
                                               CatalogAliasTranslationService catalogAliasTranslationService,
                                               CommerceSiteFinder commerceSiteFinder) {
    return new AugmentationFacade(categoryAugmentationService, productAugmentationService, sitesService,
            commerceEntityHelper, catalogAliasTranslationService, commerceSiteFinder);
  }

  @Bean
  public CommerceEntityHelper commerceEntityHelper(SitesService siteService,
                                                   CommerceConnectionSupplier CommerceConnectionSupplier) {
    return new CommerceEntityHelper(siteService, CommerceConnectionSupplier);
  }

  @Bean
  public AugmentationPageGridAdapterFactory categoryPageGridAdapterDelegate(
          AugmentationService categoryAugmentationService,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation,
          ContentBackedPageGridService categoryContentBackedPageGridService,
          SitesService sitesService, CommerceEntityHelper commerceEntityHelper) {
    return new AugmentationPageGridAdapterFactory(
            PAGE_GRID_STRUCT_PROPERTY,
            categoryAugmentationService,
            externalChannelContentTreeRelation,
            categoryContentBackedPageGridService,
            sitesService, commerceEntityHelper);
  }

  @Bean
  public AugmentationPageGridAdapterFactory productPageGridAdapterDelegate(
          AugmentationService productAugmentationService,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation,
          ContentBackedPageGridService productContentBackedPageGridService,
          SitesService sitesService, CommerceEntityHelper commerceEntityHelper) {
    return new AugmentationPageGridAdapterFactory(
            PDP_PAGEGRID_PROPERTY_NAME,
            productAugmentationService,
            externalChannelContentTreeRelation,
            productContentBackedPageGridService,
            sitesService, commerceEntityHelper);
  }

  @Bean
  public ContentAugmentedPageGridServiceImpl categoryContentBackedPageGridService(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation) {
    ContentAugmentedPageGridServiceImpl pageGridService = new ContentAugmentedPageGridServiceImpl();
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setTreeRelation(externalChannelContentTreeRelation);
    return pageGridService;
  }

  @Bean
  public ContentAugmentedPageGridServiceImpl pdpContentBackedPageGridService(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation) {
    ContentAugmentedPageGridServiceImpl pageGridService = new ContentAugmentedPageGridServiceImpl();
    pageGridService.setStructPropertyName(PDP_PAGEGRID_PROPERTY_NAME);
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setTreeRelation(externalChannelContentTreeRelation);
    pageGridService.setFallbackStructPropertyName(PAGE_GRID_STRUCT_PROPERTY);
    return pageGridService;
  }

  @Bean
  public ContentAugmentedProductPageGridServiceImpl productContentBackedPageGridService(
          Cache cache,
          SitesService sitesService,
          PageGridConfiguration pageGridConfiguration,
          ExternalChannelContentTreeRelation externalChannelContentTreeRelation) {
    ContentAugmentedProductPageGridServiceImpl pageGridService = new ContentAugmentedProductPageGridServiceImpl();
    pageGridService.setStructPropertyName(PDP_PAGEGRID_PROPERTY_NAME);
    pageGridService.setCache(cache);
    pageGridService.setSitesService(sitesService);
    pageGridService.setConfiguration(pageGridConfiguration);
    pageGridService.setTreeRelation(externalChannelContentTreeRelation);
    pageGridService.setFallbackStructPropertyName(PAGE_GRID_STRUCT_PROPERTY);
    return pageGridService;
  }

  @Bean
  public CommerceRefAdapter commerceRefAdapterDelegate(SitesService sitesService, CommerceEntityHelper commerceEntityHelper, CatalogAliasTranslationService catalogAliasTranslationService){
    return new CommerceRefAdapter(sitesService, commerceEntityHelper, catalogAliasTranslationService);
  }
}
