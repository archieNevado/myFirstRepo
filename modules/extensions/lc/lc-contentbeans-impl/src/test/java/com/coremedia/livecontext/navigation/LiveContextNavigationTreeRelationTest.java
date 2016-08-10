package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.tree.CommerceTreeRelation;
import com.coremedia.livecontext.tree.ExternalChannelContentTreeRelation;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiveContextNavigationTreeRelationTest {
  private static final String SITE_ID = "aSiteId";

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private ValidationService validationService;

  @Mock
  private Category augmentedCategory;

  @Mock
  private CommerceTreeRelation treeRelation;

  @Mock
  private SitesService sitesService;

  @Mock
  private AugmentationService augmentationService;

  @Mock
  private Site site;

  @Mock
  private Content content;

  @Mock
  private LiveContextExternalChannelImpl externalChannel;

  @InjectMocks
  @Spy
  private ExternalChannelContentTreeRelation delegate;
  @InjectMocks
  private LiveContextNavigationFactory navigationFactory;
  @InjectMocks
  private LiveContextNavigationTreeRelation testling;

  @Before
  public void setUp() throws Exception {
    testling.setNavigationFactory(navigationFactory);
    testling.setDelegate(delegate);
    navigationFactory.setTreeRelation(testling);
    delegate.setCommerceTreeRelation(treeRelation);

    when(sitesService.getSite(SITE_ID)).thenReturn(site);
    when(augmentationService.getContent(augmentedCategory)).thenReturn(content);
    when(contentBeanFactory.createBeanFor(content)).thenReturn(externalChannel);
  }

  @Test
  public void testGetChildrenOf() throws Exception {
    Category categoryChild1 = mock(Category.class);
    Category categoryChild2 = mock(Category.class);

    List<Category> categoryChildren = new ArrayList<>();
    categoryChildren.add(categoryChild1);
    categoryChildren.add(categoryChild2);

    when(augmentedCategory.getChildren()).thenReturn(categoryChildren);

    LiveContextNavigation testNavigation = navigationFactory.createNavigation(augmentedCategory, site);
    Collection<Linkable> childrenOf = testling.getChildrenOf(testNavigation);

    assertEquals(2, childrenOf.size());
    Iterator<Linkable> iterator = childrenOf.iterator();
    LiveContextNavigation firstChild = (LiveContextNavigation) iterator.next();
    LiveContextNavigation secondChild = (LiveContextNavigation) iterator.next();
    assertSame(categoryChild1, firstChild.getCategory());
    assertSame(categoryChild2, secondChild.getCategory());
  }

  /**
   * The breadcrumb is the rendered result of
   * {@link LiveContextNavigationTreeRelation#pathToRoot(com.coremedia.blueprint.common.navigation.Linkable)}
   */
  @Test
  public void testBreadcrumb_CMS_5573() throws Exception {
    Category c1 = mock(Category.class);
    Category c2 = mock(Category.class);
    Category c3 = mock(Category.class);
    when(c3.getBreadcrumb()).thenReturn(asList(augmentedCategory, c2, c1));

    LiveContextNavigation testNavigation = navigationFactory.createNavigation(c3, site);
    Collection<Linkable> breadcrumb = testling.pathToRoot(testNavigation);

    assertEquals(4, breadcrumb.size());
    /* TODO fixme
    Iterator<Linkable> iterator = breadcrumb.iterator();
    assertSame(c1, ((LiveContextNavigation) iterator.next()).getCategory());
    assertSame(c2, ((LiveContextNavigation) iterator.next()).getCategory());
    */
  }

}
