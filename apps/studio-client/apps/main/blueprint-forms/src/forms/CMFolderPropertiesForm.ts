import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import MetaDataInformationForm from "./containers/MetaDataInformationForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";

interface CMFolderPropertiesFormConfig extends Config<DocumentTabPanel> {
}

class CMFolderPropertiesForm extends DocumentTabPanel {
  declare Config: CMFolderPropertiesFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmFolderPropertiesForm";

  constructor(config: Config<CMFolderPropertiesForm> = null) {
    super(ConfigUtils.apply(Config(CMFolderPropertiesForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMFolderProperties_contexts_text,
              itemId: "cmFolderContextsForm",
              items: [
                Config(LinkListPropertyField, {
                  propertyName: "contexts",
                  hideLabel: true,
                  itemId: "contextsPropertyField",
                }),
              ],
            }),
          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataInformationForm),
      ],

    }), config));
  }
}

export default CMFolderPropertiesForm;
