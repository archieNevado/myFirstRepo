package com.coremedia.livecontext.tree;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
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
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalChannelContentTreeRelationTest {

  private static final String EXTERNAL_ID_ROOT_CATEGORY = "ROOT_CATEGORY_ID";

  @Mock
  private Category testCategory;

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

  @Before
  public void setup() {
    BaseCommerceConnection commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    when(commerceConnectionInitializer.getCommerceConnectionForSite(site)).thenReturn(commerceConnection);

    initContentMock();
    initCategoryTreeMock();
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

  private void initCategoryTreeMock() {
    when(rootCategory.isRoot()).thenReturn(true);
    when(rootCategory.getChildren()).thenReturn(Collections.singletonList(topCategory));
    when(rootCategory.getReference()).thenReturn(EXTERNAL_ID_ROOT_CATEGORY);
    when(getCommerceBeanFactory().createBeanFor(endsWith(EXTERNAL_ID_ROOT_CATEGORY), any(StoreContext.class))).thenReturn(rootCategory);

    when(topCategory.isRoot()).thenReturn(false);
    when(topCategory.getChildren()).thenReturn(Collections.singletonList(childCategory));
    when(commerceTreeRelation.getParentOf(topCategory)).thenReturn(rootCategory);
    when(getCommerceBeanFactory().createBeanFor(endsWith("topCategory"), any(StoreContext.class))).thenReturn(topCategory);

    when(childCategory.isRoot()).thenReturn(false);
    when(childCategory.getReference()).thenReturn("childCategory");
    when(commerceTreeRelation.getParentOf(childCategory)).thenReturn(topCategory);
    when(getCommerceBeanFactory().createBeanFor(endsWith("childCategory"), any(StoreContext.class))).thenReturn(childCategory);

    when(leafCategory.isRoot()).thenReturn(false);
    when(leafCategory.getReference()).thenReturn("leafCategory");
    when(commerceTreeRelation.getParentOf(leafCategory)).thenReturn(childCategory);
    when(getCommerceBeanFactory().createBeanFor(endsWith("leafCategory"), any(StoreContext.class))).thenReturn(leafCategory);

    // augmentation is not defined for topCategory
    when(augmentationService.getContent(eq(rootCategory))).thenReturn(catalogRootContent);
    when(augmentationService.getContent(eq(topCategory))).thenReturn(null);
    when(augmentationService.getContent(eq(childCategory))).thenReturn(childContent);
  }

  private void initContentMock() {
    ContentType type = mock(ContentType.class);
    when(type.isSubtypeOf(CMExternalChannel.NAME)).thenReturn(true);

    ContentSiteAspect contentSiteAspect = mock(ContentSiteAspect.class);
    when(sitesService.getContentSiteAspect(any(Content.class))).thenReturn(contentSiteAspect);
    when(contentSiteAspect.getSite()).thenReturn(site);

    // siteRootChannel
    when(site.getSiteRootDocument()).thenReturn(siteRootChannel);
    when(siteRootChannel.getType()).thenReturn(type);

    // catalogRootContent
    when(catalogRootContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn(EXTERNAL_ID_ROOT_CATEGORY);
    when(catalogRootContent.getType()).thenReturn(type);

    // childContent
    when(childContent.getType()).thenReturn(type);
    when(childContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn("childCategory");

    // leafContent
    when(leafContent.getType()).thenReturn(type);
    when(leafContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn("leafCategory");
  }

  private CommerceBeanFactory getCommerceBeanFactory() {
    return Commerce.getCurrentConnection().getCommerceBeanFactory();
  }
}
