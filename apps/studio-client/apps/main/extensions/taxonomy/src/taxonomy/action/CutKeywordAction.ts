import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CutKeywordActionBase from "./CutKeywordActionBase";

interface CutKeywordActionConfig extends Config<CutKeywordActionBase>, Partial<Pick<CutKeywordAction,
  "bindTo" |
  "clipboardValueExpression" |
  "selectionExpression"
>> {
}

class CutKeywordAction extends CutKeywordActionBase {
  declare Config: CutKeywordActionConfig;

  constructor(config: Config<CutKeywordAction> = null) {
    super(ConfigUtils.apply(Config(CutKeywordAction), config));
  }

  bindTo: ValueExpression = null;

  clipboardValueExpression: ValueExpression = null;

  selectionExpression: ValueExpression = null;
}

export default CutKeywordAction;
