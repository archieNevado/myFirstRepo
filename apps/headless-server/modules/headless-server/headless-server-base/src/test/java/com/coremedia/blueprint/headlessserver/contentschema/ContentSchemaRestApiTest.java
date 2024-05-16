package com.coremedia.blueprint.headlessserver.contentschema;

import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.coderesources.ThemeServiceConfiguration;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.headless_server.plugin_support.extensionpoints.CopyToContextParameter;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.plugin.PluginConfiguration;
import com.coremedia.caas.web.GraphQLRestMappingConfig;
import com.coremedia.caas.web.controller.ViewController;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.web.filter.GraphQlControllerFilter;
import com.coremedia.caas.web.wiring.PreviewDateContextParameter;
import com.coremedia.caas.wrapper.UrlPathFormater;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.dataloader.CacheMap;
import org.dataloader.DataLoader;
import org.dataloader.Try;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
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
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.MASTER_SITE_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.TIME_TRAVEL_ARTICLE_ID;
import static com.coremedia.caas.headless_server.plugin_support.PluginSupport.HTTP_HEADER_NAME_X_PREVIEW_DATE;
import static com.coremedia.caas.headless_server.plugin_support.PluginSupport.QUALIFIER_CAAS_COPY_TO_CONTEXT_PARAMETER;
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
        ThemeServiceConfiguration.class,
        TransformationServiceConfiguration.class,
        JacksonAutoConfiguration.class,
        ContentSchemaRestApiTest.LocalTestConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml",
        "caas.preview=true",
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ContentSchemaRestApiTest {

  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  CacheMap remoteLinkCacheMap;
  @MockBean
  DataLoader<String, Try<String>> remoteLinkDataLoader;

  @Autowired
  private WebTestClient webTestClient;

  @BeforeAll
  public void setUp() {
    RestAssuredWebTestClient.webTestClient(webTestClient
            .mutate()
            .defaultHeader("Content-Type", "application/json")
            .baseUrl("/caas/v1").build());
  }

  @Test
  void testGetSiteById() {
    // Note: As ContentSchemaGraphQLTest already tests in detail, we don't have to do it here. Just check some basics.
    given()
            .when()
            .get("/site/{siteId}", MASTER_SITE_ID).then()
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString())
            .body("site.id", equalTo(MASTER_SITE_ID))
            .body("errors", blankOrNullString());
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "page;111112;CMChannel;200",
          "article;111116;CMArticle;200",
          "picture;111114;CMPicture;200",
          "page;888888;CMChannel;404", // invalid content id
          "article;888888;CMArticle;404", // invalid content id
          "picture;888888;CMPicture;404", // invalid content id
  })
  void testGetEndpointsById(String restEndpoint, String contentId, String expectedDocType, Integer expectedHttpStatus) {
    // mock a remote link for 'page' endpoint
    lenient().when(remoteLinkDataLoader.load(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture(Try.succeeded("dummy")));

    // Note: As ContentSchemaGraphQLTest already tests in detail, we don't have to do it here. Just check some basics.
    if (HttpStatus.resolve(expectedHttpStatus).is2xxSuccessful()) {
      given()
              .when()
              .get("/{restEndpoint}/{contentId}", restEndpoint, contentId).then()
              .status(HttpStatus.OK)
              .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString())
              .body(restEndpoint + ".id", equalTo(contentId))
              .body(restEndpoint + ".type", equalTo(expectedDocType))
              .body("errors", blankOrNullString());
    } else {
      // just check http status
      given()
              .when()
              .get("/{restEndpoint}/{contentId}", restEndpoint, contentId).then()
              .status(HttpStatus.resolve(expectedHttpStatus))
              .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString());
    }
  }

  @Test
  @SuppressWarnings("java:S2699")
    // status assertion from rest-assured is not detected by sonar
  void testGetNoneExistingEndpoint() {
    given()
            .when()
            .get("/notAnEndpoint")
            .then()
            .status(HttpStatus.NOT_FOUND)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString());
  }

  @Test
  @SuppressWarnings("java:S2699")
    // status assertion from rest-assured is not detected by sonar
  void testGetArticleByIdCurrentPreviewDate() {
    given()
            .when()
            .get("/article/" + TIME_TRAVEL_ARTICLE_ID).then()
            .status(HttpStatus.NOT_FOUND)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString());
  }

  @Test
  @SuppressWarnings("java:S2699")
    // status assertion from rest-assured is not detected by sonar
  void testGetArticleByIdWithFuturePreviewDate() {
    given()
            .header(HTTP_HEADER_NAME_X_PREVIEW_DATE, "Fri, 01 Jan 2100 00:00:00 GMT")
            .when()
            .get("/article/" + TIME_TRAVEL_ARTICLE_ID).then()
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString())
            .body("article.id", equalTo(TIME_TRAVEL_ARTICLE_ID.toString()))
            .body("article.type", equalTo("CMArticle"))
            .body("errors", blankOrNullString());
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {
    @Bean
    @Qualifier(QUALIFIER_CAAS_COPY_TO_CONTEXT_PARAMETER)
    public CopyToContextParameter<Long, ZonedDateTime> previewDateContextParameter() {
      return new PreviewDateContextParameter();
    }
  }
}
