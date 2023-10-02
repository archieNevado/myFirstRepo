import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import RemoveTaxonomyActionBase from "./RemoveTaxonomyActionBase";

interface RemoveTaxonomyActionConfig extends Config<RemoveTaxonomyActionBase>, Partial<Pick<RemoveTaxonomyAction,
  "selectedValuesExpression" |
  "selectedPositionsExpression" |
  "bindTo" |
  "propertyName"
>> {
}

class RemoveTaxonomyAction extends RemoveTaxonomyActionBase {
  declare Config: RemoveTaxonomyActionConfig;

  constructor(config: Config<RemoveTaxonomyAction> = null) {
    super(ConfigUtils.apply(Config(RemoveTaxonomyAction), config));
  }

  selectedValuesExpression: ValueExpression = null;

  selectedPositionsExpression: ValueExpression = null;

  bindTo: ValueExpression = null;

  propertyName: string = null;
}

export default RemoveTaxonomyAction;
