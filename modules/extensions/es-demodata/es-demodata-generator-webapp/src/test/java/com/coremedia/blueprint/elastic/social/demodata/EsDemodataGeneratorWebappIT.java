package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.springframework.component.ComponentLoaderInitializer;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.restassured.RestAssured.given;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        EsDemodataGeneratorWebappIT.DemoDataGeneratorConfiguration.class,
        com.coremedia.elastic.core.memory.models.CoreMemoryModelConfiguration.class,
        XmlRepoConfiguration.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, value = {
        "elastic.solr.lazyIndexCreation=true",
        "elastic.core.persistence=memory",
        "logging.level.com.coremedia.blueprint.elastic.social.demodata=TRACE",
        "logging.level.root=WARN"
})
@ContextConfiguration(initializers = ComponentLoaderInitializer.class)
@ActiveProfiles("disableDataViews")
public class EsDemodataGeneratorWebappIT {

  @LocalServerPort
  private int port;

  @Before
  public void setUp() {
    RestAssured.port = port;
    RestAssured.basePath = "/servlet/generate";
  }

  @Test
  public void getStatus() {
    given().queryParam("status", "ignored")
            .when()
              .get().
            then()
              .statusCode(HttpStatus.OK.value());
  }

  @Configuration
  static class DemoDataGeneratorConfiguration {

    @Bean
    EmbeddedServletContainerFactory tomcat() {
      return new TomcatEmbeddedServletContainerFactory(0);
    }

    @Bean
    XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml");
    }

  }
}
