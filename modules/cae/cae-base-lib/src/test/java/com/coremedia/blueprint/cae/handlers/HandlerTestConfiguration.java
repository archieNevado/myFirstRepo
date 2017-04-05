package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.impl.BaseUriPrepender;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CACHE;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CONTENT_BEAN_FACTORY;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.HANDLERS;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.ID_PROVIDER;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.LINK_FORMATTER;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.VIEW_RESOLVER;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Configuration for handler tests. Requires test class to be annotated with
 * {@link org.springframework.test.context.web.WebAppConfiguration}.
 */
@Configuration
@ImportResource(
        value = {
                CONTENT_BEAN_FACTORY,
                CACHE,
                ID_PROVIDER,
                LINK_FORMATTER,
                VIEW_RESOLVER,
                HANDLERS,
                "classpath:/framework/spring/blueprint-contentbeans.xml",
                "classpath:/framework/spring/blueprint-handlers.xml",
        },
        reader = com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader.class
)
@Import({XmlRepoConfiguration.class, ContentTestConfiguration.class})
public class HandlerTestConfiguration {
  @Bean
  @Scope(SCOPE_SINGLETON)
  BeanPostProcessor prefixLinkPostProcessorPostProcessor() {
    return new BeanPostProcessor() {
      @Override
      public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
      }

      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BaseUriPrepender) {
          ((BaseUriPrepender) bean).setActive(false);
        }
        return bean;
      }
    };
  }

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  LinkFormatterTestHelper linkFormatterTestHelper() {
    return new LinkFormatterTestHelper();
  }

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  RequestTestHelper requestTestHelper() {
    return new RequestTestHelper();
  }

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  MockMvc mockMvc(WebApplicationContext wac) {
    return MockMvcBuilders.webAppContextSetup(wac).build();
  }

}
