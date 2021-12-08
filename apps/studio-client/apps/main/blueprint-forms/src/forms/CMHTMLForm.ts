import CKEditorTypes from "@coremedia/studio-client.ckeditor-constants/CKEditorTypes";
import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import MetaDataInformationForm from "./containers/MetaDataInformationForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";

interface CMHTMLFormConfig extends Config<DocumentTabPanel> {
}

class CMHTMLForm extends DocumentTabPanel {
  declare Config: CMHTMLFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmhtmlForm";

  constructor(config: Config<CMHTMLForm> = null) {
    super(ConfigUtils.apply(Config(CMHTMLForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: CustomLabels_properties.PropertyGroup_Details_label,
              itemId: "detailsDocumentForm",
              propertyNames: ["teaserTitle", "description"],
              items: [
                Config(StringPropertyField, {
                  propertyName: "teaserTitle",
                  itemId: "teaserTitle",
                }),
                Config(StringPropertyField, {
                  propertyName: "description",
                  itemId: "description",
                }),
              ],
            }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMHTML_data_text,
              itemId: "cmHtmlDataForm",
              items: [
                Config(RichTextPropertyField, {
                  itemId: "data",
                  hideLabel: true,
                  editorType: CKEditorTypes.NO_TOOLBAR_EDITOR_TYPE,
                  propertyName: "data",
                }),
              ],
            }),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMHTMLForm;
