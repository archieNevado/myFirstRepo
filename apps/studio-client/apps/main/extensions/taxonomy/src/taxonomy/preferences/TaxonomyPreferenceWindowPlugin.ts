import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyPreferenceWindowPluginBase from "./TaxonomyPreferenceWindowPluginBase";

interface TaxonomyPreferenceWindowPluginConfig extends Config<TaxonomyPreferenceWindowPluginBase> {
}

/* no config required */
class TaxonomyPreferenceWindowPlugin extends TaxonomyPreferenceWindowPluginBase {
  declare Config: TaxonomyPreferenceWindowPluginConfig;

  constructor(config: Config<TaxonomyPreferenceWindowPlugin> = null) {
    super(ConfigUtils.apply(Config(TaxonomyPreferenceWindowPlugin), config));
  }
}

export default TaxonomyPreferenceWindowPlugin;
