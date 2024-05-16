package com.coremedia.blueprint.headlessserver.contentschema;

import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.coderesources.ThemeServiceConfiguration;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.plugin.PluginConfiguration;
import com.coremedia.caas.web.GraphQLRestMappingConfig;
import com.coremedia.caas.web.controller.ViewController;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.web.filter.GraphQlControllerFilter;
import com.coremedia.caas.wrapper.UrlPathFormater;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.dataloader.CacheMap;
import org.dataloader.DataLoader;
import org.dataloader.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.given;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;

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
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml"
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AutomaticPersistedQueriesTest {

  private static String AUTOMATIC_PERSISTED_TEST_QUERY = "{__typename}";

  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  CacheMap remoteLinkCacheMap;
  @MockBean
  DataLoader<String, Try<String>> remoteLinkDataLoader;

  @Autowired
  private WebTestClient webTestClient;

  @BeforeEach
  public void setUp() {
    RestAssuredWebTestClient.webTestClient(webTestClient
            .mutate()
            .defaultHeader("Content-Type", "application/json")
            .baseUrl("/graphql").build());
  }

  @Test
  void testPersistedQueryNotFound() {
    given()
            .body(createAutomaticPersistedQueryByHash(DigestUtils.sha256Hex(AUTOMATIC_PERSISTED_TEST_QUERY)))
            .when()
            .post()
            .then()
            .status(HttpStatus.OK)
            .body("errors[0].message", equalTo("PersistedQueryNotFound"));
  }

  @Test
  void testCreateAnInvokePersistedQuery() {
    // create and persist (and invoke) the query at the server
    given()
            .body(createAutomaticPersistedQueryFromQuery(AUTOMATIC_PERSISTED_TEST_QUERY))
            .when()
            .post()
            .then()
            .status(HttpStatus.OK)
            .body("errors", blankOrNullString())
            .body("data.__typename", equalTo("Query"));

    // just send the hash for invocation of the previously created persisted query
    given()
            .body(createAutomaticPersistedQueryByHash(DigestUtils.sha256Hex(AUTOMATIC_PERSISTED_TEST_QUERY)))
            .when()
            .post()
            .then()
            .status(HttpStatus.OK)
            .body("errors", blankOrNullString())
            .body("data.__typename", equalTo("Query"));
  }

  private Map<String, Object> createAutomaticPersistedQueryByHash(String sha256Hash) {
    return Map.of(
            "extensions", Map.of("persistedQuery",
                    Map.of(
                            "version", 1,
                            "sha256Hash", sha256Hash)
            )
    );
  }

  private Map<String, Object> createAutomaticPersistedQueryFromQuery(String query) {
    return Map.of(
            "extensions", Map.of(
                    "persistedQuery", Map.of(
                            "version", 1,
                            "sha256Hash", DigestUtils.sha256Hex(query)
                    )),
            "query", query
    );
  }
}
