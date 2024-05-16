import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import ImageEditorPropertyField from "@coremedia/studio-client.main.image-editor-components/ImageEditorPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import CategoryDocumentForm from "./containers/CategoryDocumentForm";
import DetailsDocumentForm from "./containers/DetailsDocumentForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import SEOForm from "./containers/SEOForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";
import MetaDataDocumentForm from "./media/MetaDataDocumentForm";

interface CMPictureFormConfig extends Config<DocumentTabPanel>, Partial<Pick<CMPictureForm,
  "contentType" |
  "folders"
>> {
}

class CMPictureForm extends DocumentTabPanel {
  declare Config: CMPictureFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmPictureForm";

  static readonly IMAGE_EDITOR_ITEM_ID: string = "imageEditor";

  static readonly COPYRIGHT_FORM_ITEM_ID: string = "copyrightFormItemId";

  static readonly EXTRAS_TAB_ITEM_ID: string = "extrasTab";

  constructor(config: Config<CMPictureForm> = null) {
    super(ConfigUtils.apply(Config(CMPictureForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(ImageEditorPropertyField, {
              itemId: CMPictureForm.IMAGE_EDITOR_ITEM_ID,
              propertyName: "data",
              imageSettingsPropertyName: "localSettings",
            }),
            Config(DetailsDocumentForm, { itemId: "detailsForm" }),
            Config(ViewTypeSelectorForm),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_extras_title,
          itemId: CMPictureForm.EXTRAS_TAB_ITEM_ID,
          items: [
            Config(CategoryDocumentForm),
            Config(PropertyFieldGroup, {
              collapsed: false,
              itemId: CMPictureForm.COPYRIGHT_FORM_ITEM_ID,
              title: CustomLabels_properties.PropertyGroup_Description_label,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              items: [
                Config(StringPropertyField, {
                  propertyName: "alt",
                  itemId: "alt",
                }),
                Config(StringPropertyField, {
                  propertyName: "copyright",
                  itemId: "copyright",
                }),
              ],
            }),
            Config(MetaDataDocumentForm, {
              propertyName: "data",
              metadataSectionName: "exif",
              label: BlueprintDocumentTypes_properties.Meta_data_exif,
            }),
            Config(SEOForm, { bindTo: config.bindTo }),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config));
  }

  /**
   * The content type to fill the tree with.
   */
  contentType: string = null;

  /**
   * The comma separated folder values to read the content from.
   */
  folders: string = null;
}

export default CMPictureForm;
