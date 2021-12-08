import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import WebtrendsRetrievalFields from "./WebtrendsRetrievalFields";
import WebtrendsStudioPlugin_properties from "./WebtrendsStudioPlugin_properties";

interface WebtrendsCMALXBaseListRetrievalTabConfig extends Config<DocumentForm> {
}

class WebtrendsCMALXBaseListRetrievalTab extends DocumentForm {
  declare Config: WebtrendsCMALXBaseListRetrievalTabConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.webtrends.webtrendsCMALXBaseListRetrievalTab";

  constructor(config: Config<WebtrendsCMALXBaseListRetrievalTab> = null) {
    super(ConfigUtils.apply(Config(WebtrendsCMALXBaseListRetrievalTab, {
      title: WebtrendsStudioPlugin_properties.SpacerTitle_webtrends,

      items: [
        Config(WebtrendsRetrievalFields),
      ],

    }), config));
  }
}

export default WebtrendsCMALXBaseListRetrievalTab;
