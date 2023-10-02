import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import DetailsDocumentForm from "./containers/DetailsDocumentForm";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";

interface CMActionFormConfig extends Config<DocumentTabPanel> {
}

class CMActionForm extends DocumentTabPanel {
  declare Config: CMActionFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmActionForm";

  constructor(config: Config<CMActionForm> = null) {
    super(ConfigUtils.apply(Config(CMActionForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(DetailsDocumentForm),
            Config(PropertyFieldGroup, {
              itemId: "cmActionIdForm",
              title: BlueprintDocumentTypes_properties.CMAction_text,
              items: [
                Config(StringPropertyField, {
                  propertyName: "id",
                  itemId: "id",
                }),
                Config(StringPropertyField, {
                  propertyName: "type",
                  itemId: "type",
                }),
              ],
            }),
            Config(MediaDocumentForm),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DefaultExtraDataForm),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSearchableForm),
      ],

    }), config));
  }
}

export default CMActionForm;
