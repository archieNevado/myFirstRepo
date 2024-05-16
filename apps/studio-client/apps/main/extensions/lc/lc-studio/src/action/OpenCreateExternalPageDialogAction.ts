import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenCreateExternalPageDialogActionBase from "./OpenCreateExternalPageDialogActionBase";

interface OpenCreateExternalPageDialogActionConfig extends Config<OpenCreateExternalPageDialogActionBase> {
}

class OpenCreateExternalPageDialogAction extends OpenCreateExternalPageDialogActionBase {
  declare Config: OpenCreateExternalPageDialogActionConfig;

  constructor(config: Config<OpenCreateExternalPageDialogAction> = null) {
    super(ConfigUtils.apply(Config(OpenCreateExternalPageDialogAction), config));
  }
}

export default OpenCreateExternalPageDialogAction;
