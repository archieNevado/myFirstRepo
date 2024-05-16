package com.coremedia.livecontext.tree;

import com.coremedia.blueprint.base.livecontext.augmentation.AugmentationAutoConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.multisite.BlueprintMultisiteConfiguration;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {
        XmlRepoConfiguration.class,
        BlueprintMultisiteConfiguration.class,
        AugmentationAutoConfiguration.class,
        ExternalChannelContentTreeRelationTest.LocalConfig.class,
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml",
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ExternalChannelContentTreeRelationTest {

  private static final CommerceIdProvider ID_PROVIDER = new BaseCommerceIdProvider(Vendor.of("vendor"));
  private static final String EXTERNAL_ID_ROOT_CATEGORY = "ROOT_CATEGORY_ID";

  private final ExternalChannelContentTreeRelation testling;
  private final ContentRepository contentRepository;
  private final SitesService sitesService;
  private final CommerceConnectionSupplier commerceConnectionSupplier;

  private Site site;

  private CommerceBeanFactory commerceBeanFactory;
  private StoreContextImpl storeContext;

  ExternalChannelContentTreeRelationTest(ContentRepository contentRepository,
                                         SitesService sitesService,
                                         ExternalChannelContentTreeRelation testling,
                                         CommerceConnectionSupplier commerceConnectionSupplier,
                                         TreeRelation<Category> commerceTreeRelation) {
    this.contentRepository = contentRepository;
    this.sitesService = sitesService;
    this.testling = testling;
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  @BeforeEach
  public void setup() {
    site = sitesService.getSites().stream().findAny().orElseThrow(() -> new IllegalStateException("No sites available"));

    var rootCategoryId = ID_PROVIDER.formatCategoryId(null, EXTERNAL_ID_ROOT_CATEGORY);
    var topCategoryId = ID_PROVIDER.formatCategoryId(null, "topCategory");
    var childCategoryId = ID_PROVIDER.formatCategoryId(null, "childCategory");
    var leafCategoryId = ID_PROVIDER.formatCategoryId(null, "leafCategory");

    commerceBeanFactory = mock();
    BaseCommerceConnection commerceConnection = new BaseCommerceConnection();
    commerceConnection.setCommerceBeanFactory(commerceBeanFactory);
    storeContext = StoreContextBuilderImpl.from(commerceConnection, "the-site-id").build();
    commerceConnection.setInitialStoreContext(storeContext);

    when(commerceConnectionSupplier.findConnection(site)).thenReturn(Optional.of(commerceConnection));

    Category rootCategory = create(rootCategoryId, null, storeContext, commerceBeanFactory);
    lenient().when(rootCategory.isRoot()).thenReturn(true);
    Category topCategory = create(topCategoryId, rootCategory, storeContext, commerceBeanFactory);
    Category childCategory = create(childCategoryId, topCategory, storeContext, commerceBeanFactory);
    create(leafCategoryId, childCategory, storeContext, commerceBeanFactory);
  }

  Category create(CommerceId commerceId, Category parent, StoreContext storeContext, CommerceBeanFactory commerceBeanFactory) {
    Category result = mock();
    lenient().when(result.getParent()).thenReturn(parent);
    lenient().when(result.getReference()).thenReturn(commerceId);
    lenient().when(result.getContext()).thenReturn(storeContext);
    lenient().doReturn(result).when(commerceBeanFactory).loadBeanFor(commerceId, storeContext);
    lenient().doReturn(result).when(commerceBeanFactory).createBeanFor(commerceId, storeContext);
    return result;
  }

  @ParameterizedTest
  @CsvSource(value = {
          "contentId   | parentId",
          "4           | NIL",
          "6           | 4",
          "8           | 6",
          "10          | 8",
          "12          | NIL",
          "14          | NIL",
          "16          | NIL",
  }, useHeadersInDisplayName = true, delimiter = '|', nullValues = "NIL")
  void testGetParentOf(String contentId, String expectedParentId) {
    var content = contentRepository.getContent(contentId);
    if (expectedParentId == null) {
      assertThat(testling.getParentOf(content)).isNull();
    } else {
      var parent = contentRepository.getContent(expectedParentId);
      assertThat(testling.getParentOf(content)).isEqualTo(parent);
    }
  }

  @ParameterizedTest
  @CsvSource(value = {
          "contentId   | pathIds",
          "4           | 4",
          "6           | 4,6",
          "8           | 4,6,8",
          "10          | 4,6,8,10",
  }, useHeadersInDisplayName = true, delimiter = '|')
  void testPathToRoot(String contentId, String pathIds) {
    var content = contentRepository.getContent(contentId);
    var expected = Arrays.stream(pathIds.split(","))
            .map(contentRepository::getContent)
            .collect(Collectors.toList());

    assertThat(testling.pathToRoot(content)).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource(value = {
          "categoryId              | contentName",
          "childCategory           | childCategory",
          "topCategory             | augmentedCatalogRoot",
          "doesNotExist            | NIL",
  }, useHeadersInDisplayName = true, delimiter = '|', nullValues = "NIL")
  void testGetNearestContentForCategory(String externalId, String name) {
    var commerceId = ID_PROVIDER.formatCategoryId(null, externalId);
    var category = (Category)commerceBeanFactory.createBeanFor(commerceId, storeContext);
    if (name == null) {
      assertThat(testling.getNearestContentForCategory(category, site)).isNull();
    } else {
      assertThat(testling.getNearestContentForCategory(category, site)).extracting(Content::getName).isEqualTo(name);
    }
  }

  @ParameterizedTest
  @CsvSource(value = {
          "contentId   | applicable?",
          "4           | false",
          "10          | true",
          "12          | false",
          "14          | false",
          "16          | false",
  }, useHeadersInDisplayName = true, delimiter = '|')
  void testIsApplicable(String contentId, boolean applicable) {
    var content = contentRepository.getContent(contentId);
    assertThat(testling.isApplicable(content)).isEqualTo(applicable);
  }

  static class LocalConfig {

    @Bean
    CommerceTreeRelation commerceTreeRelation() {
      return new CommerceTreeRelation();
    }

    @Bean
    CommerceConnectionSupplier commerceConnectionSupplier() {
      return mock();
    }

    @Bean
    ExternalChannelContentTreeRelation externalChannelContentTreeRelation(SitesService sitesService,
                                                                          CommerceTreeRelation commerceTreeRelation,
                                                                          AugmentationService categoryAugmentationService,
                                                                          CommerceConnectionSupplier commerceConnectionSupplier) {
      var treeRelation = new ExternalChannelContentTreeRelation();
      treeRelation.setAugmentationService(categoryAugmentationService);
      treeRelation.setCommerceTreeRelation(commerceTreeRelation);
      treeRelation.setSitesService(sitesService);
      treeRelation.setCommerceConnectionSupplier(commerceConnectionSupplier);
      return treeRelation;
    }
  }
}
