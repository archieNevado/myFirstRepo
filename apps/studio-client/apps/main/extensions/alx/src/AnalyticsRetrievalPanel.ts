import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsProviderComboBox from "./AnalyticsProviderComboBox";
import AnalyticsStudioPlugin_properties from "./AnalyticsStudioPlugin_properties";

interface AnalyticsRetrievalPanelConfig extends Config<PropertyFieldGroup> {
}

class AnalyticsRetrievalPanel extends PropertyFieldGroup {
  declare Config: AnalyticsRetrievalPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.analyticsRetrievalPanel";

  constructor(config: Config<AnalyticsRetrievalPanel> = null) {
    super(ConfigUtils.apply(Config(AnalyticsRetrievalPanel, {
      title: AnalyticsStudioPlugin_properties.Retrieval_title,
      itemId: "analyticsRetrievalPanel",
      bindTo: config.bindTo,

      /* extend this one with retrieval options */
      items: [
        Config(AnalyticsProviderComboBox, { propertyName: "localSettings.analyticsProvider" }),
      ],
    }), config));
  }
}

export default AnalyticsRetrievalPanel;
