package com.coremedia.livecontext.ecommerce.sfcc.configuration;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.SpringCommerceBeanFactory;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.sfcc.asset.AssetUrlProviderImpl;
import com.coremedia.livecontext.ecommerce.sfcc.beans.AbstractSfccCommerceBean;
import com.coremedia.livecontext.ecommerce.sfcc.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceConnection;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccStoreContextProvider;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCAPIConnector;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CategoryProductAssignmentSearchResource;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductSearchResource;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ProductsResource;
import com.coremedia.livecontext.ecommerce.sfcc.pricing.PriceServiceImpl;
import com.coremedia.livecontext.ecommerce.sfcc.user.UserContextProviderImpl;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import javax.annotation.Nonnull;

@Configuration
@PropertySource(value = {
        "classpath:/com/coremedia/livecontext/ecommerce/sfcc/configuration/lc-ecommerce-sfcc.properties"
})
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
class LcEcommerce_SFCC_Configuration {

  @Bean
  CatalogServiceImpl sfccCatalogService(@Nonnull com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductsResource productsResource,
                                        @Nonnull CategoryProductAssignmentSearchResource categoryProductAssignmentSearchResource,
                                        @Nonnull ProductSearchResource productSearchResource,
                                        @Nonnull CommerceCache commerceCache,
                                        @Nonnull CommerceBeanFactory sfccCommerceBeanFactory) {
    return new CatalogServiceImpl(productsResource, categoryProductAssignmentSearchResource, productSearchResource, commerceCache, sfccCommerceBeanFactory);
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
  SfccStoreContextProvider sfccStoreContextProvider(@Nonnull SettingsService settingsService,
                                                    @Nonnull SitesService sitesService,
                                                    @Nonnull Cache cache) {
    SfccStoreContextProvider storeContextProvider = new SfccStoreContextProvider();
    storeContextProvider.setSettingsService(settingsService);
    storeContextProvider.setSitesService(sitesService);
    storeContextProvider.setCache(cache);
    return storeContextProvider;
  }

  @Bean
  CommerceBeanFactory sfccCommerceBeanFactory(@Nonnull StoreContextProvider sfccStoreContextProvider) {
    SpringCommerceBeanFactory springCommerceBeanFactory = new SpringCommerceBeanFactory();
    springCommerceBeanFactory.setStoreContextProvider(sfccStoreContextProvider);
    return springCommerceBeanFactory;
  }

  @Bean("commerce:sfcc1")
  @Scope(ConfigurableListableBeanFactory.SCOPE_PROTOTYPE)
  SfccCommerceConnection sfccCommerceConnection(@Nonnull AssetUrlProviderImpl sfccAssetUrlProvider,
                                                @Nonnull SfccStoreContextProvider sfccStoreContextProvider,
                                                @Nonnull UserContextProviderImpl sfccUserContextProvider,
                                                @Nonnull CatalogService sfccCatalogService,
                                                @Nonnull SfccCommerceIdProvider sfccCommerceIdProvider,
                                                @Nonnull CommerceBeanFactory sfccCommerceBeanFactory,
                                                @Nonnull SfccConfigurationProperties sfccConfigurationProperties) {
    SfccCommerceConnection connection = new SfccCommerceConnection(sfccConfigurationProperties);
    connection.setAssetUrlProvider(sfccAssetUrlProvider);
    connection.setStoreContextProvider(sfccStoreContextProvider);
    connection.setUserContextProvider(sfccUserContextProvider);
    connection.setCatalogService(sfccCatalogService);
    connection.setIdProvider(sfccCommerceIdProvider);
    connection.setCommerceBeanFactory(sfccCommerceBeanFactory);
    return connection;
  }

}
