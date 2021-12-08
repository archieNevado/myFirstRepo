import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsMiscPanel from "./AnalyticsMiscPanel";
import AnalyticsRetrievalPanel from "./AnalyticsRetrievalPanel";
import AnalyticsStudioPlugin_properties from "./AnalyticsStudioPlugin_properties";
import AnalyticsTrackingPanel from "./AnalyticsTrackingPanel";

interface CMChannelAnalyticsTabConfig extends Config<DocumentForm> {
}

class CMChannelAnalyticsTab extends DocumentForm {
  declare Config: CMChannelAnalyticsTabConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.cmChannelAnalyticsTab";

  constructor(config: Config<CMChannelAnalyticsTab> = null) {
    super(ConfigUtils.apply(Config(CMChannelAnalyticsTab, {
      title: AnalyticsStudioPlugin_properties.Tab_analytics_title,

      items: [
        Config(AnalyticsTrackingPanel, { bindTo: config.bindTo }),
        Config(AnalyticsRetrievalPanel, {
          bindTo: config.bindTo,
          collapsed: true,
        }),
        Config(AnalyticsMiscPanel, {
          bindTo: config.bindTo,
          collapsed: true,
        }),
      ],

    }), config));
  }
}

export default CMChannelAnalyticsTab;
