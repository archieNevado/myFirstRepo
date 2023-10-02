import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BlobPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import MetaDataWithoutSettingsForm from "./containers/MetaDataWithoutSettingsForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";

interface CMSymbolFormConfig extends Config<DocumentTabPanel> {
}

class CMSymbolForm extends DocumentTabPanel {
  declare Config: CMSymbolFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmSymbolForm";

  constructor(config: Config<CMSymbolForm> = null) {
    super(ConfigUtils.apply(Config(CMSymbolForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(CollapsibleStringPropertyForm, {
              propertyName: "description",
              title: BlueprintDocumentTypes_properties.CMSymbol_description_text,
            }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMSymbol_data_text,
              itemId: "cmSymbolIconForm",
              items: [
                Config(BlobPropertyField, {
                  propertyName: "icon",
                  hideLabel: true,
                }),
              ],
            }),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSettingsForm),
      ],

    }), config));
  }
}

export default CMSymbolForm;
