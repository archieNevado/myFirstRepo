import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import AbstractPlugin from "@jangaroo/ext-ts/plugin/Abstract";
import TabPanel from "@jangaroo/ext-ts/tab/Panel";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyPreferenceWindowPlugin from "./TaxonomyPreferenceWindowPlugin";
import TaxonomyPreferences from "./TaxonomyPreferences";

interface TaxonomyPreferenceWindowPluginBaseConfig extends Config<AbstractPlugin> {
}

class TaxonomyPreferenceWindowPluginBase extends AbstractPlugin {
  declare Config: TaxonomyPreferenceWindowPluginBaseConfig;

  constructor(config: Config<TaxonomyPreferenceWindowPlugin> = null) {
    super(config);
  }

  override init(component: Component): void {
    const prefWindow = as(component, Container);
    const tabPanel = as(prefWindow.getComponent(0), TabPanel);

    const prevPanel = new TaxonomyPreferences(Config(TaxonomyPreferences));
    tabPanel.add(prevPanel);
    tabPanel.updateLayout();
  }
}

export default TaxonomyPreferenceWindowPluginBase;
