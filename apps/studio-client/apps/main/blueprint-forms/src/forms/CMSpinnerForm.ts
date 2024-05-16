import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentInfo";
import DocumentMetaDataFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentMetaDataFormDispatcher";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import CategoryDocumentForm from "./containers/CategoryDocumentForm";
import DetailsDocumentForm from "./containers/DetailsDocumentForm";
import LinkedSettingsForm from "./containers/LinkedSettingsForm";
import LocalSettingsForm from "./containers/LocalSettingsForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import SEOForm from "./containers/SEOForm";
import TeaserWithPictureDocumentForm from "./containers/TeaserWithPictureDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMSpinnerFormConfig extends Config<DocumentTabPanel> {
}

class CMSpinnerForm extends DocumentTabPanel {
  declare Config: CMSpinnerFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmSpinnerForm";

  /**
   * A constant for the linklist property name
   */
  static readonly SEQUENCE_PROPERTY_NAME: string = "sequence";

  static readonly COPYRIGHT_FORM_ITEM_ID: string = "copyrightFormItemId";

  constructor(config: Config<CMSpinnerForm> = null) {
    super(ConfigUtils.apply(Config(CMSpinnerForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(DetailsDocumentForm, {
              itemId: "detailsForm",
              collapsed: true,
            }),
            Config(TeaserWithPictureDocumentForm, { collapsed: true }),
            Config(PropertyFieldGroup, {
              itemId: "cmSpinnerPicturesForm",
              title: CustomLabels_properties.PropertyGroup_SpinnerPictures_label,
              items: [
                Config(LinkListPropertyField, {
                  linkType: "CMPicture",
                  hideLabel: true,
                  showThumbnails: true,
                  propertyName: CMSpinnerForm.SEQUENCE_PROPERTY_NAME,
                }),
              ],
            }),
            Config(ViewTypeSelectorForm),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_extras_title,
          itemId: "metadata",
          items: [
            Config(CategoryDocumentForm),
            Config(PropertyFieldGroup, {
              collapsed: false,
              itemId: CMSpinnerForm.COPYRIGHT_FORM_ITEM_ID,
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
            Config(SEOForm, { bindTo: config.bindTo }),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_system_title,
          itemId: "system",
          autoHide: true,
          items: [
            Config(DocumentInfo),
            Config(VersionHistory),
            Config(ReferrerListPanel),
            Config(LinkedSettingsForm, { collapsed: true }),
            Config(LocalSettingsForm, { collapsed: true }),
            Config(DocumentMetaDataFormDispatcher),
          ],
        }),
      ],

    }), config));
  }
}

export default CMSpinnerForm;
