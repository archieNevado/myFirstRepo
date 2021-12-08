package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiveContextNavigationFactoryTest {

  @InjectMocks
  private LiveContextNavigationFactory testling;

  @Mock
  private CatalogService catalogService;

  @Mock
  private StoreContext storeContext;

  @Mock
  private LiveContextNavigationTreeRelation treeRelation;

  @Mock
  private SitesService sitesService;

  @Mock
  private Site site;

  @Mock
  private Content content;

  @Mock
  private LiveContextExternalChannelImpl externalChannel;

  @Mock
  private ContentSiteAspect contentSiteAspect;

  @Mock
  private CommerceConnection connection;

  @Mock
  private AugmentationService augmentationService;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private ValidationService validationService;

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Before
  public void setUp() {
    when(connection.getInitialStoreContext()).thenReturn(storeContext);

    when(commerceConnectionSupplier.findConnection(site)).thenReturn(Optional.of(connection));
    when(connection.getCatalogService()).thenReturn(catalogService);

    when(sitesService.getContentSiteAspect(content)).thenReturn(contentSiteAspect);
    when(contentSiteAspect.findSite()).thenReturn(Optional.of(site));

    when(contentBeanFactory.createBeanFor(content, LiveContextNavigation.class)).thenReturn(externalChannel);
    when(validationService.validate(externalChannel)).thenReturn(true);
  }

  @After
  public void teardown() {
    CurrentStoreContext.remove();
  }

  @Test
  public void testCreateNavigationWithValidCategory() {
    Category categoryToCreateFrom = mock(Category.class);

    LiveContextNavigation actual = testling.createNavigation(categoryToCreateFrom, site);
    assertThat(actual).as("The returned Navigation must not be null").isNotNull();

    Category categoryInNavigation = actual.getCategory();
    assertThat(categoryInNavigation)
            .as("The created LiveContextNavigation is expected to contain the category given by the first parameter of this method")
            .isSameAs(categoryToCreateFrom);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateNavigationWithCategoryIsNull() {
    testling.createNavigation(null, site);
  }

  @Test
  public void testCreateNavigationWithAugmentingContent() {
    Category categoryToCreateFrom = mock(Category.class);
    when(augmentationService.getContent(categoryToCreateFrom)).thenReturn(content);

    LiveContextNavigation actual = testling.createNavigation(categoryToCreateFrom, site);
    assertThat(actual).as("The returned Navigation must not be null").isNotNull();
    assertThat(actual).isInstanceOf(LiveContextExternalChannelImpl.class);
  }

  @Test
  public void testCreateNavigationBySeoSegment() {
    String existingSeoSegment = "existingSeoSegment";
    Category category = mock(Category.class);
    when(catalogService.findCategoryBySeoSegment(existingSeoSegment, storeContext)).thenReturn(category);

    LiveContextNavigation actual = testling.createNavigationBySeoSegment(content, existingSeoSegment);
    assertThat(actual).as("The returned Navigation must not be null").isNotNull();
    assertThat(actual.getCategory())
            .as("The created LiveContextNavigation is expected to contain the category given by the first parameter of this method")
            .isSameAs(category);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateNavigationBySeoSegmentContentWithoutContext() {
    String existingSeoSegment = "existingSeoSegment";

    when(commerceConnectionSupplier.findConnection(site)).thenReturn(Optional.empty());

    testling.createNavigationBySeoSegment(content, existingSeoSegment);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateNavigationBySeoSegmentNoValidSeoSegment() {
    String notExistingSeoSegment = "notExistingSeoSegment";

    when(catalogService.findCategoryBySeoSegment(notExistingSeoSegment, storeContext)).thenReturn(null);

    testling.createNavigationBySeoSegment(content, notExistingSeoSegment);
  }

  @Test(expected = CommerceException.class)
  public void testCreateNavigationBySeoSegmentCommerceException() {
    String anySeoSegment = "anySeoSegment";

    when(catalogService.findCategoryBySeoSegment(anySeoSegment, storeContext)).thenThrow(CommerceException.class);

    testling.createNavigationBySeoSegment(content, anySeoSegment);
  }
}
