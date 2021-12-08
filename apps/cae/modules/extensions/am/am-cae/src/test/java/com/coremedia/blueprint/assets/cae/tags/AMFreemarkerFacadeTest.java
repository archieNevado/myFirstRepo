package com.coremedia.blueprint.assets.cae.tags;


import com.coremedia.blueprint.assets.cae.DownloadPortal;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.web.FreemarkerEnvironment;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.coremedia.blueprint.assets.common.AMSettingKeys.ASSET_MANAGEMENT;
import static com.coremedia.blueprint.assets.common.AMSettingKeys.ASSET_MANAGEMENT_DOWNLOAD_PORTAL;
import static com.coremedia.blueprint.assets.common.AMSettingKeys.ASSET_MANAGEMENT_DOWNLOAD_PORTAL_ROOT_PAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AMFreemarkerFacadeTest {

  private final AMFreemarkerFacade facade = new AMFreemarkerFacade();

  @Mock
  private HttpServletRequest request;

  @Test
  public void hasDownloadPortal() {
    SettingsService settingsService = mock(SettingsService.class);
    Site site = mock(Site.class);
    Content root = mock(Content.class, "root");
    when(site.getSiteRootDocument()).thenReturn(root);
    Content content = mock(Content.class);

    when(request.getAttribute(SiteHelper.SITE_KEY)).thenReturn(site);
    when(settingsService.nestedSetting(
            List.of(ASSET_MANAGEMENT, ASSET_MANAGEMENT_DOWNLOAD_PORTAL, ASSET_MANAGEMENT_DOWNLOAD_PORTAL_ROOT_PAGE),
            Content.class, root))
            .thenReturn(content);

    facade.setSettingsService(settingsService);

    try (var mocked = mockStatic(FreemarkerEnvironment.class)) {
      mocked.when(FreemarkerEnvironment::getCurrentRequest).thenReturn(request);
      assertTrue(facade.hasDownloadPortal());
    }
  }

  @Test
  public void hasNoDownloadPortal() {
    when(request.getAttribute(SiteHelper.SITE_KEY)).thenReturn(null);

    try (var mocked = mockStatic(FreemarkerEnvironment.class)) {
      mocked.when(FreemarkerEnvironment::getCurrentRequest).thenReturn(request);
      assertFalse(facade.hasDownloadPortal());
    }
  }

  @Test
  public void downloadPortal() {
    DownloadPortal portal = mock(DownloadPortal.class);
    facade.setDownloadPortal(portal);
    assertEquals(portal, facade.getDownloadPortal());
  }
}
