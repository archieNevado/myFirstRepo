import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentInfo";
import DocumentMetaDataFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentMetaDataFormDispatcher";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../../BlueprintTabs_properties";
import LinkedSettingsForm from "./LinkedSettingsForm";
import LocalSettingsForm from "./LocalSettingsForm";

interface MetaDataWithoutSearchableFormConfig extends Config<DocumentForm> {
}

class MetaDataWithoutSearchableForm extends DocumentForm {
  declare Config: MetaDataWithoutSearchableFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.metaDataWithoutSearchableForm";

  constructor(config: Config<MetaDataWithoutSearchableForm> = null) {
    super(ConfigUtils.apply(Config(MetaDataWithoutSearchableForm, {
      title: BlueprintTabs_properties.Tab_system_title,
      itemId: "system",
      autoHide: true,

      items: [
        Config(DocumentInfo),
        Config(VersionHistory),
        Config(ReferrerListPanel),
        Config(DocumentMetaDataFormDispatcher),
        Config(LinkedSettingsForm, { collapsed: true }),
        Config(LocalSettingsForm, { collapsed: true }),
      ],

    }), config));
  }
}

export default MetaDataWithoutSearchableForm;
