import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsStudioPlugin_properties from "./AnalyticsStudioPlugin_properties";

interface AnalyticsTrackingPanelConfig extends Config<PropertyFieldGroup> {
}

class AnalyticsTrackingPanel extends PropertyFieldGroup {
  declare Config: AnalyticsTrackingPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.analyticsTrackingPanel";

  constructor(config: Config<AnalyticsTrackingPanel> = null) {
    super(ConfigUtils.apply(Config(AnalyticsTrackingPanel, {
      title: AnalyticsStudioPlugin_properties.Tracking_title,
      itemId: "analyticsTrackingPanel",

      items: [
        /* extend this one with tracking options */
      ],

    }), config));
  }
}

export default AnalyticsTrackingPanel;
