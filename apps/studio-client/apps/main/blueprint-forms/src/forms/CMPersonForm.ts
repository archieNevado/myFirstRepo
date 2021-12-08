import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CMArticleSystemForm from "./components/CMArticleSystemForm";
import CMPersonMetaDataTab from "./components/CMPersonMetaDataTab";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MiscSettingsForm from "./containers/MiscSettingsForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import PersonDetailsDocumentForm from "./containers/PersonDetailsDocumentForm";
import RelatedDocumentForm from "./containers/RelatedDocumentForm";

interface CMPersonFormConfig extends Config<DocumentTabPanel> {
}

class CMPersonForm extends DocumentTabPanel {
  declare Config: CMPersonFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmPersonForm";

  constructor(config: Config<CMPersonForm> = null) {
    super(ConfigUtils.apply(Config(CMPersonForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PersonDetailsDocumentForm, { bindTo: config.bindTo }),
            Config(MediaDocumentForm, { bindTo: config.bindTo }),
            Config(MiscSettingsForm, { collapsed: true }),
            Config(RelatedDocumentForm, { bindTo: config.bindTo }),
          ],
        }),
        Config(CMPersonMetaDataTab),
        Config(MultiLanguageDocumentForm, { bindTo: config.bindTo }),
        Config(CMArticleSystemForm, { bindTo: config.bindTo }),
      ],

    }), config));
  }
}

export default CMPersonForm;
