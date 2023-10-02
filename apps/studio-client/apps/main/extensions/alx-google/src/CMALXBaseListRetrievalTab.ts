import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

interface CMALXBaseListRetrievalTabConfig extends Config<DocumentForm> {
}

class CMALXBaseListRetrievalTab extends DocumentForm {
  declare Config: CMALXBaseListRetrievalTabConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.googleanalytics.cmalxBaseListRetrievalTab";

  constructor(config: Config<CMALXBaseListRetrievalTab> = null) {
    super(ConfigUtils.apply(Config(CMALXBaseListRetrievalTab, {
      title: GoogleAnalyticsStudioPlugin_properties.SpacerTitle_googleanalytics,

      items: [
        Config(Container, {
          items: [
            Config(PropertyFieldGroup, {
              bindTo: config.bindTo,
              header: false,
              itemId: "propertyFieldGroup",
              items: [

              ],
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default CMALXBaseListRetrievalTab;
