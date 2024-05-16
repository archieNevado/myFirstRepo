import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import DeleteKeywordActionBase from "./DeleteKeywordActionBase";

interface DeleteKeywordActionConfig extends Config<DeleteKeywordActionBase>, Partial<Pick<DeleteKeywordAction,
  "bindTo" |
  "selectionExpression"
>> {
}

class DeleteKeywordAction extends DeleteKeywordActionBase {
  declare Config: DeleteKeywordActionConfig;

  constructor(config: Config<DeleteKeywordAction> = null) {
    super(ConfigUtils.apply(Config(DeleteKeywordAction), config));
  }

  bindTo: ValueExpression = null;

  selectionExpression: ValueExpression = null;
}

export default DeleteKeywordAction;
