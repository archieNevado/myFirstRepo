import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BlobPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CMDownloadSystemForm from "./components/CMDownloadSystemForm";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import DetailsDocumentForm from "./containers/DetailsDocumentForm";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import RelatedDocumentForm from "./containers/RelatedDocumentForm";
import TeaserDocumentForm from "./containers/TeaserDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMDownloadFormConfig extends Config<DocumentTabPanel> {
}

class CMDownloadForm extends DocumentTabPanel {
  declare Config: CMDownloadFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmDownloadForm";

  constructor(config: Config<CMDownloadForm> = null) {
    super(ConfigUtils.apply(Config(CMDownloadForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(DetailsDocumentForm),
            Config(TeaserDocumentForm, { collapsed: true }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMDownload_data_text,
              itemId: "cmDownloadDataForm",
              items: [
                Config(BlobPropertyField, {
                  bindTo: config.bindTo,
                  hideLabel: true,
                  propertyName: "data",
                }),
                Config(StringPropertyField, {
                  propertyName: "filename",
                  itemId: "filename",
                }),
              ],
            }),
            Config(MediaDocumentForm, {
              collapsed: true,
              expandOnValues: "pictures",
            }),
            Config(RelatedDocumentForm, {
              collapsed: true,
              expandOnValues: "related",
            }),
            Config(ValidityDocumentForm),
            Config(ViewTypeSelectorForm),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(CMDownloadSystemForm, { bindTo: config.bindTo }),
      ],

    }), config));
  }
}

export default CMDownloadForm;
