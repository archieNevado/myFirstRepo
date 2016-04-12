package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.livecontext.tree.CommerceTreeRelation;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.tree.ExternalChannelContentTreeRelation;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextNavigationTreeRelationTest {
  private static final String SITE_ID = "aSiteId";

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private LiveContextNavigationFactory navigationFactory;

  @Mock
  private Category testCategory;

  @Mock
  private CommerceTreeRelation treeRelation;

  @Mock
  private SitesService sitesService;

  @Mock
  private ExternalChannelContentTreeRelation delegate;

  @Mock
  private AugmentationService augmentationService;

  @Mock
  private Site site;

  private LiveContextNavigationTreeRelation testling;

  private LiveContextNavigation testNavigation;


  @Before
  public void setUp() throws Exception {
    initMocks(this);
    testling = new LiveContextNavigationTreeRelation();
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setNavigationFactory(navigationFactory);
    testling.setDelegate(delegate);

    testNavigation = new LiveContextCategoryNavigation(testCategory, site, testling);
    when(sitesService.getSite(SITE_ID)).thenReturn(site);
  }

  @Test
  public void testGetChildrenOf() throws Exception {
    Category categoryChild1 = mock(Category.class);
    Category categoryChild2 = mock(Category.class);

    List<Category> categoryChildren = new ArrayList<>();
    categoryChildren.add(categoryChild1);
    categoryChildren.add(categoryChild2);

    when(testCategory.getChildren()).thenReturn(categoryChildren);
    when(navigationFactory.createNavigation(categoryChild1, site)).thenReturn(new LiveContextCategoryNavigation(categoryChild1, site, testling));
    when(navigationFactory.createNavigation(categoryChild2, site)).thenReturn(new LiveContextCategoryNavigation(categoryChild2, site, testling));

    Collection<Linkable> childrenOf = testling.getChildrenOf(testNavigation);

    assertEquals(2, childrenOf.size());
    Iterator<Linkable> iterator = childrenOf.iterator();
    LiveContextNavigation firstChild = (LiveContextNavigation) iterator.next();
    LiveContextNavigation secondChild = (LiveContextNavigation) iterator.next();
    assertSame(categoryChild1, firstChild.getCategory());
    assertSame(categoryChild2, secondChild.getCategory());
  }
}
