import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentInfo";
import DocumentMetaDataFormDispatcher
  from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentMetaDataFormDispatcher";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../../BlueprintTabs_properties";
import LinkedSettingsForm from "../containers/LinkedSettingsForm";
import LocalSettingsForm from "../containers/LocalSettingsForm";
import SearchableForm from "../containers/SearchableForm";

interface CMArticleSystemFormConfig extends Config<DocumentForm> {
}

class CMArticleSystemForm extends DocumentForm {
  declare Config: CMArticleSystemFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmArticleSystemForm";

  constructor(config: Config<CMArticleSystemForm> = null) {
    super(ConfigUtils.apply(Config(CMArticleSystemForm, {
      title: BlueprintTabs_properties.Tab_system_title,
      itemId: "system",
      autoHide: true,

      items: [
        Config(DocumentInfo),
        Config(VersionHistory),
        Config(ReferrerListPanel),
        Config(SearchableForm, { collapsed: true }),
        Config(LinkedSettingsForm, { collapsed: true }),
        Config(LocalSettingsForm, { collapsed: true }),
        Config(DocumentMetaDataFormDispatcher),
      ],

    }), config));
  }
}

export default CMArticleSystemForm;
