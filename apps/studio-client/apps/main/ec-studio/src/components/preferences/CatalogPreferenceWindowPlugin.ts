import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogPreferenceWindowPluginBase from "./CatalogPreferenceWindowPluginBase";

interface CatalogPreferenceWindowPluginConfig extends Config<CatalogPreferenceWindowPluginBase> {
}

/* no config required */
class CatalogPreferenceWindowPlugin extends CatalogPreferenceWindowPluginBase {
  declare Config: CatalogPreferenceWindowPluginConfig;

  constructor(config: Config<CatalogPreferenceWindowPlugin> = null) {
    super(ConfigUtils.apply(Config(CatalogPreferenceWindowPlugin), config));
  }
}

export default CatalogPreferenceWindowPlugin;
