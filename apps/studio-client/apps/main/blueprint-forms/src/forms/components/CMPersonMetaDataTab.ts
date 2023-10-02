import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../../BlueprintTabs_properties";
import PersonSEOForm from "../containers/PersonSEOForm";

interface CMPersonMetaDataTabConfig extends Config<DocumentForm> {
}

class CMPersonMetaDataTab extends DocumentForm {
  declare Config: CMPersonMetaDataTabConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.personMetadataTab";

  constructor(config: Config<CMPersonMetaDataTab> = null) {
    super(ConfigUtils.apply(Config(CMPersonMetaDataTab, {
      title: BlueprintTabs_properties.Tab_extras_title,
      itemId: "metadata",

      items: [
        Config(PersonSEOForm, {
          bindTo: config.bindTo,
          delegatePropertyNames: ["firstName", "lastName", "displayName"],
        }),
      ],

    }), config));
  }
}

export default CMPersonMetaDataTab;
