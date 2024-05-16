import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentInfo";
import DocumentMetaDataFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentMetaDataFormDispatcher";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../../BlueprintTabs_properties";

interface MetaDataWithoutSettingsFormConfig extends Config<DocumentForm> {
}

class MetaDataWithoutSettingsForm extends DocumentForm {
  declare Config: MetaDataWithoutSettingsFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.metaDataWithoutSettingsForm";

  constructor(config: Config<MetaDataWithoutSettingsForm> = null) {
    super(ConfigUtils.apply(Config(MetaDataWithoutSettingsForm, {
      title: BlueprintTabs_properties.Tab_system_title,
      itemId: "system",
      autoHide: true,

      items: [
        Config(DocumentInfo),
        Config(VersionHistory),
        Config(ReferrerListPanel),
        Config(DocumentMetaDataFormDispatcher),
      ],

    }), config));
  }
}

export default MetaDataWithoutSettingsForm;
