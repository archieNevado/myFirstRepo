package com.coremedia.blueprint.assets.cae.tags;

import com.coremedia.blueprint.assets.cae.DownloadPortal;
import com.coremedia.blueprint.assets.cae.AMUtils;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.objectserver.view.freemarker.FreemarkerUtils;
import org.springframework.beans.factory.annotation.Required;

public class AMFreemarkerFacade {

  private SettingsService settingsService;

  private AMMessageKeysFreemarker amMessageKeysFreemarker = new AMMessageKeysFreemarker();

  private DownloadPortal downloadPortal;

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setDownloadPortal(DownloadPortal downloadPortal) {
    this.downloadPortal = downloadPortal;
  }

  public DownloadPortal getDownloadPortal() {
    return downloadPortal;
  }

  public boolean hasDownloadPortal() {
    Site siteFromRequest = SiteHelper.getSiteFromRequest(FreemarkerUtils.getCurrentRequest());
    if (null == siteFromRequest) {
      return false;
    }
    Content downloadPortalRootDocument = AMUtils.getDownloadPortalRootDocument(settingsService, siteFromRequest);
    return null != downloadPortalRootDocument;
  }

  public AMMessageKeysFreemarker getAmMessageKeys() {
    return amMessageKeysFreemarker;
  }
}
