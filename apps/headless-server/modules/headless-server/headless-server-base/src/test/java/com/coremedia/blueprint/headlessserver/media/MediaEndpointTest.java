package com.coremedia.blueprint.headlessserver.media;

import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.base.caas.wrapper.WrapperConfig;
import com.coremedia.blueprint.coderesources.ThemeServiceConfiguration;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.plugin.PluginConfiguration;
import com.coremedia.caas.web.GraphQLRestMappingConfig;
import com.coremedia.caas.web.controller.MediaController;
import com.coremedia.caas.web.controller.ViewController;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.web.filter.GraphQlControllerFilter;
import com.coremedia.caas.web.monitoring.CaasMetricsConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.dataloader.CacheMap;
import org.dataloader.DataLoader;
import org.dataloader.Try;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;

@SpringBootTest(classes = {
        CaasConfig.class,
        BlueprintBaseMediaConfig.class,
        GraphQLController.class,
        GraphQlControllerFilter.class,
        GraphQLRestMappingConfig.class,
        ViewController.class,
        PluginConfiguration.class,
        ThemeServiceConfiguration.class,
        TransformationServiceConfiguration.class,
        JacksonAutoConfiguration.class,
        CaasMetricsConfig.class,
        MediaController.class,
        WrapperConfig.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml"
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MediaEndpointTest {

  @MockBean
  CacheMap remoteLinkCacheMap;
  @MockBean
  MeterRegistry meterRegistry;
  @MockBean
  DataLoader<String, Try<String>> remoteLinkDataLoader;

  @Autowired
  private WebTestClient webTestClient;

  @BeforeAll
  public void setUp() {
    RestAssuredWebTestClient.webTestClient(webTestClient
            .mutate()
            .baseUrl("/caas/v1/media").build());
  }

  @ParameterizedTest
  @CsvSource(delimiter = ',', value = {
          "111114/data/c85378e69d4f0a3fdd566c5d708ad8dc/,pic16.jpg,image/jpeg",
          "111114/data/c85378e69d4f0a3fdd566c5d708ad8dc/,pic16.jpeg,image/jpeg",
          "111114/data/c85378e69d4f0a3fdd566c5d708ad8dc/testCrop/200/,pic16.png,image/png", // trigger transformation to png (requires crop data)
          "111114/data/c85378e69d4f0a3fdd566c5d708ad8dc/testCrop/200/,pic16.gif,image/gif", // trigger transformation to gif (requires crop data)
          "111120/data/86a2ab8630cd785234f3b1cefbf07346/,pdf20.pdf,application/pdf",
  })
  void testQualifiedDownload(String uri, String fileName, String contentType) {
    given()
            .when()
            .get(uri + fileName)
            .then()
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(fileName).build().toString())
            .header(HttpHeaders.CONTENT_TYPE, contentType);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ',', value = {
          "111114/data/c85378e69d4f0a3fdd566c5d708ad8dc,image/jpeg",
          "111114/data/c85378e69d4f0a3fdd566c5d708ad8dc/testCrop/200,image/jpeg",
          "markup/111122/data/712e862264a0e0bda5f6cff85f131826,text/html; charset=UTF-8",
  })
  void testBlindDownload(String uri, String contentType) {
    // developer note: 'blind' refers to the request url w/o any filename
    given()
            .when()
            .get(uri)
            .then()
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString())
            .header(HttpHeaders.CONTENT_TYPE, contentType);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ',', value = {
          "no-endpoint",
          "markup/12341234",
          "markup/12341234/data",
          "markup/12341234/data/mocked-hash",
          "markup/111122",
          "markup/111122/data",
          "markup/111122/data/wrong-hash",
          "12341234",
          "12341234/data",
          "12341234/data/mocked-hash",
          "111114",
          "111114/data",
          "111114/data/wrong-hash",
  })
  void testNotFound(String uri) {
    given()
            .when()
            .get(uri)
            .then()
            .status(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ',', value = {
          "111114/data/c85378e69d4f0a3fdd566c5d708ad8dc/testCrop/200/pic16.webp", // trigger an unsupported transformation to webp
  })
  void testBadRequest(String uri) {
    given()
            .when()
            .get(uri)
            .then()
            .status(HttpStatus.BAD_REQUEST);
  }
}
