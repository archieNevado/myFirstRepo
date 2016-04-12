package com.coremedia.livecontext.tree;


import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.tree.CommerceTreeRelation;
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
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExternalChannelContentTreeRelationTest {

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

  ExternalChannelContentTreeRelation testling;

  @Before
  public void setup() {
    initMocks(this);
    testling = new ExternalChannelContentTreeRelation();
    testling.setAugmentationService(augmentationService);
    testling.setCommerceTreeRelation(commerceTreeRelation);
    testling.setSitesService(sitesService);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    MockCommerceEnvBuilder.create().setupEnv();

    initContentMock();
    initCategoryTreeMock();
  }

  @Test
  public void testGetParentOf() throws Exception {
    //direct parent
    assertEquals(childContent, testling.getParentOf(leafContent));
    //direct parent missing
    assertEquals(catalogRootContent, testling.getParentOf(childContent));
    //fallback for root category
    assertEquals(siteRootChannel, testling.getParentOf(catalogRootContent));
  }


  @Test
  public void testPathToRoot() {
    List<Content> path = testling.pathToRoot(leafContent);
    assertEquals(4, path.size());
    assertEquals(siteRootChannel, path.get(0));
    assertEquals(catalogRootContent, path.get(1));
    assertEquals(childContent, path.get(2));
    assertEquals(leafContent, path.get(3));

    List<Content> catalogRootPath = testling.pathToRoot(catalogRootContent);
    assertEquals(2, catalogRootPath.size());
    assertEquals(siteRootChannel, catalogRootPath.get(0));
    assertEquals(catalogRootContent, catalogRootPath.get(1));

    List<Content> siteRootPath = testling.pathToRoot(siteRootChannel);
    assertEquals(1, siteRootPath.size());
    assertEquals(siteRootChannel, catalogRootPath.get(0));
  }

  @Test
  public void testGetNearestContentForCategory() {
    assertEquals(childContent, testling.getNearestContentForCategory(childCategory, site));
    assertEquals(catalogRootContent, testling.getNearestContentForCategory(topCategory, site));
    Category category = mock(Category.class);
    when(category.getExternalId()).thenReturn("bluzb");
    assertNull(testling.getNearestContentForCategory(category, site));
  }

  private void initCategoryTreeMock() {
    when(rootCategory.isRoot()).thenReturn(true);
    when(rootCategory.getChildren()).thenReturn(Collections.singletonList(topCategory));
    when(rootCategory.getReference()).thenReturn(Category.EXTERNAL_ID_ROOT_CATEGORY);
    when(commerceTreeRelation.getParentOf(rootCategory)).thenReturn(null);
    when(getCommerceBeanFactory().createBeanFor(endsWith(Category.EXTERNAL_ID_ROOT_CATEGORY), any(StoreContext.class))).thenReturn(rootCategory);

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

    //augmentation is not defined for topCategory
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

    //siteRootChannel
    when(site.getSiteRootDocument()).thenReturn(siteRootChannel);
    when(siteRootChannel.getType()).thenReturn(type);

    //catalogRootContent
    when(catalogRootContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn(Category.EXTERNAL_ID_ROOT_CATEGORY);
    when(catalogRootContent.getType()).thenReturn(type);

    //childContent
    when(childContent.getType()).thenReturn(type);
    when(childContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn("childCategory");

    //leafContent
    when(leafContent.getType()).thenReturn(type);
    when(leafContent.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn("leafCategory");
  }

  private CommerceBeanFactory getCommerceBeanFactory() {
    return Commerce.getCurrentConnection().getCommerceBeanFactory();
  }
}