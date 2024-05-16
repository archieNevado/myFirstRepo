import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CMArticleSystemForm from "./components/CMArticleSystemForm";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import AuthorLinkListDocumentForm from "./containers/AuthorLinkListDocumentForm";
import DetailsDocumentForm from "./containers/DetailsDocumentForm";
import ExternallyVisibleDateForm from "./containers/ExternallyVisibleDateForm";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import RelatedDocumentForm from "./containers/RelatedDocumentForm";
import TeaserDocumentForm from "./containers/TeaserDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMArticleFormConfig extends Config<DocumentTabPanel> {
}

class CMArticleForm extends DocumentTabPanel {
  declare Config: CMArticleFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmArticleForm";

  constructor(config: Config<CMArticleForm> = null) {
    super(ConfigUtils.apply(Config(CMArticleForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(DetailsDocumentForm, { bindTo: config.bindTo }),
            Config(TeaserDocumentForm, {
              bindTo: config.bindTo,
              collapsed: true,
            }),
            Config(MediaDocumentForm, { bindTo: config.bindTo }),
            Config(AuthorLinkListDocumentForm, { bindTo: config.bindTo }),
            Config(RelatedDocumentForm, { bindTo: config.bindTo }),
            Config(ViewTypeSelectorForm, { bindTo: config.bindTo }),
            Config(ExternallyVisibleDateForm, { bindTo: config.bindTo }),
            Config(ValidityDocumentForm, { bindTo: config.bindTo }),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm, { bindTo: config.bindTo }),
        Config(CMArticleSystemForm, { bindTo: config.bindTo }),
      ],

    }), config));
  }
}

export default CMArticleForm;
