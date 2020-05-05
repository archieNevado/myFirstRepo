package com.acme.coremedia.studio.dashboard {
import com.coremedia.cms.editor.sdk.dashboard.Reloadable;

import ext.container.Container;

public class ReloadableWidgetBase extends Container implements Reloadable{
  public function ReloadableWidgetBase(config:ReloadableWidget = null) {
    super(config);
  }

  public function reload():void {
  }
}
}
