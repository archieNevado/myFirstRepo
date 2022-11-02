package com.coremedia.blueprint.headlessserver.contentschema;

import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.coderesources.ThemeServiceConfiguration;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.wrapper.UrlPathFormater;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dataloader.CacheMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.BLOG_TAXONOMY_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.BLOG_TAXONOMY_UUID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.HAMBURG_LOCATION_TAXONOMY_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.HAMBURG_LOCATION_TAXONOMY_UUID;

@SpringBootTest(classes = {
        CaasConfig.class,
        BlueprintBaseMediaConfig.class,
        GraphQLController.class,
        TransformationServiceConfiguration.class,
        ThemeServiceConfiguration.class,
        ContentSchemaTaxonomyGraphQLTest.LocalTestConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml"
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SuppressWarnings("java:S2699") // assertions from spring-graphql are not detected by sonar
class ContentSchemaTaxonomyGraphQLTest {

  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  ObjectMapper objectMapper;
  @MockBean
  CacheMap remoteLinkCacheMap;

  @Autowired
  private WebTestClient webTestClient;
  private WebGraphQlTester webGraphQlTester;

  @BeforeAll
  public void setUp() {
    webGraphQlTester = HttpGraphQlTester.builder(
                    webTestClient
                            .mutate()
                            .baseUrl("/graphql")
            )
            .header("Content-Type", "application/json")
            .build();
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "130",
          "39395448-62e6-412a-a1cb-ad31bff67ed0",
  })
  void testTaxonomyById(String idOrUuid) {
    webGraphQlTester.documentName("taxonomyById")
            .variable("id", idOrUuid)
            .execute()
            .path("content.taxonomy.id").entity(Integer.class).isEqualTo(BLOG_TAXONOMY_ID)
            .path("content.taxonomy.uuid").entity(String.class).isEqualTo(BLOG_TAXONOMY_UUID)
            .path("content.taxonomy.type").entity(String.class).isEqualTo("CMTaxonomy")
            .path("content.taxonomy.name").entity(String.class).isEqualTo("Blog")
            .path("content.taxonomy.value").entity(String.class).isEqualTo("Blog");
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "13444",
          "3ee8a696-9720-4131-ab87-390c628f4d85",
  })
  void testLocationTaxonomyById(String idOrUuid) {
    webGraphQlTester.documentName("taxonomyById")
            .variable("id", idOrUuid)
            .execute()
            .path("content.taxonomy.id").entity(Integer.class).isEqualTo(HAMBURG_LOCATION_TAXONOMY_ID)
            .path("content.taxonomy.uuid").entity(String.class).isEqualTo(HAMBURG_LOCATION_TAXONOMY_UUID)
            .path("content.taxonomy.type").entity(String.class).isEqualTo("CMLocTaxonomy")
            .path("content.taxonomy.name").entity(String.class).isEqualTo("Hamburg")
            .path("content.taxonomy.value").entity(String.class).isEqualTo("Hamburg");
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "Blog;CMTaxonomy;130;39395448-62e6-412a-a1cb-ad31bff67ed0;Blog",
          "Hamburg;CMLocTaxonomy;13444;3ee8a696-9720-4131-ab87-390c628f4d85;Hamburg",
          "Germany/Hamburg;CMLocTaxonomy;13444;3ee8a696-9720-4131-ab87-390c628f4d85;Hamburg",
          "Europe/Germany/Hamburg;CMLocTaxonomy;13444;3ee8a696-9720-4131-ab87-390c628f4d85;Hamburg",
          "Europe;CMLocTaxonomy;134;062e3060-f135-452f-962b-85a6ef014870;Europe",
  })
  void testLocationTaxonomyByPath(String pathSegments, String type, int id, String uuid, String nameValue) {
    webGraphQlTester.documentName("taxonomyByPath")
            .variable("pathSegments", pathSegments)
            .variable("type", type)
            .execute()
            .path("content.taxonomyByPath.id").entity(Integer.class).isEqualTo(id)
            .path("content.taxonomyByPath.uuid").entity(String.class).isEqualTo(uuid)
            .path("content.taxonomyByPath.type").entity(String.class).isEqualTo(type)
            .path("content.taxonomyByPath.name").entity(String.class).isEqualTo(nameValue)
            .path("content.taxonomyByPath.value").entity(String.class).isEqualTo(nameValue);
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
          "Europe;CMTaxonomy;134;062e3060-f135-452f-962b-85a6ef014870;Europe",
  })
  void testLocationTaxonomyByPathExpectError(String pathSegments, String type) {
    webGraphQlTester.documentName("taxonomyByPath")
            .variable("pathSegments", pathSegments)
            .variable("type", type)
            .execute()
            .errors()
            .expect(error -> error.getMessage().equals(String.format("Cannot find taxonomy for path %s and type %s", pathSegments, type)));
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {
    @Bean
    BeanResolver pluginSchemaAdapterBeansResolver(BeanFactory beanFactory) {
      return new BeanFactoryResolver(beanFactory);
    }
  }
}
