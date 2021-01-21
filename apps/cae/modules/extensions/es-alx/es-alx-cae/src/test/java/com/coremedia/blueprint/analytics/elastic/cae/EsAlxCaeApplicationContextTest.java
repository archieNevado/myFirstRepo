package com.coremedia.blueprint.analytics.elastic.cae;

import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EsAlxCaeApplicationContextTest.LocalConfig.class)
@WebAppConfiguration
public class EsAlxCaeApplicationContextTest {

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties({
          DeliveryConfigurationProperties.class
  })
  @ImportResource({
          "classpath:/com/coremedia/blueprint/analytics/elastic/cae/EsAlxCaeApplicationContextTest.xml",
          "classpath:/spring/test/dummy-views.xml",
          "classpath:/com/coremedia/cap/common/xml/uapi-xml-services.xml",
  })
  @TestPropertySource(properties = "elastic.core.persistence=memory")
  static class LocalConfig {
  }

  @Test
  public void canLoadApplicationContext() {
    // if control flow ends up here, we're done
  }

}
