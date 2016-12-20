package com.coremedia.blueprint.studio.taxonomy.preferences {

import ext.Component;
import ext.Plugin;
import ext.container.Container;
import ext.tab.TabPanel;

public class TaxonomyPreferenceWindowPluginBase implements Plugin {

  public function TaxonomyPreferenceWindowPluginBase() {
    super();
  }

  public function init(component:Component):void {
    var prefWindow:Container = component as Container;
    var tabPanel:TabPanel = prefWindow.getComponent(0) as TabPanel;

    var prevPanel:TaxonomyPreferences = new TaxonomyPreferences(TaxonomyPreferences({}));
    tabPanel.add(prevPanel);
    tabPanel.updateLayout();
  }
}
}

