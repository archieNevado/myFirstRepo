import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BooleanPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import DefaultExtraDataForm from "./components/DefaultExtraDataForm";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import TeaserDocumentForm from "./containers/TeaserDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMExternalLinkFormConfig extends Config<DocumentTabPanel> {
}

class CMExternalLinkForm extends DocumentTabPanel {
  declare Config: CMExternalLinkFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmExternalLinkForm";

  constructor(config: Config<CMExternalLinkForm> = null) {
    super(ConfigUtils.apply(Config(CMExternalLinkForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: BlueprintDocumentTypes_properties.CMExternalLink_url_text,
              itemId: "urlForm",
              items: [
                Config(StringPropertyField, {
                  propertyName: "url",
                  itemId: "url",
                  hideLabel: true,
                }),
                Config(BooleanPropertyField, {
                  propertyName: "openInNewTab",
                  hideLabel: true,
                }),
              ],
            }),
            Config(TeaserDocumentForm),
            Config(MediaDocumentForm, {
              collapsed: true,
              expandOnValues: "pictures",
            }),
            Config(ViewTypeSelectorForm),
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

export default CMExternalLinkForm;
