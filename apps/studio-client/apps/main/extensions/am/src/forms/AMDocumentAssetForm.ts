import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";
import AMAssetForm from "./AMAssetForm";
import AssetDetailsBlobPropertyField from "./AssetDetailsBlobPropertyField";

interface AMDocumentAssetFormConfig extends Config<AMAssetForm> {
}

class AMDocumentAssetForm extends AMAssetForm {
  declare Config: AMDocumentAssetFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.amDocumentAssetForm";

  constructor(config: Config<AMDocumentAssetForm> = null) {
    super(ConfigUtils.apply(Config(AMDocumentAssetForm, {
      originalIconCls: CoreIcons_properties.type_asset_document,

      renditions: [
        Config(PropertyFieldGroup, {
          itemId: "download",
          title: AMStudioPlugin_properties.PropertyGroup_download_label,
          expandOnValues: AssetConstants.PROPERTY_ASSET_DOWNLOAD,
          collapsed: true,
          items: [
            Config(AssetDetailsBlobPropertyField, {
              propertyName: AssetConstants.PROPERTY_ASSET_DOWNLOAD,
              hideLabel: true,
              showImageThumbnail: false,
              blobIconCls: config.downloadIconCls,
              visiblePropertyName: "metadata.renditions.download.show",
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default AMDocumentAssetForm;
