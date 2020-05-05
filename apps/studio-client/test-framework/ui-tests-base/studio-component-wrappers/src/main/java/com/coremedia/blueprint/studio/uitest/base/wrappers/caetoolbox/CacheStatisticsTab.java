package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

@ExtJSObject(id = CacheStatisticsTab.ID)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CacheStatisticsTab extends Panel {
  public static final String ID = "cacheStatisticsTab";
  public static final String XTYPE = "com.coremedia.caetools.plugin.config.cacheStatisticsTab";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cacheStatisticsGrid")
  private CacheStatisticsGrid cacheStatisticsGrid;

  public CacheStatisticsGrid getCacheStatisticsGrid() {
    return cacheStatisticsGrid;
  }
}
