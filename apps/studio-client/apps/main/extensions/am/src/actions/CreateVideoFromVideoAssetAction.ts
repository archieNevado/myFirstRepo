import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AssetConstants from "../AssetConstants";
import CreateContentFromAssetAction from "./CreateContentFromAssetAction";

interface CreateVideoFromVideoAssetActionConfig extends Config<CreateContentFromAssetAction> {
}

/**
 * An action that creates a new video from a given video asset.
 */
class CreateVideoFromVideoAssetAction extends CreateContentFromAssetAction {
  declare Config: CreateVideoFromVideoAssetActionConfig;

  constructor(config: Config<CreateVideoFromVideoAssetAction> = null) {
    super(ConfigUtils.apply(Config(CreateVideoFromVideoAssetAction, {
      assetContentType: AssetConstants.DOCTYPE_VIDEO_ASSET,
      sourceRenditionProperty: AssetConstants.PROPERTY_ASSET_WEB,
      targetContentType: "CMVideo",
      targetRenditionProperty: "data",
      targetAssetLinkProperty: "asset",
      targetLinkedThumbnailProperty: "pictures",
      targetCopyrightProperty: "copyright",
      targetThumbnailProperty: "data",
      sourceThumbnailProperty: AssetConstants.PROPERTY_ASSET_THUMBNAIL,
      targetThumbnailContentType: "CMPicture",
      targetThumbnailAssetLinkProperty: "asset",
      targetValidToProperty: "validTo",

    }), config));
  }
}

export default CreateVideoFromVideoAssetAction;
