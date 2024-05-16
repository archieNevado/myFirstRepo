import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AddKeywordActionBase from "./AddKeywordActionBase";

interface AddKeywordActionConfig extends Config<AddKeywordActionBase>, Partial<Pick<AddKeywordAction,
  "bindTo" |
  "selectionExpression" |
  "propertyName" |
  "taxonomyId"
>> {
}

class AddKeywordAction extends AddKeywordActionBase {
  declare Config: AddKeywordActionConfig;

  constructor(config: Config<AddKeywordAction> = null) {
    super(ConfigUtils.apply(Config(AddKeywordAction), config));
  }

  bindTo: ValueExpression = null;

  selectionExpression: ValueExpression = null;

  propertyName: string = null;

  taxonomyId: string = null;
}

export default AddKeywordAction;
