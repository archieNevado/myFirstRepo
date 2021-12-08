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

interface CMImageFormConfig extends Config<DocumentTabPanel> {
}

class CMImageForm extends DocumentTabPanel {
  declare Config: CMImageFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmImageForm";

  constructor(config: Config<CMImageForm> = null) {
    super(ConfigUtils.apply(Config(CMImageForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(CollapsibleStringPropertyForm, {
              propertyName: "description",
              title: BlueprintDocumentTypes_properties.CMImage_description_text,
            }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMImage_data_text,
              itemId: "cmImageDataForm",
              items: [
                Config(BlobPropertyField, {
                  propertyName: "data",
                  hideLabel: true,
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

export default CMImageForm;
