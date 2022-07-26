package com.coremedia.blueprint.headlessserver.contentschema;

import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.media.TransformationService;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.plugin.PluginConfiguration;
import com.coremedia.caas.web.GraphQLRestMappingConfig;
import com.coremedia.caas.web.controller.ViewController;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.web.filter.GraphQlControllerFilter;
import com.coremedia.caas.wrapper.UrlPathFormater;
import com.coremedia.image.ImageDimensionsExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.dataloader.CacheMap;
import org.dataloader.DataLoader;
import org.dataloader.Try;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.lenient;

@SpringBootTest(classes = {
        CaasConfig.class,
        BlueprintBaseMediaConfig.class,
        GraphQLController.class,
        GraphQlControllerFilter.class,
        GraphQLRestMappingConfig.class,
        ViewController.class,
        PluginConfiguration.class,
        TransformationServiceConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml"
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@ContextConfiguration(classes = ContentSchemaPersistedQueriesTest.LocalTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ContentSchemaPersistedQueriesTest {

  @MockBean
  TransformationService transformationService;
  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  CacheMap remoteLinkCacheMap;
  @MockBean
  ThemeService themeService;
  @MockBean
  ImageDimensionsExtractor imageDimensionsExtractor;
  @MockBean
  DataLoader<String, Try<String>> remoteLinkDataLoader;

  @Autowired
  private WebTestClient webTestClient;

  @BeforeAll
  public void setUp() {
    RestAssuredWebTestClient.webTestClient(webTestClient
            .mutate()
            .defaultHeader("Content-Type", "application/json")
            .baseUrl("/graphql").build());
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "article;id;111116;data.content.article;false",
          "page;id;111112;data.content.page;false",
          "picture;id;111114;data.content.picture;false",
          "site;siteId;the-site-id;data.content.site;false",
          "article;id;888888;data.content.article;true",
          "page;id;888888;data.content.page;true",
          "picture;id;888888;data.content.picture;true",
          "site;siteId;invalid-site-id;data.content.site;true",
  })
  void testGetContentByIdViaPersistedQuery(String persistedQueryName, String varName, String contentId, String jsonPathPrefix, boolean expectEmptyResponse) {
    // mock a remote link for 'page' persisted query
    lenient().when(remoteLinkDataLoader.load(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture(Try.succeeded("dummy")));

    if (expectEmptyResponse) {
      given()
              .body(createJsonPostBody(persistedQueryName, Map.of(varName, contentId)))
              .when()
              .post()
              .then()
              .status(HttpStatus.OK)
              .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString())
              .body(jsonPathPrefix, blankOrNullString())
              .body("errors", blankOrNullString());
    } else {
      // Note: As ContentSchemaGraphQLTest already tests in detail, we don't have to do it here. Just check id.
      given()
              .body(createJsonPostBody(persistedQueryName, Map.of(varName, contentId)))
              .when().post().then()
              .status(HttpStatus.OK)
              .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString())
              .body(jsonPathPrefix + ".id", equalTo(contentId))
              .body("errors", blankOrNullString());
    }
  }

  private Map<String, Object> createJsonPostBody(String persistedQueryName, Map<String, Object> variables) {
    return Map.of("id", persistedQueryName, "variables", variables);
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {
    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }
}
