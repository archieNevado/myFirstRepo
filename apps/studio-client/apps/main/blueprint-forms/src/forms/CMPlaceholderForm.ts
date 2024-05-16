import ViewtypePropertyField from "@coremedia/studio-client.main.bpbase-studio-components/viewtypes/ViewtypePropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";

interface CMPlaceholderFormConfig extends Config<DocumentTabPanel> {
}

class CMPlaceholderForm extends DocumentTabPanel {
  declare Config: CMPlaceholderFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmPlaceholderForm";

  constructor(config: Config<CMPlaceholderForm> = null) {
    super(ConfigUtils.apply(Config(CMPlaceholderForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMPlaceholder_text,
              itemId: "cmPlaceholderIdForm",
              items: [
                Config(ViewtypePropertyField),
                Config(StringPropertyField, {
                  propertyName: "title",
                  itemId: "title",
                }),
                Config(StringPropertyField, {
                  propertyName: "id",
                  itemId: "id",
                }),
              ],
            }),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config));
  }
}

export default CMPlaceholderForm;
