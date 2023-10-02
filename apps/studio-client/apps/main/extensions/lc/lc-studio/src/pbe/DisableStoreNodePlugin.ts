import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import DisableStoreNodePluginBase from "./DisableStoreNodePluginBase";

interface DisableStoreNodePluginConfig extends Config<DisableStoreNodePluginBase> {
}

/**
 *
 * Plugin to disable Store breadcrumb element.
 *
 */
class DisableStoreNodePlugin extends DisableStoreNodePluginBase {
  declare Config: DisableStoreNodePluginConfig;

  constructor(config: Config<DisableStoreNodePlugin> = null) {
    super(ConfigUtils.apply(Config(DisableStoreNodePlugin), config));
  }
}

export default DisableStoreNodePlugin;
