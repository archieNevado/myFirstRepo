import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentInfo from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentInfo";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import VersionHistory from "@coremedia/studio-client.main.editor-components/sdk/premular/VersionHistory";
import StructPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/struct/StructPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";

interface AMAssetSystemTabConfig extends Config<DocumentForm> {
}

class AMAssetSystemTab extends DocumentForm {
  declare Config: AMAssetSystemTabConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.amAssetSystemTab";

  constructor(config: Config<AMAssetSystemTab> = null) {
    super(ConfigUtils.apply(Config(AMAssetSystemTab, {
      title: BlueprintTabs_properties.Tab_system_title,
      itemId: "system",
      autoHide: true,

      ...ConfigUtils.append({
        plugins: [
          Config(AddItemsPlugin, {
            onlyIf: (): boolean => session._.getUser().isAdministrative(),
            items: [
              Config(PropertyFieldGroup, {
                title: AMStudioPlugin_properties.PropertyGroup_state_label,
                itemId: "stateFormPanel",
                collapsed: true,
                items: [
                  Config(StructPropertyField, {
                    propertyName: "state",
                    hideLabel: true,
                  }),
                ],
              }),
            ],
            after: [
              Config(DocumentInfo),
            ],
          }),
        ],
      }),
      items: [
        Config(DocumentInfo),
        Config(VersionHistory),

        Config(PropertyFieldGroup, {
          title: AMStudioPlugin_properties.PropertyGroup_metadata_label,
          itemId: "metadataForm",
          collapsed: true,
          items: [
            Config(StructPropertyField, {
              propertyName: AssetConstants.PROPERTY_ASSET_METADATA,
              hideLabel: true,
              itemId: "metadataStructPropertyField",
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default AMAssetSystemTab;
