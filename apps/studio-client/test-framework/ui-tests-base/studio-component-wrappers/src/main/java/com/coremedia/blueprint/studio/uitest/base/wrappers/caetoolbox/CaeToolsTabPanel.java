package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.tab.TabPanel;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject(id = "caeToolsTabPanel")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CaeToolsTabPanel extends TabPanel {
  @Inject
  private CacheStatisticsTab cacheStatisticsTab;
  @Inject
  private CacheBrowserTab cacheBrowserTab;

  public void activateCacheBrowserTab() {
    setActiveTab(CacheBrowserTab.ID);
    cacheBrowserTab.visible().waitUntilTrue();
  }

  public CacheBrowserTab getCacheBrowserTab() {
    return cacheBrowserTab;
  }

  public void activateCacheStatisticsTab() {
    setActiveTab(CacheStatisticsTab.ID);
    cacheStatisticsTab.visible().waitUntilTrue();
  }

  public CacheStatisticsTab getCacheStatisticsTab() {
    return cacheStatisticsTab;
  }
}
