import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenManagementCenterActionBase from "./OpenManagementCenterActionBase";

interface OpenManagementCenterActionConfig extends Config<OpenManagementCenterActionBase> {
}

/**
 * @deprecated This action is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 * @deprecated
 */
class OpenManagementCenterAction extends OpenManagementCenterActionBase {
  declare Config: OpenManagementCenterActionConfig;

  constructor(config: Config<OpenManagementCenterAction> = null) {
    super(ConfigUtils.apply(Config(OpenManagementCenterAction), config));
  }
}

export default OpenManagementCenterAction;
