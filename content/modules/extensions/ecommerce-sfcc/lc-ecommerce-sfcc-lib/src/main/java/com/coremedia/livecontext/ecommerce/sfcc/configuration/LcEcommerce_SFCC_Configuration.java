package com.coremedia.livecontext.ecommerce.sfcc.configuration;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultCommerceConnectionFinder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.SpringCommerceBeanFactory;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.id.IdProvider;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.push.PushService;
import com.coremedia.livecontext.ecommerce.push.SyncStatusStrategy;
import com.coremedia.livecontext.ecommerce.sfcc.asset.AssetUrlProviderImpl;
import com.coremedia.livecontext.ecommerce.sfcc.beans.AbstractSfccCommerceBean;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceConnection;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccStoreContextProvider;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCAPIConnector;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CatalogsResource;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CategoryProductAssignmentSearchResource;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CustomerGroupsResource;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.LibrariesResource;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductSearchResource;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductsResource;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ShopProductSearchResource;
import com.coremedia.livecontext.ecommerce.sfcc.p13n.SegmentServiceImpl;
import com.coremedia.livecontext.ecommerce.sfcc.push.FetchContentUrlHelper;
import com.coremedia.livecontext.ecommerce.sfcc.push.DefaultSyncStatusStrategy;
import com.coremedia.livecontext.ecommerce.sfcc.push.PushServiceImpl;
import com.coremedia.livecontext.ecommerce.sfcc.push.SfccContentHelper;
import com.coremedia.livecontext.ecommerce.sfcc.user.UserContextProviderImpl;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;

@Configuration
@ImportResource(reader = ResourceAwareXmlBeanDefinitionReader.class,
        value = {
                "classpath:/framework/spring/livecontext-cache.xml",
                "classpath:framework/spring/lc-ecommerce-connection.xml"
        }
)
@ComponentScan(basePackageClasses = {
        AbstractSfccCommerceBean.class,
        AbstractOCAPIConnector.class
})
@EnableConfigurationProperties(SfccConfigurationProperties.class)
public class LcEcommerce_SFCC_Configuration {

  @Bean
  CatalogServiceImpl sfccCatalogService(@NonNull CatalogsResource catalogsResource,
                                        @NonNull ProductsResource productsResource,
                                        @NonNull CategoryProductAssignmentSearchResource categoryProductAssignmentSearchResource,
                                        @NonNull ProductSearchResource productSearchResource,
                                        @NonNull ShopProductSearchResource shopProductSearchResource,
                                        @NonNull CommerceCache commerceCache,
                                        @NonNull CommerceBeanFactory sfccCommerceBeanFactory) {
    return new CatalogServiceImpl(catalogsResource, productsResource, categoryProductAssignmentSearchResource,
            productSearchResource, shopProductSearchResource, commerceCache, sfccCommerceBeanFactory);
  }

  @Bean
  AssetUrlProviderImpl sfccAssetUrlProvider() {
    return new AssetUrlProviderImpl();
  }

  @Bean
  UserContextProviderImpl sfccUserContextProvider() {
    return new UserContextProviderImpl();
  }

  @Bean
  SfccCommerceIdProvider sfccCommerceIdProvider() {
    return new SfccCommerceIdProvider();
  }

  @Bean
  SfccStoreContextProvider sfccStoreContextProvider(@NonNull DefaultCommerceConnectionFinder commerceConnectionFinder,
                                                    @NonNull SettingsService settingsService,
                                                    @NonNull SitesService sitesService,
                                                    @NonNull Cache cache) {
    SfccStoreContextProvider storeContextProvider = new SfccStoreContextProvider(commerceConnectionFinder);
    storeContextProvider.setSettingsService(settingsService);
    storeContextProvider.setSitesService(sitesService);
    storeContextProvider.setCache(cache);
    return storeContextProvider;
  }

  @Bean
  SegmentServiceImpl sfccSegmentService(@NonNull CustomerGroupsResource customerGroupsResource,
                                        @NonNull CommerceBeanFactory sfccCommerceBeanFactory,
                                        @NonNull CommerceCache commerceCache) {
    return new SegmentServiceImpl(customerGroupsResource, sfccCommerceBeanFactory, commerceCache);
  }


  @Bean
  PushServiceImpl sfccPushService(@NonNull LibrariesResource resource,
                                  @NonNull IdProvider idProvider,
                                  @NonNull SfccContentHelper sfccContentHelper,
                                  @NonNull FetchContentUrlHelper fetchContentUrlHelper,
                                  @NonNull SyncStatusStrategy syncStatusStrategy,
                                  @NonNull Cache cache) {
    return new PushServiceImpl(resource, idProvider, sfccContentHelper, fetchContentUrlHelper, syncStatusStrategy, cache);
  }

  @Bean
  SfccContentHelper sfccContentHelper(@NonNull LibrariesResource resource,
                                      @NonNull IdProvider idProvider,
                                      @NonNull FetchContentUrlHelper fetchContentUrlHelper,
                                      @NonNull Cache cache
                                      ) {
    return new SfccContentHelper(resource, idProvider, fetchContentUrlHelper, cache);
  }

  @Bean
  FetchContentUrlHelper fetchContentUrlHelper(@NonNull @Value("${studio.previewUrlPrefix}") String previewUrlPrefix,
                                              @NonNull IdProvider idProvider) {
    return new FetchContentUrlHelper(previewUrlPrefix, idProvider);
  }

  @Bean
  DefaultSyncStatusStrategy syncStatusStrategy() {
    return new DefaultSyncStatusStrategy();
  }

  @Bean
  CommerceBeanFactory sfccCommerceBeanFactory(@NonNull StoreContextProvider sfccStoreContextProvider) {
    SpringCommerceBeanFactory springCommerceBeanFactory = new SpringCommerceBeanFactory();
    springCommerceBeanFactory.setStoreContextProvider(sfccStoreContextProvider);
    return springCommerceBeanFactory;
  }

  @Bean("commerce:sfcc1")
  @Scope(ConfigurableListableBeanFactory.SCOPE_PROTOTYPE)
  SfccCommerceConnection sfccCommerceConnection(@NonNull AssetUrlProviderImpl sfccAssetUrlProvider,
                                                @NonNull SfccStoreContextProvider sfccStoreContextProvider,
                                                @NonNull UserContextProviderImpl sfccUserContextProvider,
                                                @NonNull CatalogService sfccCatalogService,
                                                @NonNull SfccCommerceIdProvider sfccCommerceIdProvider,
                                                @NonNull CommerceBeanFactory sfccCommerceBeanFactory,
                                                @NonNull SegmentService sfccSegmentService,
                                                @NonNull PushService sfccPushService,
                                                @NonNull SfccConfigurationProperties sfccConfigurationProperties) {
    SfccCommerceConnection connection = new SfccCommerceConnection(sfccConfigurationProperties);
    connection.setAssetUrlProvider(sfccAssetUrlProvider);
    connection.setStoreContextProvider(sfccStoreContextProvider);
    connection.setUserContextProvider(sfccUserContextProvider);
    connection.setCatalogService(sfccCatalogService);
    connection.setIdProvider(sfccCommerceIdProvider);
    connection.setCommerceBeanFactory(sfccCommerceBeanFactory);
    connection.setSegmentService(sfccSegmentService);
    connection.setPushService(sfccPushService);
    return connection;
  }
}
