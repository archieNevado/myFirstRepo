package com.coremedia.blueprint.headlessserver.contentschema;

import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.media.TransformationService;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.wrapper.UrlPathFormater;
import com.coremedia.image.ImageDimensionsExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dataloader.CacheMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.ARTICLE_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.ARTICLE_REPO_PATH;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.DERIVED_ROOT_CHANNEL_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.DERIVED_SITE_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.DERIVED_SITE_LOCALE;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.DERIVED_SITE_NAME;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.DERIVED_SITE_REPO_PATH;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.DOWNLOAD_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.HTML_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.MASTER_SITE_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.MASTER_SITE_LOCALE;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.MASTER_SITE_NAME;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.MASTER_SITE_REPO_PATH;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.MEDIA_DELIVERY_PATH;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.MEDIA_REPO_PATH;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.PICTURE_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.ROOT_CHANNEL_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.ROOT_CHANNEL_REPO_PATH;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.ROOT_CHANNEL_SEGMENT;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.SETTINGS_KEY;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.SETTINGS_VALUE;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.VIDEO_ID;

@SpringBootTest(classes = {
        CaasConfig.class,
        BlueprintBaseMediaConfig.class,
        GraphQLController.class,
        TransformationServiceConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml"
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@ContextConfiguration(classes = ContentSchemaGraphQLTest.LocalTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SuppressWarnings("java:S2699") // assertions from spring-graphql are not detected by sonar
class ContentSchemaGraphQLTest {

  @MockBean
  ThemeService themeService;
  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  ObjectMapper objectMapper;
  @MockBean
  TransformationService transformationService;
  @MockBean
  ImageDimensionsExtractor imageDimensionsExtractor;
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

  @Test
  void testGetAllSites() {
    webGraphQlTester.documentName("allSites")
            .execute()
            .path("content.sites[*].id").entityList(String.class).contains(MASTER_SITE_ID, DERIVED_SITE_ID)
            .path("content.sites[*].name").entityList(String.class).contains(MASTER_SITE_NAME, DERIVED_SITE_NAME)
            .path("content.sites[*].locale").entityList(String.class).contains(MASTER_SITE_LOCALE, DERIVED_SITE_LOCALE)
            .path("content.sites[*].repositoryPath").entityList(String.class).contains(MASTER_SITE_REPO_PATH, DERIVED_SITE_REPO_PATH)
            .path("content.sites[*].crops").hasValue()
            .path("content.sites[?(@.name == '" + MASTER_SITE_NAME + "')].root.id").entityList(Integer.class).containsExactly(ROOT_CHANNEL_ID)
            .path("content.sites[0].crops").hasValue();
  }

  @Test
  void testGetSiteById() {
    webGraphQlTester.documentName("siteById")
            .variable("siteId", MASTER_SITE_ID)
            .execute()
            .path("content.site.id").entity(String.class).isEqualTo(MASTER_SITE_ID)
            .path("content.site.name").entity(String.class).isEqualTo(MASTER_SITE_NAME)
            .path("content.site.locale").entity(String.class).isEqualTo(MASTER_SITE_LOCALE)
            .path("content.site.repositoryPath").entity(String.class).isEqualTo(MASTER_SITE_REPO_PATH)
            .path("content.site.root.id").entity(Integer.class).isEqualTo(ROOT_CHANNEL_ID)
            .path("content.site.derivedSites[0].id").entity(String.class).isEqualTo(DERIVED_SITE_ID)
            .path("content.site.crops").hasValue();
  }

  @Test
  void testGetSiteByRootSegment() {
    webGraphQlTester.documentName("siteByRootSegment")
            .variable("rootSegment", ROOT_CHANNEL_SEGMENT)
            .execute()
            .path("content.site.id").entity(String.class).isEqualTo(MASTER_SITE_ID)
            .path("content.site.name").entity(String.class).isEqualTo(MASTER_SITE_NAME)
            .path("content.site.locale").entity(String.class).isEqualTo(MASTER_SITE_LOCALE)
            .path("content.site.repositoryPath").entity(String.class).isEqualTo(MASTER_SITE_REPO_PATH)
            .path("content.site.root.id").entity(Integer.class).isEqualTo(ROOT_CHANNEL_ID)
            .path("content.site.root.segment").entity(String.class).isEqualTo(ROOT_CHANNEL_SEGMENT)
            .path("content.site.derivedSites[0].id").entity(String.class).isEqualTo(DERIVED_SITE_ID)
            .path("content.site.crops").hasValue();
  }

  @Test
  void testGetPageById() {
    webGraphQlTester.documentName("pageById")
            .variable("id", ROOT_CHANNEL_ID)
            .execute()
            .path("content.page.id").entity(Integer.class).isEqualTo(ROOT_CHANNEL_ID)
            .path("content.page.type").entity(String.class).isEqualTo("CMChannel")
            .path("content.page.repositoryPath").entity(String.class).isEqualTo(ROOT_CHANNEL_REPO_PATH)
            .path("content.page.locale").entity(String.class).isEqualTo(MASTER_SITE_LOCALE)
            .path("content.page.title").entity(String.class).isEqualTo("USHomePage")
            .path("content.page.keywords").entity(String.class).isEqualTo("key 1,key2,key3")
            .path("content.page.keywordsList").entityList(String.class).contains("key 1", "key2", "key3")
            .path("content.page.htmlTitle").entity(String.class).isEqualTo("htmlTitle")
            .path("content.page.htmlDescription").entity(String.class).isEqualTo("htmlDescription")

            .path("content.page.localizedVariants[*].id").entityList(Integer.class).contains(ROOT_CHANNEL_ID, DERIVED_ROOT_CHANNEL_ID)
            .path("content.page.localizedVariants[*].locale").entityList(String.class).contains(MASTER_SITE_LOCALE, DERIVED_SITE_LOCALE)
            .path("content.page.localizedVariant.id").entity(Integer.class).isEqualTo(DERIVED_ROOT_CHANNEL_ID)
            .path("content.page.localizedVariant.locale").entity(String.class).isEqualTo(DERIVED_SITE_LOCALE)
            .path("content.page.localizationRoot.id").entity(Integer.class).isEqualTo(ROOT_CHANNEL_ID)
            .path("content.page.localizationRoot.locale").entity(String.class).isEqualTo(MASTER_SITE_LOCALE)

            .path("content.page.children").hasValue()
            .path("content.page.pictures[*].id").entityList(Integer.class).contains(PICTURE_ID)
            .path("content.page.picture.id").entity(Integer.class).isEqualTo(PICTURE_ID)
            .path("content.page.picturesPaged.totalCount").entity(Integer.class).isEqualTo(1)
            .path("content.page.picturesPaged.result[*].id").entityList(Integer.class).contains(PICTURE_ID)
            .path("content.page.picturesPaged.result[*].type").entityList(String.class).contains("CMPicture")

            .path("content.page.settings." + SETTINGS_KEY).entity(Integer.class).isEqualTo(SETTINGS_VALUE);
  }

  @Test
  void testGetArticleById() {
    webGraphQlTester.documentName("articleById")
            .variable("id", ARTICLE_ID)
            .execute()
            .path("content.article.id").entity(Integer.class).isEqualTo(ARTICLE_ID)
            .path("content.article.type").entity(String.class).isEqualTo("CMArticle")
            .path("content.article.locale").entity(String.class).isEqualTo(MASTER_SITE_LOCALE)
            .path("content.article.repositoryPath").entity(String.class).isEqualTo(ARTICLE_REPO_PATH)
            .path("content.article.mainNavigation").valueIsNull()
            .path("content.article.detailText.text").entity(String.class).matches(html -> html.contains("foo bar"))
            .path("content.article.detailText.textAsTree").valueIsNull() // element tree
            .path("content.article.detailText.textReferencedContent").hasValue()
            .path("content.article.teaserText.text").entity(String.class).matches(html -> html.contains("foo bar"))
            .path("content.article.teaserText.textAsTree").valueIsNull() // element tree
            .path("content.article.teaserText.textReferencedContent").hasValue()
            // legacy properties w/o objects 'detailText' or 'teaserText'
            .path("content.article.detailTextLegacy").entity(String.class).matches(html -> html.contains("foo bar"))
            .path("content.article.detailTextAsTree").valueIsNull()
            .path("content.article.detailTextReferencedContent").hasValue()
            .path("content.article.teaserTextLegacy").entity(String.class).matches(html -> html.contains("foo bar"))
            .path("content.article.teaserTextAsTree").valueIsNull()
            .path("content.article.teaserTextReferencedContent").hasValue()
            // Note: These assertions effectively test the settings adapter with various input parameters (see query articleById.graphql)
            //       All settings return null as there are not setting in xml repo content!
            .path("content.article.onePath1.p1").valueIsNull()
            .path("content.article.onePath2.p1").valueIsNull()
            .path("content.article.twoPaths1.p1").valueIsNull()
            .path("content.article.twoPaths1.p2").valueIsNull()
            .path("content.article.twoPaths2.p1").valueIsNull()
            .path("content.article.twoPaths2.p2").valueIsNull()
            .path("content.article.tree1.p1.p11").valueIsNull()
            .path("content.article.tree1.p1.p12").valueIsNull()
            .path("content.article.tree2.p1.p11").valueIsNull()
            .path("content.article.tree2.p1.p12.p121").valueIsNull()
            .path("content.article.tree2.p1.p12.p122").valueIsNull();
  }

  @Test
  void testGetPictureById() {
    webGraphQlTester.documentName("pictureById")
            .variable("id", PICTURE_ID)
            .execute()
            .path("content.picture.id").entity(Integer.class).isEqualTo(PICTURE_ID)
            .path("content.picture.type").entity(String.class).isEqualTo("CMPicture")
            .path("content.picture.name").entity(String.class).isEqualTo("pic16")
            .path("content.picture.locale").entity(String.class).isEqualTo(MASTER_SITE_LOCALE)
            .path("content.picture.repositoryPath").entity(String.class).isEqualTo(MEDIA_REPO_PATH)
            .path("content.picture.caption").entity(String.class).matches(html -> html.contains("foo bar"))
            .path("content.picture.captionReferencedContent").entityList(String.class).hasSize(0)
            .path("content.picture.uriTemplate").entity(String.class).matches(uriTemplate -> uriTemplate.startsWith(MEDIA_DELIVERY_PATH + PICTURE_ID + "/data"));
  }

  @Test
  void testGetVideoById() {
    webGraphQlTester.documentName("videoById")
            .variable("id", VIDEO_ID)
            .execute()
            .path("content.content.id").entity(Integer.class).isEqualTo(VIDEO_ID)
            .path("content.content.type").entity(String.class).isEqualTo("CMVideo")
            .path("content.content.name").entity(String.class).isEqualTo("vid18")
            .path("content.content.repositoryPath").entity(String.class).isEqualTo(MEDIA_REPO_PATH)
            .path("content.content.fullyQualifiedUrl").entity(String.class).matches(uriTemplate -> uriTemplate.startsWith(MEDIA_DELIVERY_PATH + VIDEO_ID + "/data"));
  }

  @Test
  void testGetDownloadById() {
    webGraphQlTester.documentName("cmDownloadById")
            .variable("id", DOWNLOAD_ID)
            .execute()
            .path("content.content.id").entity(Integer.class).isEqualTo(DOWNLOAD_ID)
            .path("content.content.type").entity(String.class).isEqualTo("CMDownload")
            .path("content.content.name").entity(String.class).isEqualTo("pdf20")
            .path("content.content.repositoryPath").entity(String.class).isEqualTo(MEDIA_REPO_PATH)
            .path("content.content.fullyQualifiedUrl").entity(String.class).matches(url -> url.startsWith(MEDIA_DELIVERY_PATH + DOWNLOAD_ID + "/data"));
  }

  @Test
  void testGetHtmlFragmentById() {
    webGraphQlTester.documentName("htmlFragmentById")
            .variable("id", HTML_ID)
            .execute()
            .path("content.content.id").entity(Integer.class).isEqualTo(HTML_ID)
            .path("content.content.type").entity(String.class).isEqualTo("CMHTML")
            .path("content.content.name").entity(String.class).isEqualTo("html22")
            .path("content.content.repositoryPath").entity(String.class).isEqualTo(MEDIA_REPO_PATH)
            .path("content.content.description").entity(String.class).isEqualTo("html description")
            .path("content.content.html").entity(String.class).matches(html -> html.contains("foo bar"))
            .path("content.content.data.uri").entity(String.class).matches(url -> url.startsWith(MEDIA_DELIVERY_PATH + "markup/" + HTML_ID + "/data"));
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {
    @Bean
    BeanResolver pluginSchemaAdapterBeansResolver(BeanFactory beanFactory) {
      return new BeanFactoryResolver(beanFactory);
    }
  }
}
