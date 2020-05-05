package com.coremedia.blueprint.personalization.include;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.personalization.contentbeans.CMP13NSearch;
import com.coremedia.blueprint.personalization.contentbeans.CMSelectionRules;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBean;

public class P13NIncludePredicate extends AbstractP13nContainerPredicate {

  private final SitesService sitesService;
  private final SettingsService settingsService;

  public P13NIncludePredicate(SitesService sitesService, SettingsService settingsService) {
    super();
    this.sitesService = sitesService;
    this.settingsService = settingsService;
  }

  @Override
  protected boolean isBeanMatching(Object bean) {
    if (bean instanceof CMSelectionRules || bean instanceof CMP13NSearch) {
      ContentBean contentBean = (ContentBean) bean;
      Site site = sitesService.getContentSiteAspect(contentBean.getContent()).getSite();
      if (site == null) {
        return false;
      }
      return settingsService.getSetting(P13NDynamicIncludeSettings.P13N_DYNAMIC_INCLUDES_ENABLED_SETTING, Boolean.class, site).orElse(false)
              && settingsService.getSetting(P13NDynamicIncludeSettings.P13N_DYNAMIC_INCLUDES_PER_ITEMS_SETTING, Boolean.class, site).orElse(false);
    }
    return false;
  }
}
