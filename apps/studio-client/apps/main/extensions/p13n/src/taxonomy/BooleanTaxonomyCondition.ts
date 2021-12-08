import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BooleanTaxonomyConditionBase from "./BooleanTaxonomyConditionBase";

interface BooleanTaxonomyConditionConfig extends Config<BooleanTaxonomyConditionBase> {
}

class BooleanTaxonomyCondition extends BooleanTaxonomyConditionBase {
  declare Config: BooleanTaxonomyConditionConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.booleanTaxonomyCondition";

  constructor(config: Config<BooleanTaxonomyCondition> = null) {
    super(ConfigUtils.apply(Config(BooleanTaxonomyCondition), config));
  }
}

export default BooleanTaxonomyCondition;
