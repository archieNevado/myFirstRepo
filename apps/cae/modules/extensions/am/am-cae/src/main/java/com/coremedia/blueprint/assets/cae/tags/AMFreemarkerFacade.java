package com.coremedia.blueprint.assets.cae.tags;

import com.coremedia.blueprint.assets.cae.AMUtils;
import com.coremedia.blueprint.assets.cae.DownloadPortal;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.web.FreemarkerEnvironment;
import com.coremedia.cap.multisite.SiteHelper;

import javax.annotation.PostConstruct;

public class AMFreemarkerFacade {

  private SettingsService settingsService;

  private DownloadPortal downloadPortal;

  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  public void setDownloadPortal(DownloadPortal downloadPortal) {
    this.downloadPortal = downloadPortal;
  }

  @PostConstruct
  void initialize() {
    if (downloadPortal== null) {
      throw new IllegalStateException("Required property not set: downloadPortal");
    }
    if (settingsService == null) {
      throw new IllegalStateException("Required property not set: settingsService");
    }
  }

  public DownloadPortal getDownloadPortal() {
    return downloadPortal;
  }

  public boolean hasDownloadPortal() {
    return SiteHelper.findSite(FreemarkerEnvironment.getCurrentRequest())
            .map(site -> AMUtils.getDownloadPortalRootDocument(settingsService, site))
            .isPresent();
  }
}
