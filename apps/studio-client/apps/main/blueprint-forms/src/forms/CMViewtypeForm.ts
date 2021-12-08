import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BlobPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import MetaDataWithoutSettingsForm from "./containers/MetaDataWithoutSettingsForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";

interface CMViewtypeFormConfig extends Config<DocumentTabPanel> {
}

class CMViewtypeForm extends DocumentTabPanel {
  declare Config: CMViewtypeFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmViewtypeForm";

  constructor(config: Config<CMViewtypeForm> = null) {
    super(ConfigUtils.apply(Config(CMViewtypeForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMViewtype_text,
              itemId: "cmViewtypeLayoutForm",
              items: [
                Config(StringPropertyField, {
                  propertyName: "layout",
                  itemId: "layout",
                }),
                Config(StringPropertyField, {
                  propertyName: "description",
                  itemId: "description",
                }),
              ],
            }),
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMSymbol_icon_text,
              itemId: "cmViewTypeIconForm",
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

export default CMViewtypeForm;
