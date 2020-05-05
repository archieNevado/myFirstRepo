package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject(id = CacheBrowserTab.ID)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CacheBrowserTab extends Panel {
  public static final String ID = "cacheBrowserTab";
  public static final String XTYPE = "com.coremedia.caetools.plugin.config.cacheBrowserTab";

  @Inject
  private CacheKeyListView cacheKeyListView;
  @Inject
  private SelectedEntryView selectedEntryView;

  public CacheKeyListView getCacheKeyListView() {
    return cacheKeyListView;
  }

  public SelectedEntryView getSelectedEntryView() {
    return selectedEntryView;
  }
}
