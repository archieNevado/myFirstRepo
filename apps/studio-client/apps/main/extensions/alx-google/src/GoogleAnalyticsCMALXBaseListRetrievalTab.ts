import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import GoogleAnalyticsRetrievalFields from "./GoogleAnalyticsRetrievalFields";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

interface GoogleAnalyticsCMALXBaseListRetrievalTabConfig extends Config<DocumentForm> {
}

class GoogleAnalyticsCMALXBaseListRetrievalTab extends DocumentForm {
  declare Config: GoogleAnalyticsCMALXBaseListRetrievalTabConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsCMALXBaseListRetrievalTab";

  constructor(config: Config<GoogleAnalyticsCMALXBaseListRetrievalTab> = null) {
    super(ConfigUtils.apply(Config(GoogleAnalyticsCMALXBaseListRetrievalTab, {
      itemId: "googleanalytics",
      title: GoogleAnalyticsStudioPlugin_properties.SpacerTitle_googleanalytics,

      items: [
        Config(GoogleAnalyticsRetrievalFields),
      ],

    }), config));
  }
}

export default GoogleAnalyticsCMALXBaseListRetrievalTab;
