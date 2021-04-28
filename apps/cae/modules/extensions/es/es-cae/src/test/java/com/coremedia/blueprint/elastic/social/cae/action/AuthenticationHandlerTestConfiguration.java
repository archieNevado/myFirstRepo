package com.coremedia.blueprint.elastic.social.cae.action;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.cae.configuration.BlueprintPageCaeContentBeansConfiguration;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.objectserver.beans.ContentBeanFactory;
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
                                              NavigationSegmentsUriHelper navigationSegmentsUriHelper,
                                              ContentBeanFactory contentBeanFactory,
                                              ContentLinkBuilder contentLinkBuilder,
                                              ContextHelper contextHelper,
                                              UrlPathFormattingHelper urlPathFormattingHelper,
                                              SitesService sitesService) {
    AuthenticationHandler testling = new AuthenticationHandler();
    testling.setBeanFactory(beanFactory);
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    testling.setContextHelper(contextHelper);
    testling.setUrlPathFormattingHelper(urlPathFormattingHelper);
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setSitesService(sitesService);
    testling.setContentLinkBuilder(contentLinkBuilder);
    return testling;
  }

}
