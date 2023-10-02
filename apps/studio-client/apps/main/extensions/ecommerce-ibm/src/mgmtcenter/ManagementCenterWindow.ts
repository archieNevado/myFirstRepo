import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import Window from "@jangaroo/ext-ts/window/Window";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ManagementCenterFrame from "./ManagementCenterFrame";

interface ManagementCenterWindowConfig extends Config<Window> {
}

/**
 * A window containing the Management Center of the IBM WebSphere Commerce Server.
 */
class ManagementCenterWindow extends Window {
  declare Config: ManagementCenterWindowConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.ibm.studio.config.managementCenterWindow";

  /**
   * The id of the management center window
   */
  static readonly MANAGEMENT_CENTER_WINDOW_ID: string = "managementCenterWindow";

  /**
   * The id of the management center frame
   */
  static readonly MANAGEMENT_CENTER_FRAME_ID: string = "managementCenterFrame";

  constructor(config: Config<ManagementCenterWindow> = null) {
    super(ConfigUtils.apply(Config(ManagementCenterWindow, {
      title: LivecontextStudioPlugin_properties.Window_ManagementCenter_title,
      layout: "fit",
      id: ManagementCenterWindow.MANAGEMENT_CENTER_WINDOW_ID,
      closeAction: "hide",
      resizable: true,
      constrainHeader: true,
      stateful: true,
      stateId: ManagementCenterWindow.MANAGEMENT_CENTER_WINDOW_ID,
      stateEvents: ["hide", "show", "resize", "move"],

      items: [
        Config(ManagementCenterFrame, { id: ManagementCenterWindow.MANAGEMENT_CENTER_FRAME_ID }),
      ],

    }), config));
  }
}

export default ManagementCenterWindow;
