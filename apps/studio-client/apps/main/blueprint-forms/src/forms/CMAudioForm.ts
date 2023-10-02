import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../BlueprintDocumentTypes_properties";
import BlueprintTabs_properties from "../BlueprintTabs_properties";
import CustomLabels_properties from "../CustomLabels_properties";
import CategoryDocumentForm from "./containers/CategoryDocumentForm";
import CollapsibleStringPropertyForm from "./containers/CollapsibleStringPropertyForm";
import DataDocumentForm from "./containers/DataDocumentForm";
import DetailsDocumentForm from "./containers/DetailsDocumentForm";
import MediaDocumentForm from "./containers/MediaDocumentForm";
import MetaDataWithoutSearchableForm from "./containers/MetaDataWithoutSearchableForm";
import MultiLanguageDocumentForm from "./containers/MultiLanguageDocumentForm";
import PlayerSettingsPropertyGroup from "./containers/PlayerSettingsPropertyGroup";
import SEOForm from "./containers/SEOForm";
import TeaserDocumentForm from "./containers/TeaserDocumentForm";
import ValidityDocumentForm from "./containers/ValidityDocumentForm";
import ViewTypeSelectorForm from "./containers/ViewTypeSelectorForm";

interface CMAudioFormConfig extends Config<DocumentTabPanel> {
}

class CMAudioForm extends DocumentTabPanel {
  declare Config: CMAudioFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.cmAudioForm";

  static readonly #LOCAL_SETTINGS: string = "localSettings";

  static readonly #PLAYER_SETTINGS: string = "playerSettings";

  constructor(config: Config<CMAudioForm> = null) {
    super(ConfigUtils.apply(Config(CMAudioForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(DetailsDocumentForm),
            Config(TeaserDocumentForm, { collapsed: true }),
            Config(DataDocumentForm, {
              title: BlueprintDocumentTypes_properties.CMAudio_data_text,
              helpText: BlueprintDocumentTypes_properties.CMAudio_data_helpText,
            }),
            Config(PlayerSettingsPropertyGroup, {
              itemId: "mediaOptionsPropertyFieldGroup",
              columns: 2,
              hideHideControlsCheckbox: true,
              hideMuteCheckbox: true,
              title: CustomLabels_properties.PropertyGroup_AudioProperties_label,
            }),
            Config(MediaDocumentForm, {
              collapsed: true,
              expandOnValues: "pictures",
            }),
            Config(ViewTypeSelectorForm),
            Config(ValidityDocumentForm),
          ],
        }),
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_extras_title,
          itemId: "metadata",
          items: [
            Config(CategoryDocumentForm),
            Config(CollapsibleStringPropertyForm, {
              title: BlueprintDocumentTypes_properties.CMMedia_copyright_text,
              propertyName: "copyright",
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

export default CMAudioForm;
