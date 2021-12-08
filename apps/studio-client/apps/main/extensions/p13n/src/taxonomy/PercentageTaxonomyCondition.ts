import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import PercentageTaxonomyConditionBase from "./PercentageTaxonomyConditionBase";

interface PercentageTaxonomyConditionConfig extends Config<PercentageTaxonomyConditionBase> {
}

class PercentageTaxonomyCondition extends PercentageTaxonomyConditionBase {
  declare Config: PercentageTaxonomyConditionConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.percentageTaxonomyCondition";

  constructor(config: Config<PercentageTaxonomyCondition> = null) {
    super(ConfigUtils.apply(Config(PercentageTaxonomyCondition, { layout: Config(HBoxLayout) }), config));
  }
}

export default PercentageTaxonomyCondition;
