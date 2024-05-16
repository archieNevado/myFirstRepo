import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyChangePluginBase from "./TaxonomyChangePluginBase";

interface TaxonomyChangePluginConfig extends Config<TaxonomyChangePluginBase> {
}

/**
 * @public
 */
class TaxonomyChangePlugin extends TaxonomyChangePluginBase {
  declare Config: TaxonomyChangePluginConfig;

  constructor(config: Config<TaxonomyChangePlugin> = null) {
    super(ConfigUtils.apply(Config(TaxonomyChangePlugin), config));
  }
}

export default TaxonomyChangePlugin;
