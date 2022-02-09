package com.coremedia.blueprint.component.cae.corporate;

import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith({SpringExtension.class})
@SpringBootTest(properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/com/coremedia/testing/contenttest.xml"
}, classes = {
        PropertyPlaceholderAutoConfiguration.class,
        CorporateCaeApplicationContextTest.LocalConfig.class,
})
class CorporateCaeApplicationContextTest {

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties({
          DeliveryConfigurationProperties.class
  })
  @ImportResource(value = {
          "classpath:/META-INF/coremedia/component-corporate.xml",
  }, reader = ResourceAwareXmlBeanDefinitionReader.class)
  static class LocalConfig {

    @Bean // CAE provides a list bean named 'handlerInterceptors'
    List<?> handlerInterceptors() {
      return List.of();
    }
  }

  @Test
  void canLoadApplicationContext() {
    // if control flow ends up here, we're done
  }
}
