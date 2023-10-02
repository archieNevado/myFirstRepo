import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EditTaxonomyActionBase from "./EditTaxonomyActionBase";

interface EditTaxonomyActionConfig extends Config<EditTaxonomyActionBase>, Partial<Pick<EditTaxonomyAction,
  "bindTo" |
  "taxonomyId"
>> {
}

class EditTaxonomyAction extends EditTaxonomyActionBase {
  declare Config: EditTaxonomyActionConfig;

  constructor(config: Config<EditTaxonomyAction> = null) {
    super(ConfigUtils.apply(Config(EditTaxonomyAction), config));
  }

  bindTo: ValueExpression = null;

  taxonomyId: string = null;
}

export default EditTaxonomyAction;
