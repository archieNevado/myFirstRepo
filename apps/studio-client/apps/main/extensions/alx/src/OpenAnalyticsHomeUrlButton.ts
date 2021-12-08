import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenAnalyticsUrlButtonBase from "./OpenAnalyticsUrlButtonBase";

interface OpenAnalyticsHomeUrlButtonConfig extends Config<OpenAnalyticsUrlButtonBase> {
}

/**
 * Button to open a new browser window with the analytics home url for the configured service provider.
 */
class OpenAnalyticsHomeUrlButton extends OpenAnalyticsUrlButtonBase {
  declare Config: OpenAnalyticsHomeUrlButtonConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.openAnalyticsHomeUrlButton";

  constructor(config: Config<OpenAnalyticsHomeUrlButton> = null) {
    super(ConfigUtils.apply(Config(OpenAnalyticsHomeUrlButton, {
      iconCls: CoreIcons_properties.analytics,
      disabled: true,

    }), config));
  }
}

export default OpenAnalyticsHomeUrlButton;
