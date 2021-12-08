import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CreateContentFromAssetActionBase from "./CreateContentFromAssetActionBase";

interface CreateContentFromAssetActionConfig extends Config<CreateContentFromAssetActionBase>, Partial<Pick<CreateContentFromAssetAction,
  "assetContentType" |
  "targetContentType" |
  "sourceRenditionProperty" |
  "targetRenditionProperty" |
  "targetAssetLinkProperty" |
  "targetCopyrightProperty" |
  "targetValidToProperty" |
  "targetThumbnailContentType" |
  "targetThumbnailProperty" |
  "sourceThumbnailProperty" |
  "targetLinkedThumbnailProperty" |
  "targetThumbnailAssetLinkProperty"
>> {
}

/**
 *
 * A <code>contentAction</code> that creates a new content from a given asset content.
 *
 */
class CreateContentFromAssetAction extends CreateContentFromAssetActionBase {
  declare Config: CreateContentFromAssetActionConfig;

  constructor(config: Config<CreateContentFromAssetAction> = null) {
    super(ConfigUtils.apply(Config(CreateContentFromAssetAction), config));
  }

  /**
   * The name of the ContentType of the asset.
   */
  assetContentType: string = null;

  /**
   * The name of the ContentType to be used for creating the content.
   * The created content items will be placed in the home folder configured for the targetContentType in the
   * properties of the QuickCreateSettings resource bundle.
   */
  targetContentType: string = null;

  /**
   * The name of the property where the rendition blob is stored in the asset.
   */
  sourceRenditionProperty: string = null;

  /**
   * The name of the property that should store the link to the rendition blob in the created content.
   */
  targetRenditionProperty: string = null;

  /**
   * The name of the property that should store the link to the source asset in the created content.
   */
  targetAssetLinkProperty: string = null;

  /**
   * The name of the property that should store the copyright information in the created content.
   */
  targetCopyrightProperty: string = null;

  /**
   * The name of the property that should store the valid to date in the created content.
   */
  targetValidToProperty: string = null;

  /**
   * The content type of the thumbnail document created on the fly and saved alongside the created content.
   */
  targetThumbnailContentType: string = null;

  /**
   * The name of the property that should store the thumbnail blob in the thumbnail document.
   */
  targetThumbnailProperty: string = null;

  /**
   * The name of the property that stores the thumbnail in the asset.
   */
  sourceThumbnailProperty: string = null;

  /**
   * The name of the property that stores a link to the thumbnail document in the created content.
   */
  targetLinkedThumbnailProperty: string = null;

  /**
   * The name of the property that stores a link from the thumbnail document back to the asset.
   */
  targetThumbnailAssetLinkProperty: string = null;
}

export default CreateContentFromAssetAction;
