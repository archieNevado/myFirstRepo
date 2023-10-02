import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenInManagementCenterActionBase from "./OpenInManagementCenterActionBase";

interface OpenInManagementCenterActionConfig extends Config<OpenInManagementCenterActionBase> {
}

/**
 * This catalog object action can be used to open the catalog object in the IBM WCS Management Center view.
 * Currently only product and product variants are supported.
 *
 * @deprecated This action is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 * @deprecated
 */
class OpenInManagementCenterAction extends OpenInManagementCenterActionBase {
  declare Config: OpenInManagementCenterActionConfig;

  constructor(config: Config<OpenInManagementCenterAction> = null) {
    super(ConfigUtils.apply(Config(OpenInManagementCenterAction), config));
  }
}

export default OpenInManagementCenterAction;
