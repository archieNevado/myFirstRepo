import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";
import AMAssetMetaDataTab from "./AMAssetMetaDataTab";
import AMAssetSystemTab from "./AMAssetSystemTab";
import AssetDetailsBlobPropertyField from "./AssetDetailsBlobPropertyField";

interface AMAssetFormConfig extends Config<DocumentTabPanel>, Partial<Pick<AMAssetForm,
  "renditions" |
  "originalIconCls" |
  "downloadIconCls"
>> {
}

class AMAssetForm extends DocumentTabPanel {
  declare Config: AMAssetFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.amAssetForm";

  #localItems: Array<any> = null;

  constructor(config: Config<AMAssetForm> = null) {
    super((()=>{
      this.#localItems = [
        Config(PropertyFieldGroup, {
          itemId: "original",
          title: AMStudioPlugin_properties.PropertyGroup_original_label,
          expandOnValues: AssetConstants.PROPERTY_ASSET_ORIGINAL,
          items: [
            Config(AssetDetailsBlobPropertyField, {
              propertyName: AssetConstants.PROPERTY_ASSET_ORIGINAL,
              hideLabel: true,
              showImageThumbnail: false,
              blobIconCls: config.originalIconCls,
              visiblePropertyName: "metadata.renditions.original.show",
            }),
          ],
        }),

        Config(PropertyFieldGroup, {
          itemId: "thumbnail",
          title: AMStudioPlugin_properties.PropertyGroup_thumbnail_label,
          collapsed: true,
          items: [
            Config(AssetDetailsBlobPropertyField, {
              propertyName: AssetConstants.PROPERTY_ASSET_THUMBNAIL,
              hideLabel: true,
              showImageThumbnail: false,
            }),
          ],
        }),
      ];
      config = ConfigUtils.apply({
        renditions: null,
        originalIconCls: "content-type-l content-type-AMAsset-icon",
        downloadIconCls: "content-type-l content-type-AMAsset-icon",
      }, config);
      return ConfigUtils.apply(Config(AMAssetForm, {

        items: [
          Config(DocumentForm, {
            title: AMStudioPlugin_properties.Tab_renditions_title,
            items: this.#localItems.concat(config.renditions || []),
          }),
          Config(AMAssetMetaDataTab),
          Config(AMAssetSystemTab),
        ],

      }), config);
    })());
  }

  /**
   * The items to be displayed in the 'Renditions' tab below the property editor for the original property.
   */
  renditions: Array<any> = null;

  /**
   * An optional class used as iconCls for the original property editor.
   */
  originalIconCls: string = null;

  /**
   * An optional class used as iconCls for the download property editor.
   */
  downloadIconCls: string = null;
}

export default AMAssetForm;
