import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AssetConstants from "../AssetConstants";
import CreateContentFromAssetAction from "./CreateContentFromAssetAction";

interface CreatePictureFromPictureAssetActionConfig extends Config<CreateContentFromAssetAction> {
}

/**
 * An action that creates a new picture from a given picture asset.
 */
class CreatePictureFromPictureAssetAction extends CreateContentFromAssetAction {
  declare Config: CreatePictureFromPictureAssetActionConfig;

  constructor(config: Config<CreatePictureFromPictureAssetAction> = null) {
    super(ConfigUtils.apply(Config(CreatePictureFromPictureAssetAction, {
      assetContentType: AssetConstants.DOCTYPE_PICTURE_ASSET,
      sourceRenditionProperty: AssetConstants.PROPERTY_ASSET_WEB,
      targetContentType: "CMPicture",
      targetRenditionProperty: "data",
      targetAssetLinkProperty: "asset",
      targetCopyrightProperty: "copyright",
      targetValidToProperty: "validTo",

    }), config));
  }
}

export default CreatePictureFromPictureAssetAction;
