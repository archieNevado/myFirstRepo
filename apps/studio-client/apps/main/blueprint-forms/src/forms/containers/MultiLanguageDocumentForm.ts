import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import SiteInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/SiteInfo";
import AvailableLocalesPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/AvailableLocalesPropertyField";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import MasterVersionPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/MasterVersionPropertyField";
import DerivedContentsList from "@coremedia/studio-client.main.editor-components/sdk/sites/DerivedContentsList";
import SynchronizationPropertyField from "@coremedia/studio-client.main.editor-components/sdk/synchronization/SynchronizationPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../../BlueprintTabs_properties";

interface MultiLanguageDocumentFormConfig extends Config<DocumentForm> {
}

class MultiLanguageDocumentForm extends DocumentForm {
  declare Config: MultiLanguageDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.multiLanguageDocumentForm";

  constructor(config: Config<MultiLanguageDocumentForm> = null) {
    super(ConfigUtils.apply(Config(MultiLanguageDocumentForm, {
      title: BlueprintTabs_properties.Tab_locale_title,
      itemId: "locale",
      autoHide: true,

      items: [
        Config(SiteInfo),
        Config(PropertyFieldGroup, {
          title: BlueprintTabs_properties.Tab_locale_title,
          itemId: "localizationForm",
          collapsed: false,
          items: [
            Config(AvailableLocalesPropertyField, {
              itemId: "locale",
              propertyName: "locale",
            }),
            Config(LinkListPropertyField, {
              itemId: "master",
              propertyName: "master",
              showThumbnails: true,
            }),
            Config(MasterVersionPropertyField, {
              itemId: "masterVersion",
              propertyName: "masterVersion",
            }),
            Config(SynchronizationPropertyField, {
              itemId: "synchorn",
              propertyName: editorContext._.getSitesService().getSiteModel().getIgnoreUpdatesProperty(),
            }),
            Config(DerivedContentsList, {
              itemId: "derivedContentsList",
              showThumbnails: true,
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default MultiLanguageDocumentForm;
