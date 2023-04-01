package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.caas.search.HeadlessSearchAutoConfiguration;
import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.base.livecontext.augmentation.AugmentationAutoConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceServicesConfiguration;
import com.coremedia.blueprint.coderesources.ThemeServiceConfiguration;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.richtext.RichtextTransformerRegistry;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.wrapper.UrlPathFormater;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.dataloader.CacheMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

import static com.coremedia.blueprint.caas.augmentation.TestRepoConstants.AUGMENTED_PAGE_EXTERNAL_ID;
import static com.coremedia.blueprint.caas.augmentation.TestRepoConstants.AUGMENTED_PAGE_ID;
import static com.coremedia.blueprint.caas.augmentation.TestRepoConstants.CATEGORY_EXTERNAL_ID;
import static com.coremedia.blueprint.caas.augmentation.TestRepoConstants.CATEGORY_REFERENCE;
import static com.coremedia.blueprint.caas.augmentation.TestRepoConstants.MASTER_SITE_ID;
import static com.coremedia.blueprint.caas.augmentation.TestRepoConstants.PRODUCT_LIST_ID;
import static com.coremedia.blueprint.caas.augmentation.TestRepoConstants.PRODUCT_REFERENCE;
import static com.coremedia.blueprint.caas.augmentation.TestRepoConstants.ROOT_CHANNEL_SEGMENT;

@SpringBootTest(classes = {
        HeadlessAugmentationConfiguration.class,
        AugmentationAutoConfiguration.class,
        BaseCommerceServicesConfiguration.class,
        HeadlessSearchAutoConfiguration.class,
        CaasConfig.class,
        BlueprintBaseMediaConfig.class,
        GraphQLController.class,
        TransformationServiceConfiguration.class,
        ThemeServiceConfiguration.class,
        CommerceSchemaGraphQLTest.LocalTestConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml",
        "cache.capacities.java.lang.Object=20",
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SuppressWarnings("java:S2699") // assertions from spring-graphql are not detected by sonar
class CommerceSchemaGraphQLTest {

  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  ObjectMapper objectMapper;
  @MockBean
  CacheMap remoteLinkCacheMap;
  @MockBean(name = "solrClient")
  SolrClient solrClient;
  @MockBean
  RichtextTransformerRegistry richtextTransformerRegistry;

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
          "root-en;111112;c1,c2",
          "root-en/subpage-en;1111112;",
          "root-de;11144;",
  })
  void testGetPageByPath(String path, String contentId, String values) {
    var externalIds = values != null ? values.split(",") : new String[0];
    webGraphQlTester.documentName("pageByPath")
            .variable("path", path)
            .execute()
            .path("content.pageByPath.id").entity(String.class).isEqualTo(contentId)
            .path("content.pageByPath.children").hasValue()
            .path("content.pageByPath.children[*].categoryRef.externalId")
            .entityList(String.class).containsExactly(externalIds);
  }

  @Test
  void testAugmentationForCommerceIdBySite() {
    webGraphQlTester.documentName("commerce/augmentationForCommerceIdBySite")
            .variable("commerceId", CATEGORY_REFERENCE)
            .variable("siteId", MASTER_SITE_ID)
            .execute()
            .path("commerce.augmentationForCommerceIdBySite").hasValue()
            .path("commerce.augmentationForCommerceIdBySite.id").hasValue()
            .path("commerce.augmentationForCommerceIdBySite.grid").hasValue()
            .path("commerce.augmentationForCommerceIdBySite.commerceRef").hasValue()
            .path("commerce.augmentationForCommerceIdBySite.commerceRef.externalId")
            .entity(String.class).isEqualTo(CATEGORY_EXTERNAL_ID);
  }
  @Test
  void testCategoryAugmentationBySite() {
    webGraphQlTester.documentName("commerce/categoryAugmentationBySite")
            .variable("externalId", CATEGORY_REFERENCE)
            .variable("siteId", MASTER_SITE_ID)
            .execute()
            .path("commerce.categoryAugmentationBySite").hasValue()
            .path("commerce.categoryAugmentationBySite.id").hasValue()
            .path("commerce.categoryAugmentationBySite.grid").hasValue()
            .path("commerce.categoryAugmentationBySite.commerceRef").hasValue()
            .path("commerce.categoryAugmentationBySite.commerceRef.externalId")
            .entity(String.class).isEqualTo(CATEGORY_REFERENCE);
  }
  @Test
  void testCategoryAugmentationByStore() {
    webGraphQlTester.documentName("commerce/categoryAugmentationByStore")
            .variable("externalId", CATEGORY_REFERENCE)
            .variable("catalogId", "catalog")
            .variable("storeId", "store")
            .variable("locale", "en-US")
            .execute()
            .path("commerce.categoryAugmentationByStore").hasValue()
            .path("commerce.categoryAugmentationByStore.id").hasValue()
            .path("commerce.categoryAugmentationByStore.grid").hasValue()
            .path("commerce.categoryAugmentationByStore.commerceRef").hasValue()
            .path("commerce.categoryAugmentationByStore.commerceRef.externalId")
            .entity(String.class).isEqualTo(CATEGORY_REFERENCE);
  }

  @Test
  void testProductAugmentationBySite() {
    webGraphQlTester.documentName("commerce/productAugmentationBySite")
            .variable("externalId", PRODUCT_REFERENCE)
            .variable("siteId", MASTER_SITE_ID)
            .execute()
            .path("commerce.productAugmentationBySite").hasValue()
            .path("commerce.productAugmentationBySite.grid").hasValue()
            .path("commerce.productAugmentationBySite.commerceRef").hasValue()
            .path("commerce.productAugmentationBySite.commerceRef.externalId")
            .entity(String.class).isEqualTo(PRODUCT_REFERENCE);
  }

  @Test
  void testProductAugmentationByStore() {
    webGraphQlTester.documentName("commerce/productAugmentationByStore")
            .variable("externalId", PRODUCT_REFERENCE)
            .variable("catalogId", "catalog")
            .variable("storeId", "store")
            .variable("locale", "en-US")
            .execute()
            .path("commerce.productAugmentationByStore").hasValue()
            .path("commerce.productAugmentationByStore.grid").hasValue()
            .path("commerce.productAugmentationByStore.commerceRef").hasValue()
            .path("commerce.productAugmentationByStore.commerceRef.externalId")
            .entity(String.class).isEqualTo(PRODUCT_REFERENCE);
  }

  @Test
  void testGetExternalPage() {
    // test commerce-reference-schema-ext.graphql
    webGraphQlTester.documentName("commerce/externalPageById")
            .variable("externalId", AUGMENTED_PAGE_EXTERNAL_ID)
            .variable("siteId", MASTER_SITE_ID)
            .execute()
            .path("commerce.externalPage").hasValue()
            .path("commerce.externalPage.id").entity(Integer.class).isEqualTo(AUGMENTED_PAGE_ID)
            .path("commerce.externalPage.externalId").entity(String.class).isEqualTo(AUGMENTED_PAGE_EXTERNAL_ID);
  }

  @Test
  void testCategoryAugmentationBySegment() {
    webGraphQlTester.documentName("content/categoryAugmentationBySegment")
            .variable("externalId", CATEGORY_REFERENCE)
            .variable("breadcrumb", "a/b/c")
            .variable("catalogAlias", "test")
            .variable("rootSegment", ROOT_CHANNEL_SEGMENT)
            .execute()
            .path("content.categoryAugmentationBySegment").hasValue()
            .path("content.categoryAugmentationBySegment.grid").hasValue()
            .path("content.categoryAugmentationBySegment.commerceRef").hasValue()
            .path("content.categoryAugmentationBySegment.commerceRef.externalId")
            .entity(String.class).isEqualTo(CATEGORY_REFERENCE);
  }

  @Test
  void testCategoryAugmentationBySite1() {
    webGraphQlTester.documentName("content/categoryAugmentationBySite")
            .variable("externalId", CATEGORY_REFERENCE)
            .variable("breadcrumb", "a/b/c")
            .variable("catalogAlias", "test")
            .variable("siteId", MASTER_SITE_ID)
            .execute()
            .path("content.categoryAugmentationBySite").hasValue()
            .path("content.categoryAugmentationBySite.grid").hasValue()
            .path("content.categoryAugmentationBySite.commerceRef").hasValue()
            .path("content.categoryAugmentationBySite.commerceRef.externalId")
            .entity(String.class).isEqualTo(CATEGORY_REFERENCE);
  }

  @Test
  void testProductAugmentationBySegment() {
    webGraphQlTester.documentName("content/productAugmentationBySegment")
            .variable("externalId", PRODUCT_REFERENCE)
            .variable("breadcrumb", "a/b/c")
            .variable("catalogAlias", "test")
            .variable("rootSegment", ROOT_CHANNEL_SEGMENT)
            .execute()
            .path("content.productAugmentationBySegment").hasValue()
            .path("content.productAugmentationBySegment.id").hasValue()
            .path("content.productAugmentationBySegment.grid").hasValue()
            .path("content.productAugmentationBySegment.commerceRef").hasValue()
            .path("content.productAugmentationBySegment.commerceRef.externalId").entity(String.class).isEqualTo(PRODUCT_REFERENCE);
  }

  @Test
  void testProductAugmentationBySite1() {
    webGraphQlTester.documentName("content/productAugmentationBySite")
            .variable("externalId", CATEGORY_REFERENCE)
            .variable("breadcrumb", "a/b/c")
            .variable("catalogAlias", "test")
            .variable("siteId", MASTER_SITE_ID)
            .execute()
            .path("content.productAugmentationBySite").hasValue()
            .path("content.productAugmentationBySite.id").hasValue()
            .path("content.productAugmentationBySite.grid").hasValue()
            .path("content.productAugmentationBySite.commerceRef").hasValue()
            .path("content.productAugmentationBySite.commerceRef.externalId")
            .entity(String.class).isEqualTo(CATEGORY_REFERENCE);
  }

  @Test
  void testGetProductList() {
    // test commerce-reference-schema-ext.graphql
    webGraphQlTester.documentName("content/productListById")
            .variable("id", PRODUCT_LIST_ID)
            .execute()
            .path("content.productList.externalId").entity(String.class).isEqualTo(CATEGORY_REFERENCE)
            .path("content.productList.categoryRef.externalId").entity(String.class).isEqualTo(CATEGORY_EXTERNAL_ID)
            .path("content.productList.productItems[*]").entityList(Object.class).hasSize(0);
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {
    @Bean
    BeanResolver pluginSchemaAdapterBeansResolver(BeanFactory beanFactory) {
      return new BeanFactoryResolver(beanFactory);
    }
  }
}
