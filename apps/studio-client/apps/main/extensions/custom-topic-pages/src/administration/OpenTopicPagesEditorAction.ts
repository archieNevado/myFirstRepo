import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenTopicPagesEditorActionBase from "./OpenTopicPagesEditorActionBase";

interface OpenTopicPagesEditorActionConfig extends Config<OpenTopicPagesEditorActionBase> {
}

class OpenTopicPagesEditorAction extends OpenTopicPagesEditorActionBase {
  declare Config: OpenTopicPagesEditorActionConfig;

  constructor(config: Config<OpenTopicPagesEditorAction> = null) {
    super(ConfigUtils.apply(Config(OpenTopicPagesEditorAction), config));
  }
}

export default OpenTopicPagesEditorAction;
