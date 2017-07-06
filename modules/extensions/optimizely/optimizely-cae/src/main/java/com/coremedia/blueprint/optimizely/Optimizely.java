package com.coremedia.blueprint.optimizely;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.util.ExtensionsAspectUtil;

/**
 * @cm.template.api
 */
public class Optimizely {
  private Page page;
  private SettingsService settingsService;

  public Optimizely(Page page, SettingsService settingsService) {
    this.page = page;
    this.settingsService = settingsService;
  }

  /**
   * @cm.template.api
   */
  public boolean isEnabled() {
    return settingsService.settingWithDefault("optimizely.enabled", Boolean.class, false, page.getNavigation()) &&
            ExtensionsAspectUtil.isFeatureConfigured(getOptimizelyId());
  }

  /**
   * @cm.template.api
   */
  public String getOptimizelyId() {
    return settingsService.settingWithDefault("optimizely.id" + ExtensionsAspectUtil.EXTERNAL_ACCOUNT, String.class, "", page.getNavigation());
  }
}
