import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import DataDocumentForm from "./containers/DataDocumentForm";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import RelatedDocumentForm from "./containers/RelatedDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMInteractiveFormConfig extends Config<DocumentTabPanel> {
}

class CMInteractiveForm extends DocumentTabPanel {
  declare Config: CMInteractiveFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmInteractiveForm";

  constructor(config: Config<CMInteractiveForm> = null) {
    super(ConfigUtils.apply(Config(CMInteractiveForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(DataDocumentForm, {
              title: CustomLabels_properties.PropertyGroup_InteractiveData_label,
              helpText: BlueprintDocumentTypes_properties.CMInteractive_data_helpText,
            }),
            Config(CollapsibleStringPropertyForm, {
              propertyName: "copyright",
              title: BlueprintDocumentTypes_properties.CMMedia_copyright_text,
            }),
            Config(MediaDocumentForm),
            Config(RelatedDocumentForm, { collapsed: true }),
            Config(ViewTypeSelectorForm, { collapsed: true }),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config));
  }
}

export default CMInteractiveForm;
