import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenOptimizelyServiceUrlActionBase from "./OpenOptimizelyServiceUrlActionBase";

interface OpenOptimizelyServiceUrlActionConfig extends Config<OpenOptimizelyServiceUrlActionBase> {
}

class OpenOptimizelyServiceUrlAction extends OpenOptimizelyServiceUrlActionBase {
  declare Config: OpenOptimizelyServiceUrlActionConfig;

  constructor(config: Config<OpenOptimizelyServiceUrlAction> = null) {
    super(ConfigUtils.apply(Config(OpenOptimizelyServiceUrlAction), config));
  }
}

export default OpenOptimizelyServiceUrlAction;
