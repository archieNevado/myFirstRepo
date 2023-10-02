import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ManagementCenterFrameBase from "./ManagementCenterFrameBase";

interface ManagementCenterFrameConfig extends Config<ManagementCenterFrameBase>, Partial<Pick<ManagementCenterFrame,
  "src" |
  "tooltip"
>> {
}

/**
 * This component contains an iframe to integrate the IBM WebSphere Commerce Management Center into the Studio.
 */
class ManagementCenterFrame extends ManagementCenterFrameBase {
  declare Config: ManagementCenterFrameConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.ibm.studio.config.managementCenterFrame";

  constructor(config: Config<ManagementCenterFrame> = null) {
    super(ConfigUtils.apply(Config(ManagementCenterFrame), config));
  }

  src: string = null;

  tooltip: string = null;
}

export default ManagementCenterFrame;
