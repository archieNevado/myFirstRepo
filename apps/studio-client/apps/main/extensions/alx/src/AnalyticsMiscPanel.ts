import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsStudioPlugin_properties from "./AnalyticsStudioPlugin_properties";

interface AnalyticsMiscPanelConfig extends Config<PropertyFieldGroup> {
}

class AnalyticsMiscPanel extends PropertyFieldGroup {
  declare Config: AnalyticsMiscPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.analyticsMiscPanel";

  constructor(config: Config<AnalyticsMiscPanel> = null) {
    super(ConfigUtils.apply(Config(AnalyticsMiscPanel, {
      title: AnalyticsStudioPlugin_properties.Misc_title,
      itemId: "analyticsMiscPanel",

      items: [
        /* extend this one with miscellaneous options */
      ],

    }), config));
  }
}

export default AnalyticsMiscPanel;
