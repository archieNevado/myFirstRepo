import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StructPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/struct/StructPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import MetaDataWithoutSettingsForm from "./containers/MetaDataWithoutSettingsForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";

interface CMResourceBundleFormConfig extends Config<DocumentTabPanel> {
}

class CMResourceBundleForm extends DocumentTabPanel {
  declare Config: CMResourceBundleFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmResourceBundleForm";

  constructor(config: Config<CMResourceBundleForm> = null) {
    super(ConfigUtils.apply(Config(CMResourceBundleForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMResourceBundle_localizations_text,
              itemId: "cmSettingsSettingsForm",
              items: [
                Config(StructPropertyField, {
                  hideLabel: true,
                  propertyName: "localizations",
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

export default CMResourceBundleForm;
