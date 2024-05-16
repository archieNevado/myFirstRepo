import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import CategoryDocumentForm from "./containers/CategoryDocumentForm";
import DataDocumentForm from "./containers/DataDocumentForm";
import DetailsDocumentForm from "./containers/DetailsDocumentForm";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import PlayerSettingsPropertyGroup from "./containers/PlayerSettingsPropertyGroup";
import RelatedDocumentForm from "./containers/RelatedDocumentForm";
import SEOForm from "./containers/SEOForm";
import TeaserDocumentForm from "./containers/TeaserDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMVideoFormConfig extends Config<DocumentTabPanel> {
}

class CMVideoForm extends DocumentTabPanel {
  declare Config: CMVideoFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmVideoForm";

  static readonly COPYRIGHT_FORM_ITEM_ID: string = "copyrightFormItemId";

  static readonly EXTRAS_TAB_ITEM_ID: string = "extrasTab";

  static readonly CONTENT_TAB_ITEM_ID: string = "contentTab";

  constructor(config: Config<CMVideoForm> = null) {
    super(ConfigUtils.apply(Config(CMVideoForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: CMVideoForm.CONTENT_TAB_ITEM_ID,
          items: [
            Config(DetailsDocumentForm),
            Config(TeaserDocumentForm),
            Config(DataDocumentForm, {
              title: BlueprintDocumentTypes_properties.CMVideo_data_text,
              helpText: BlueprintDocumentTypes_properties.CMVideo_data_helpText,
            }),
            Config(PlayerSettingsPropertyGroup, {
              itemId: "mediaOptionsPropertyFieldGroup",
              columns: 2,
              title: CustomLabels_properties.PropertyGroup_VideoProperties_label,
            }),
            Config(MediaDocumentForm, {
              collapsed: true,
              maxCardinality: 1,
            }),
            Config(RelatedDocumentForm, { collapsed: false }),
            Config(ViewTypeSelectorForm),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_extras_title,
          itemId: CMVideoForm.EXTRAS_TAB_ITEM_ID,
          items: [
            Config(CategoryDocumentForm),
            Config(PropertyFieldGroup, {
              collapsed: false,
              itemId: CMVideoForm.COPYRIGHT_FORM_ITEM_ID,
              title: CustomLabels_properties.PropertyGroup_Description_label,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              items: [
                Config(StringPropertyField, { propertyName: "copyright" }),
              ],
            }),
            Config(SEOForm),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config));
  }
}

export default CMVideoForm;
