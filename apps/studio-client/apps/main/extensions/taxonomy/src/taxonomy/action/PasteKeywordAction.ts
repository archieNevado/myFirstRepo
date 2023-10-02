import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import PasteKeywordActionBase from "./PasteKeywordActionBase";

interface PasteKeywordActionConfig extends Config<PasteKeywordActionBase>, Partial<Pick<PasteKeywordAction,
  "clipboardValueExpression" |
  "selectionExpression"
>> {
}

class PasteKeywordAction extends PasteKeywordActionBase {
  declare Config: PasteKeywordActionConfig;

  constructor(config: Config<PasteKeywordAction> = null) {
    super(ConfigUtils.apply(Config(PasteKeywordAction), config));
  }

  clipboardValueExpression: ValueExpression = null;

  selectionExpression: ValueExpression = null;
}

export default PasteKeywordAction;
