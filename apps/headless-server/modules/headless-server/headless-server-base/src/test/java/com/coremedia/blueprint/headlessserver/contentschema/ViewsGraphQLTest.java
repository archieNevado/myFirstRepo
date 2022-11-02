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
import com.coremedia.caas.web.wiring.IgnoreFilterPredicatesContextParameter;
import com.coremedia.caas.web.wiring.PreviewDateContextParameter;
import com.coremedia.caas.wrapper.UrlPathFormater;
import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.dataloader.CacheMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.coremedia.caas.headless_server.plugin_support.PluginSupport.QUALIFIER_CAAS_COPY_TO_CONTEXT_PARAMETER;
import static com.coremedia.caas.web.CaasWebConfig.ATTRIBUTE_NAMES_TO_GQL_CONTEXT;
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
        TransformationServiceConfiguration.class,
        ThemeServiceConfiguration.class,
        JacksonAutoConfiguration.class,
        ViewsGraphQLTest.LocalTestConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml",
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SuppressWarnings("java:S2699") // assertions from spring-graphql are not detected by sonar
class ViewsGraphQLTest {

  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  CacheMap remoteLinkCacheMap;

  @Autowired
  private WebTestClient webTestClient;

  @BeforeAll
  public void setUp() {
    RestAssuredWebTestClient.webTestClient(webTestClient
            .mutate()
            .defaultHeader("Content-Type", "application/json")
            .baseUrl("/").build());
  }

  @ParameterizedTest
  @CsvSource(value = {
          "root-en",
          "root-de",
  })
  void testGetAllSitesWithView(String rootSegmentAsView) {
    getWebGraphQlTester(rootSegmentAsView).documentName("allSites")
            .execute()
            .path("content.sites[*].id").entityList(String.class).hasSize(1);
  }

  @Test
  void testGetAllSitesWithWrongView() {
    given()
            .when()
            .get("non-existing-view/graphql").then()
            .status(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "root-en;the-site-id",
          "root-de;the-derived-site-id",
  })
  void testGetSiteByIdWithView(String rootSegmentAsView, String siteId) {
    getWebGraphQlTester(rootSegmentAsView).documentName("siteById")
            .variable("siteId", siteId)
            .execute()
            .path("content.site.id").entity(String.class).isEqualTo(siteId);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "root-en;the-site-id",
          "root-de;the-derived-site-id",
  })
  void testGetSiteByNoIdWithView(String rootSegmentAsView, String siteId) {
    getWebGraphQlTester(rootSegmentAsView).documentName("siteById")
            .execute()
            .path("content.site.id").entity(String.class).isEqualTo(siteId);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "root-en;the-derived-site-id",
          "root-de;the-site-id",
  })
  void testGetSiteByIdWithWrongView(String rootSegmentAsView, String siteId) {
    getWebGraphQlTester(rootSegmentAsView).documentName("siteById")
            .variable("siteId", siteId)
            .execute()
            .path("content.site").valueIsNull();
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "root-en;111112;root-en",
          "root-en/subpage-en;1111112;root-en",
          "subpage-en;1111112;root-en",
          "root-de;11144;root-de",
  })
  void testGetPageByPath(String path, String contentId, String rootSegmentAsView) {
    getWebGraphQlTester(rootSegmentAsView).documentName("pageByPath")
            .variable("path", path)
            .execute()
            .path("content.pageByPath.id").entity(String.class).isEqualTo(contentId);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "root-en;root-de",
          "root-en/subpage-en;root-de",
          "subpage-en;root-de",
          "subpage-de;root-en",
          "root-de;root-en",
  })
  void testGetPageByPathWrongView(String path, String rootSegmentAsView) {
    getWebGraphQlTester(rootSegmentAsView).documentName("pageByPath")
            .variable("path", path)
            .execute()
            .path("content.pageByPath").valueIsNull();
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "CMChannel;111112;root-en",
          "CMArticle;111116;root-en",
          "CMPicture;111114;root-en",
          "CMChannel;11144;root-de",
  })
  void testGetContentById(String contentType, String contentId, String rootSegmentAsView) {
    getWebGraphQlTester(rootSegmentAsView).documentName("contentById")
            .variable("id", contentId)
            .variable("type", contentType)
            .execute()
            .path("content.content.id").entity(String.class).isEqualTo(contentId)
            .path("content.content.type").entity(String.class).isEqualTo(contentType);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "CMChannel;111112;root-de",
          "CMArticle;111116;root-de",
          "CMPicture;111114;root-de",
          "CMChannel;11144;root-en",
  })
  void testGetContentByIdWithWrongView(String contentType, String contentId, String rootSegmentAsView) {
    getWebGraphQlTester(rootSegmentAsView).documentName("contentById")
            .variable("id", contentId)
            .variable("type", contentType)
            .execute()
            .path("content.content").valueIsNull();
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "CMChannel;111112;root-en",
          "CMArticle;111116;root-en",
          "CMPicture;111114;root-en",
          "CMChannel;11144;root-de",
  })
  void testGetContentByIdViaRest(String contentType, String contentId, String rootSegmentAsView) {
    given()
            .when()
            .get("{rootSegmentAsView}/caas/v1/content/{contentType}/{id}", rootSegmentAsView, contentType, contentId).then()
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("api.json").build().toString())
            .body("content.type", equalTo(contentType))
            .body("errors", blankOrNullString());
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "CMChannel;111112;root-de",
          "CMArticle;111116;root-de",
          "CMPicture;111114;root-de",
          "CMChannel;11144;root-en",
  })
  void testGetContentByIdViaRestWithWrongView(String contentType, String contentId, String rootSegmentAsView) {
    given()
            .when()
            .get("{rootSegmentAsView}/caas/v1/content/{contentType}/{id}", rootSegmentAsView, contentType, contentId).then()
            .status(HttpStatus.resolve(404));
  }

  private WebGraphQlTester getWebGraphQlTester(String view) {
    return HttpGraphQlTester.builder(
                    webTestClient
                            .mutate()
                            .baseUrl(String.format("/%s/graphql", view))
            )
            .header("Content-Type", "application/json")
            .build();
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {

    @Bean
    @Qualifier(ATTRIBUTE_NAMES_TO_GQL_CONTEXT)
    public Set<String> requestAttributeNamesToGraphqlContext() {
      return new HashSet<>();
    }

    @Bean
    @Qualifier(QUALIFIER_CAAS_COPY_TO_CONTEXT_PARAMETER)
    public CopyToContextParameter<Long, ZonedDateTime> previewDateContextParameter() {
      return new PreviewDateContextParameter();
    }

    @Bean
    @Qualifier(QUALIFIER_CAAS_COPY_TO_CONTEXT_PARAMETER)
    @ConditionalOnProperty(prefix = "caas", name = "bypass-filter-predicates")
    public CopyToContextParameter<String, Collection<String>> ignoreFilterPredicatesContextParameter() {
      return new IgnoreFilterPredicatesContextParameter();
    }
  }
}
