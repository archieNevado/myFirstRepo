package com.coremedia.blueprint.caas.augmentation.tree;

import com.coremedia.blueprint.base.livecontext.augmentation.AugmentationAutoConfiguration;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.multisite.BlueprintMultisiteConfiguration;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {
        XmlRepoConfiguration.class,
        BlueprintMultisiteConfiguration.class,
        AugmentationAutoConfiguration.class,
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml",
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ExternalBreadcrumbContentTreeRelationTest {

  private static final CommerceIdProvider ID_PROVIDER = new BaseCommerceIdProvider(Vendor.of("vendor"));

  private final ContentRepository contentRepository;
  private final SitesService sitesService;
  private final AugmentationService augmentationService;
  private final ExternalBreadcrumbContentTreeRelation testling;

  private final Site site;

  ExternalBreadcrumbContentTreeRelationTest(ContentRepository contentRepository,
                                            SitesService sitesService,
                                            AugmentationService categoryAugmentationService) {
    this.contentRepository = contentRepository;
    this.sitesService = sitesService;
    augmentationService = categoryAugmentationService;
    site = sitesService.getSite("the-site-id");

    var breadcrumb = List.of("ROOT", "topCategory", "childCategory", "leafCategory");
    testling = create(breadcrumb);
  }

  ExternalBreadcrumbContentTreeRelation create(List<String> externalIds) {
    var breadcrumb = externalIds.stream()
            .map(id -> ID_PROVIDER.formatCategoryId(null, id)).collect(Collectors.toList());
    ExternalBreadcrumbTreeRelation treeRelation = new ExternalBreadcrumbTreeRelation(breadcrumb);
    return new ExternalBreadcrumbContentTreeRelation(augmentationService, treeRelation, sitesService);
  }

  @ParameterizedTest
  @CsvSource(value = {
          "contentId   | parentId",
          "111112      | NIL",
          "6           | 111112",
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
          "111112      | 111112",
          "6           | 111112,6",
          "8           | 111112,6,8",
          "10          | 111112,6,8,10",
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
          "leafCategory            | leafCategory",
          "childCategory           | childCategory",
          "topCategory             | augmentedCatalogRoot",
          "ROOT                    | augmentedCatalogRoot",
  }, useHeadersInDisplayName = true, delimiter = '|', nullValues = "NIL")
  void testGetNearestContentForCategory(String externalId, String name) {
    var commerceId = ID_PROVIDER.formatCategoryId(null, externalId);
    if (name == null) {
      assertThat(testling.getNearestContentForCategory(commerceId, site)).isNull();
    } else {
      assertThat(testling.getNearestContentForCategory(commerceId, site)).extracting(Content::getName).isEqualTo(name);
    }
  }

  @ParameterizedTest
  @CsvSource(value = {
          "contentId   | applicable?",
          "111112      | false",
          "10          | true",
          "12          | false",
          "14          | false",
          "16          | false",
  }, useHeadersInDisplayName = true, delimiter = '|')
  void testIsApplicable(String contentId, boolean applicable) {
    var content = contentRepository.getContent(contentId);
    assertThat(testling.isApplicable(content)).isEqualTo(applicable);
  }

}

