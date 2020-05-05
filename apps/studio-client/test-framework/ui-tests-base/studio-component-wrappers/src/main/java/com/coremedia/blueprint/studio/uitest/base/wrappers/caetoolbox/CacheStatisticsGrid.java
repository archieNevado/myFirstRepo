package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import com.coremedia.uitesting.ext3.wrappers.selection.RowModel;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CacheStatisticsGrid extends GridPanel<RowModel> {
  public void stopUpdates() {
    evalVoid("self.stopUpdates()");
  }

  public void forceUpdate() {
    evalVoid("self.forceUpdate()");
  }

  public void startUpdates() {
    evalVoid("self.startUpdates()");
  }
}
