import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StructPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/struct/StructPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import MetaDataWithoutSettingsForm from "./containers/MetaDataWithoutSettingsForm";

interface EditorPreferencesFormConfig extends Config<DocumentTabPanel> {
}

class EditorPreferencesForm extends DocumentTabPanel {
  declare Config: EditorPreferencesFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.editorPreferencesForm";

  constructor(config: Config<EditorPreferencesForm> = null) {
    super(ConfigUtils.apply(Config(EditorPreferencesForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMSettings_settings_text,
              itemId: "editorPreferencesDataForm",
              items: [
                Config(StructPropertyField, {
                  hideLabel: true,
                  propertyName: "data",
                }),
              ],
            }),
          ],
        }),
        Config(MetaDataWithoutSettingsForm),
      ],

    }), config));
  }
}

export default EditorPreferencesForm;
