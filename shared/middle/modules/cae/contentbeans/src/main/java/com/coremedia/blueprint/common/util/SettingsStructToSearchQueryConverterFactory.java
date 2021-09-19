package com.coremedia.blueprint.common.util;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMQueryList;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanFactory;

/**
 * Factory class for SettingsStructToSearchQueryConverter.
 */
public class SettingsStructToSearchQueryConverterFactory {

  /**
   * Create a new instance of SettingsStructToSearchQueryConverter.
   * @param queryList the query list
   * @param sitesService the sites service
   * @param settingsService the settings service
   * @param contentRepository the content repository
   * @param contentBeanFactory the content bean factory
   * @param unlimited true to ignore limit (default limit will be applied)
   * @return a new instance of SettingsStructToSearchQueryConverter
   */
  public SettingsStructToSearchQueryConverter newInstance(CMQueryList queryList,
                                                          SitesService sitesService,
                                                          SettingsService settingsService,
                                                          ContentRepository contentRepository,
                                                          ContentBeanFactory contentBeanFactory,
                                                          boolean unlimited) {
    return new SettingsStructToSearchQueryConverter(queryList,
            sitesService,
            settingsService,
            contentRepository,
            contentBeanFactory,
            unlimited);
  }
}
