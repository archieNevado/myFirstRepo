package com.coremedia.blueprint.headlessserver.cors;

import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.plugin.PluginConfiguration;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.web.filter.GraphQlControllerFilter;
import com.coremedia.caas.wrapper.UrlPathFormater;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.dataloader.CacheMap;
import org.dataloader.DataLoader;
import org.dataloader.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;

@SpringBootTest(classes = {
        CaasConfig.class,
        BlueprintBaseMediaConfig.class,
        GraphQLController.class,
        GraphQlControllerFilter.class,
        PluginConfiguration.class,
        TransformationServiceConfiguration.class,
        CorsCustomConfigTest.LocalTestConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml"
})
@ComponentScan("com.coremedia.caas.web.cors")
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@ActiveProfiles("cors-fallback-config-test")
@ExtendWith(SpringExtension.class)
class CorsFallbackConfigTest {

  // note: this value is set in test application properties of the active profile
  private static String ALLOWED_ORIGIN = "testorigin.vm";

  @Autowired
  private WebTestClient webTestClient;
  @MockBean
  ThemeService themeService;
  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  DataLoader<String, Try<String>> remoteLinkDataLoader;
  @MockBean
  CacheMap remoteLinkCacheMap;

  @BeforeEach
  public void setUp() {
    RestAssuredWebTestClient.webTestClient(webTestClient
            .mutate()
            .baseUrl("/graphql")
            .build()
    );
  }

  @Test
  void testPreflightRequestDefault() {
    given().header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, HttpHeaders.ORIGIN)
            .when()
            .options()
            .then()
            .status(HttpStatus.OK)
            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN)
            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,OPTIONS")
            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.ORIGIN)
            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
  }

  @Test
  @SuppressWarnings("java:S2699") // status forbidden is actually an assertion
  void testPreflightRequestForbiddenOrigin() {
    given().header(HttpHeaders.ORIGIN, "http://invalid-origin.vm")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.ORIGIN)
            .when()
            .options()
            .then()
            .status(HttpStatus.FORBIDDEN);
  }

  @Test
  @SuppressWarnings("java:S2699") // status forbidden is actually an assertion
  void testPreflightRequestForbiddenMethod() {
    given().header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT")
            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.ORIGIN)
            .when()
            .options()
            .then()
            .status(HttpStatus.FORBIDDEN);
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {
    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }
}
