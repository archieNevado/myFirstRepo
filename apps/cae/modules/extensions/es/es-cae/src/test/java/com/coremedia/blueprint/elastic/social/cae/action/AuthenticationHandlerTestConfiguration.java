package com.coremedia.blueprint.elastic.social.cae.action;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.configuration.BlueprintPageCaeContentBeansConfiguration;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.i18n.ResourceBundleInterceptor;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.elastic.social.cae.user.PasswordNeverExpiresPolicy;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.cache.Cache;
import com.coremedia.cae.webflow.FlowRunner;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration(proxyBeanMethods = false)
@ImportResource(
        value = {
                "classpath:/framework/spring/blueprint-handlers.xml",
        },
        reader = com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader.class
)
@Import({
        BlueprintPageCaeContentBeansConfiguration.class,
        ContentTestConfiguration.class,
})
@EnableWebMvc
class AuthenticationHandlerTestConfiguration {

  @Bean
  public XmlUapiConfig xmlUapiConfig() {
    return new XmlUapiConfig("classpath:/com/coremedia/blueprint/elastic/social/cae/action/content.xml");
  }

  @Bean
  MockMvc mockMvc(WebApplicationContext wac) {
    return MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Bean
  AuthenticationHandler authenticationHandler(BeanFactory beanFactory,
                                              Cache cache,
                                              NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                              ContentBeanFactory contentBeanFactory,
                                              ContentLinkBuilder contentLinkBuilder,
                                              ContextHelper contextHelper,
                                              UrlPathFormattingHelper urlPathFormattingHelper,
                                              SitesService sitesService,
                                              LinkFormatter linkFormatter,
                                              SettingsService settingsService,
                                              FlowRunner flowRunner,
                                              ResourceBundleInterceptor resourceBundleInterceptor,
                                              DataViewFactory dataviewFactory,
                                              MimeTypeService mimeTypeService) {
    AuthenticationHandler testling = new AuthenticationHandler();
    testling.setBeanFactory(beanFactory);
    testling.setCache(cache);
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    testling.setContextHelper(contextHelper);
    testling.setUrlPathFormattingHelper(urlPathFormattingHelper);
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setSitesService(sitesService);
    testling.setContentLinkBuilder(contentLinkBuilder);
    testling.setLinkFormatter(linkFormatter);
    testling.setPasswordExpiryPolicy(new PasswordNeverExpiresPolicy());
    testling.setSettingsService(settingsService);
    testling.setFlowRunner(flowRunner);
    testling.setResourceBundleInterceptor(resourceBundleInterceptor);
    testling.setDataViewFactory(dataviewFactory);
    testling.setMimeTypeService(mimeTypeService);
    return testling;
  }

}
