package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextExternalChannelTest extends LiveContextContentBeanTestBase {

  private LiveContextExternalChannel testling;

  @Inject
  private SettingsService settingsService;

  @Inject
  private SitesService sitesService;

  @Mock
  private LiveContextNavigationFactory liveContextNavigationFactory;

  @Mock
  Category category;



  @Before
  public void before() {
    testling = getContentBean(100);
    setUpPreviewDate();

    MockCommerceEnvBuilder.create().setupEnv();

    initMocks(this);

    CatalogService catalogService = mock(CatalogService.class);
    when(catalogService.withStoreContext(any(StoreContext.class))).thenReturn(catalogService);
    when(catalogService.findCategoryById(anyString())).thenReturn(category);
    ((BaseCommerceConnection)Commerce.getCurrentConnection()).setCatalogService(catalogService);
    when(liveContextNavigationFactory.createNavigation(any(Category.class), any(Site.class))).thenReturn(mock(LiveContextNavigation.class));
    testling.setLiveContextNavigationFactory(liveContextNavigationFactory);
  }

  @Test
  public void testSettingsMechanism() throws Exception {
    Map setting = settingsService.setting(LiveContextExternalChannel.COMMERCE_STRUCT, Map.class, testling);
    Assert.assertNotNull(setting);
    assertEquals(3, setting.size());
  }

  @Test
  public void testGetExternalChildrenWithSelectedCategories() throws Exception {
    Assert.assertTrue(testling.isCommerceChildrenSelected());
    Site site = sitesService.getContentSiteAspect(testling.getContent()).getSite();

    List<Linkable> externalChildren = testling.getExternalChildren(site);
    Assert.assertTrue(externalChildren.size() == 3);
  }

}
