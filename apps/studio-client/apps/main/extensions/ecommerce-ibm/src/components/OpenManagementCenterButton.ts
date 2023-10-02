import Button from "@jangaroo/ext-ts/button/Button";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenManagementCenterAction from "../action/OpenManagementCenterAction";

interface OpenManagementCenterButtonConfig extends Config<Button> {
}

/**
 * @deprecated This component is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 * @deprecated
 */
class OpenManagementCenterButton extends Button {
  declare Config: OpenManagementCenterButtonConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.ibm.studio.config.openManagementCenterButton";

  constructor(config: Config<OpenManagementCenterButton> = null) {
    super(ConfigUtils.apply(Config(OpenManagementCenterButton, {
      iconAlign: "top",

      baseAction: new OpenManagementCenterAction({}),

    }), config));
  }
}

export default OpenManagementCenterButton;
