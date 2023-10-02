import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import AbstractPlugin from "@jangaroo/ext-ts/plugin/Abstract";
import TabPanel from "@jangaroo/ext-ts/tab/Panel";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CatalogPreferenceWindowPlugin from "./CatalogPreferenceWindowPlugin";
import CatalogPreferences from "./CatalogPreferences";

interface CatalogPreferenceWindowPluginBaseConfig extends Config<AbstractPlugin> {
}

class CatalogPreferenceWindowPluginBase extends AbstractPlugin {
  declare Config: CatalogPreferenceWindowPluginBaseConfig;

  constructor(config: Config<CatalogPreferenceWindowPlugin> = null) {
    super(config);
  }

  override init(component: Component): void {
    const prefWindow = as(component, Container);
    const tabPanel = as(prefWindow.getComponent(0), TabPanel);

    const prevPanel = new CatalogPreferences(Config(CatalogPreferences));
    tabPanel.add(prevPanel);
    tabPanel.updateLayout();
  }
}

export default CatalogPreferenceWindowPluginBase;
