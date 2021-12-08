import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenAnalyticsDeepLinkUrlButtonBase from "./OpenAnalyticsDeepLinkUrlButtonBase";

interface OpenAnalyticsDeepLinkUrlButtonConfig extends Config<OpenAnalyticsDeepLinkUrlButtonBase> {
}

/**
 * Button to open a new browser window with the analytics report deep link for the current preview document.
 */
class OpenAnalyticsDeepLinkUrlButton extends OpenAnalyticsDeepLinkUrlButtonBase {
  declare Config: OpenAnalyticsDeepLinkUrlButtonConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.openAnalyticsDeepLinkUrlButton";

  constructor(config: Config<OpenAnalyticsDeepLinkUrlButton> = null) {
    super(ConfigUtils.apply(Config(OpenAnalyticsDeepLinkUrlButton, {
      iconCls: CoreIcons_properties.analytics,
      disabled: true,

    }), config));
  }
}

export default OpenAnalyticsDeepLinkUrlButton;
