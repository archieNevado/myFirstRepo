package com.coremedia.livecontext.tree;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceIdOrThrow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExternalChannelContentTreeRelationTest {

  private static final String EXTERNAL_ID_ROOT_CATEGORY = "ROOT_CATEGORY_ID";
  private static final String REFERENCE_PREFIX = "ibm:///catalog/category/";

  @Mock
  private CommerceTreeRelation commerceTreeRelation;

  @Mock
  private SitesService sitesService;

  @Mock
  private AugmentationService augmentationService;

  @Mock
  private Site site;

  @Mock
  private Content siteRootChannel;

  @Mock
  Category rootCategory;

  @Mock
  private Content catalogRootContent;

  @Mock
  Category topCategory; //no content assigned

  @Mock
  Category childCategory;

  @Mock
  private Content childContent;

  @Mock
  Category leafCategory;

  @Mock
  private Content leafContent;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @InjectMocks
  ExternalChannelContentTreeRelation testling;
  private MockCommerceEnvBuilder envBuilder;

  @Before
  public void setup() {
    envBuilder = MockCommerceEnvBuilder.create();
    BaseCommerceConnection commerceConnection = envBuilder.setupEnv();
    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(commerceConnection));

    initContentMock();
    initCategoryTreeMock();
  }

  @After
  public void tearDown() throws Exception {
    envBuilder.tearDownEnv();
  }

  @Test
  public void testGetParentOf() throws Exception {
    // direct parent
    assertThat(testling.getParentOf(leafContent)).isEqualTo(childContent);

    // direct parent missing
    assertThat(testling.getParentOf(childContent)).isEqualTo(catalogRootContent);

    // fallback for root category
    assertThat(testling.getParentOf(catalogRootContent)).isEqualTo(siteRootChannel);
  }

  @Test
  public void testPathToRoot() {
    List<Content> path = testling.pathToRoot(leafContent);
    assertThat(path).hasSize(4);
    assertThat(path.get(0)).isEqualTo(siteRootChannel);
    assertThat(path.get(1)).isEqualTo(catalogRootContent);
    assertThat(path.get(2)).isEqualTo(childContent);
    assertThat(path.get(3)).isEqualTo(leafContent);

    List<Content> catalogRootPath = testling.pathToRoot(catalogRootContent);
    assertThat(catalogRootPath).hasSize(2);
    assertThat(catalogRootPath.get(0)).isEqualTo(siteRootChannel);
    assertThat(catalogRootPath.get(1)).isEqualTo(catalogRootContent);

    List<Content> siteRootPath = testling.pathToRoot(siteRootChannel);
    assertThat(siteRootPath).hasSize(1);
    assertThat(catalogRootPath.get(0)).isEqualTo(siteRootChannel);
  }

  @Test
  public void testGetNearestContentForCategory() {
    assertThat(testling.getNearestContentForCategory(childCategory, site)).isEqualTo(childContent);
    assertThat(testling.getNearestContentForCategory(topCategory, site)).isEqualTo(catalogRootContent);

    Category category = mock(Category.class);
    when(category.getExternalId()).thenReturn("bluzb");
    assertThat(testling.getNearestContentForCategory(category, site)).isNull();
  }

  @Test
  public void testIsApplicable(){
    //test valid external channel contents
    assertThat(testling.isApplicable(leafContent)).isTrue();
    assertThat(testling.isApplicable(siteRootChannel)).isFalse();

    //test broken category link (e.g. category is in workspace only)
    Content contentWithBrokenCategoryLink = mock(Content.class);
    Category brokenCategory = mock(Category.class);

    ContentType typeExternalChannel = mock(ContentType.class);
    when(typeExternalChannel.isSubtypeOf(CMExternalChannel.NAME)).thenReturn(true);
    when(contentWithBrokenCategoryLink.getType()).thenReturn(typeExternalChannel);

    when(contentWithBrokenCategoryLink.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn(REFERENCE_PREFIX + "does_not_exsist");
    doReturn(brokenCategory).when(getCommerceBeanFactory()).createBeanFor(endsWith("does_not_exsist"), any(StoreContext.class));
    doReturn(null).when(getCommerceBeanFactory()).loadBeanFor(endsWith("does_not_exsist"), any(StoreContext.class));

    assertThat(testling.isApplicable(contentWithBrokenCategoryLink)).isFalse();

    //test external channel with empty category reference
    Content contentWithEmptyCategoryLink = mock(Content.class);
    when(contentWithEmptyCategoryLink.getType()).thenReturn(typeExternalChannel);
    when(contentWithEmptyCategoryLink.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn(null);

    assertThat(testling.isApplicable(contentWithEmptyCategoryLink)).isFalse();
  }

  private void initCategoryTreeMock() {
    when(rootCategory.isRoot()).thenReturn(true);
    when(rootCategory.getChildren()).thenReturn(Collections.singletonList(topCategory));
    String idPrefix = "test:///s/category/";
    when(rootCategory.getId()).thenReturn(parseCommerceIdOrThrow(idPrefix + EXTERNAL_ID_ROOT_CATEGORY));
    doReturn(rootCategory).when(getCommerceBeanFactory()).createBeanFor(endsWith(EXTERNAL_ID_ROOT_CATEGORY), any(StoreContext.class));
    doReturn(rootCategory).when(getCommerceBeanFactory()).loadBeanFor(endsWith(EXTERNAL_ID_ROOT_CATEGORY), any(StoreContext.class));

    when(topCategory.isRoot()).thenReturn(false);
    when(topCategory.getChildren()).thenReturn(Collections.singletonList(childCategory));
    when(commerceTreeRelation.getParentOf(topCategory)).thenReturn(rootCategory);
    doReturn(topCategory).when(getCommerceBeanFactory()).createBeanFor(endsWith("topCategory"), any(StoreContext.class));
    doReturn(topCategory).when(getCommerceBeanFactory()).loadBeanFor(endsWith("topCategory"), any(StoreContext.class));

    when(childCategory.isRoot()).thenReturn(false);
    when(childCategory.getId()).thenReturn(parseCommerceIdOrThrow(idPrefix + "childCategory"));
    when(commerceTreeRelation.getParentOf(childCategory)).thenReturn(topCategory);
    doReturn(childCategory).when(getCommerceBeanFactory()).createBeanFor(endsWith("childCategory"), any(StoreContext.class));
    doReturn(childCategory).when(getCommerceBeanFactory()).loadBeanFor(endsWith("childCategory"), any(StoreContext.class));

    when(leafCategory.isRoot()).thenReturn(false);
    when(leafCategory.getId()).thenReturn(parseCommerceIdOrThrow(idPrefix + "leafCategory"));
    when(commerceTreeRelation.getParentOf(leafCategory)).thenReturn(childCategory);
    doReturn(leafCategory).when(getCommerceBeanFactory()).createBeanFor(endsWith("leafCategory"), any(StoreContext.class));
    doReturn(leafCategory).when(getCommerceBeanFactory()).loadBeanFor(endsWith("leafCategory"), any(StoreContext.class));

    // augmentation is not defined for topCategory
    when(augmentationService.getContent(eq(rootCategory))).thenReturn(catalogRootContent);
    when(augmentationService.getContent(eq(topCategory))).thenReturn(null);
    when(augmentationService.getContent(eq(childCategory))).thenReturn(childContent);
  }

  private void initContentMock() {
    ContentType typeExternalChannel = mock(ContentType.class);
    when(typeExternalChannel.isSubtypeOf(CMExternalChannel.NAME)).thenReturn(true);

    ContentSiteAspect contentSiteAspect = mock(ContentSiteAspect.class);
    when(sitesService.getContentSiteAspect(any(Content.class))).thenReturn(contentSiteAspect);
    when(contentSiteAspect.getSite()).thenReturn(site);

    // siteRootChannel
    ContentType typeAugmentedPage = mock(ContentType.class);
    when(typeAugmentedPage.isSubtypeOf(CMExternalChannel.NAME)).thenReturn(false);

    when(site.getSiteRootDocument()).thenReturn(siteRootChannel);
    when(siteRootChannel.getType()).thenReturn(typeAugmentedPage);

    // catalogRootContent
    when(catalogRootContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn(REFERENCE_PREFIX + EXTERNAL_ID_ROOT_CATEGORY);
    when(catalogRootContent.getType()).thenReturn(typeExternalChannel);

    // childContent
    when(childContent.getType()).thenReturn(typeExternalChannel);
    when(childContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn(REFERENCE_PREFIX + "childCategory");

    // leafContent
    when(leafContent.getType()).thenReturn(typeExternalChannel);
    when(leafContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn(REFERENCE_PREFIX + "leafCategory");
  }

  private CommerceId endsWith(String suffix) {
    return argThat(commerceId -> commerceId
            .getExternalId()
            .map(e -> e.equals(suffix))
            .orElse(false));
  }

  private CommerceBeanFactory getCommerceBeanFactory() {
    return CurrentCommerceConnection.get().getCommerceBeanFactory();
  }
}
