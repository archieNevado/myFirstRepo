package com.coremedia.livecontext.ecommerce.ibm.cae.storefront;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.service.StoreFrontConnector;
import com.coremedia.livecontext.ecommerce.ibm.cae.WcsUrlProvider;
import com.coremedia.livecontext.ecommerce.ibm.login.WcLoginWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextProviderImpl;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.handler.LiveContextProductSeoLinkBuilderHelper;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(
        value = "classpath:/framework/spring/livecontext-services.xml",
        reader = ResourceAwareXmlBeanDefinitionReader.class)
public class StoreFrontConfiguration {

  @Value("${livecontext.ibm.wcs.default.url}")
  private String wcsStorefrontUrl;

  @Bean
  LiveContextProductSeoLinkBuilderHelper liveContextProductSeoLinkBuilderHelper() {
    return new LiveContextProductSeoLinkBuilderHelper();
  }

  @Bean
  @Qualifier("pageHandlerUrlProvider")
  WcsUrlProvider wcsPageHandlerUrlProvider(@Value("${livecontext.ibm.wcs.storefront.url}") String defaultStoreFrontUrl,
                                           @Value("${livecontext.ibm.wcs.storefront.preview.url}") String previewStoreFrontUrl,
                                           @Value("${livecontext.ibm.contract.preview.url}") String shoppingFlowUrlForContractPreview,
                                           @Value("${livecontext.ibm.product.non-seo.url}") String productNonSeoUrl,
                                           @Value("${livecontext.ibm.category.non-seo.url}") String categoryNonSeoUrl) {
    WcsUrlProvider wcsUrlProvider = new WcsUrlProvider();
    wcsUrlProvider.setDefaultStoreFrontUrl(defaultStoreFrontUrl);
    wcsUrlProvider.setPreviewStoreFrontUrl(previewStoreFrontUrl);
    wcsUrlProvider.setShoppingFlowUrlForContractPreview(shoppingFlowUrlForContractPreview);
    wcsUrlProvider.setProductNonSeoUrl(productNonSeoUrl);
    wcsUrlProvider.setCategoryNonSeoUrl(categoryNonSeoUrl);
    wcsUrlProvider.setUrlPattern("{language}/{storeName}/{seoSegment}");
    return wcsUrlProvider;
  }

  @Bean
  UserSessionServiceImpl commerceUserSessionService(StoreFrontConnector storeFrontConnector,
                                            WcsUrlProvider wcsPageHandlerUrlProvider,
                                            CommerceCache commerceCache,
                                            WcLoginWrapperService loginWrapperService,
                                            UserService commerceUserService) {
    UserSessionServiceImpl userSessionService = new UserSessionServiceImpl();
    userSessionService.setStoreFrontConnector(storeFrontConnector);
    userSessionService.setCommerceCache(commerceCache);
    userSessionService.setLoginWrapperService(loginWrapperService);
    userSessionService.setUserService(commerceUserService);
    userSessionService.setUrlProvider(wcsPageHandlerUrlProvider);
    userSessionService.setWcsStorefrontUrl(wcsStorefrontUrl);
    return userSessionService;
  }

  @Bean
  UserContextProviderImpl userContextProvider(UserSessionService userSessionService) {
    UserContextProviderImpl userContextProvider = new UserContextProviderImpl();
    userContextProvider.setUserSessionService(userSessionService);
    return userContextProvider;
  }
}
