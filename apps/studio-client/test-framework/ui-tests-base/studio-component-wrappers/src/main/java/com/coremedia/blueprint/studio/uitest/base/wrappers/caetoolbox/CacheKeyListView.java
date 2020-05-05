package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import com.coremedia.uitesting.ext3.wrappers.selection.RowModel;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

@ExtJSObject(id = CacheKeyListView.ID)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CacheKeyListView extends GridPanel<RowModel> {
  public static final String ID = "cacheKeyListView";
  public static final String XTYPE = "com.coremedia.caetools.plugin.config.cacheKeyListView";
}
