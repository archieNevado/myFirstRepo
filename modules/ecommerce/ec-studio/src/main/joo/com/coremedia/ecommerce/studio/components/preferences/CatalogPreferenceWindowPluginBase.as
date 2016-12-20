package com.coremedia.ecommerce.studio.components.preferences {
import ext.Component;
import ext.Plugin;
import ext.container.Container;
import ext.tab.TabPanel;

public class CatalogPreferenceWindowPluginBase implements Plugin {

  public function CatalogPreferenceWindowPluginBase() {
    super();
  }

  public function init(component:Component):void {
    var prefWindow:Container = component as Container;
    var tabPanel:TabPanel = prefWindow.getComponent(0) as TabPanel;

      var prevPanel:CatalogPreferences = new CatalogPreferences(CatalogPreferences({}));
      tabPanel.add(prevPanel);
      tabPanel.updateLayout();
  }
}
}

