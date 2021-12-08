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
import CustomLabels_properties from "../../CustomLabels_properties";

interface MultiLangWithBundlesDocumentFormConfig extends Config<DocumentForm> {
}

class MultiLangWithBundlesDocumentForm extends DocumentForm {
  declare Config: MultiLangWithBundlesDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.multiLangWithBundlesDocumentForm";

  constructor(config: Config<MultiLangWithBundlesDocumentForm> = null) {
    super(ConfigUtils.apply(Config(MultiLangWithBundlesDocumentForm, {
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
            }),
            Config(MasterVersionPropertyField, {
              itemId: "masterVersion",
              propertyName: "masterVersion",
            }),
            Config(SynchronizationPropertyField, {
              itemId: "ignoreUpdates",
              propertyName: "ignoreUpdates",
            }),
            Config(DerivedContentsList, {
              itemId: "derivedContentsList",
              showThumbnails: true,
            }),
          ],
        }),
        Config(PropertyFieldGroup, {
          title: CustomLabels_properties.PropertyGroup_ResourceBundles_label,
          itemId: "resourceBundles",
          items: [
            Config(LinkListPropertyField, {
              propertyName: "resourceBundles2",
              hideLabel: true,
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default MultiLangWithBundlesDocumentForm;
