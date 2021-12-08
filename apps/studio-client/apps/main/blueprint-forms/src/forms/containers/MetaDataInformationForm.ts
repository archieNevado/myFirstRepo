import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentInfo";
import DocumentMetaDataFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentMetaDataFormDispatcher";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../../BlueprintTabs_properties";

interface MetaDataInformationFormConfig extends Config<DocumentForm> {
}

class MetaDataInformationForm extends DocumentForm {
  declare Config: MetaDataInformationFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.metaDataInformationForm";

  constructor(config: Config<MetaDataInformationForm> = null) {
    super(ConfigUtils.apply(Config(MetaDataInformationForm, {
      title: BlueprintTabs_properties.Tab_system_title,
      itemId: "system",
      autoHide: true,

      items: [
        Config(DocumentInfo),
        Config(VersionHistory),
        Config(ReferrerListPanel),
        Config(DocumentMetaDataFormDispatcher, { bindTo: config.bindTo }),
      ],

    }), config));
  }
}

export default MetaDataInformationForm;
