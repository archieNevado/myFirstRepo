
class AssetConstants {

  static readonly ASSET_LIBRARY_PATH: string = "/Assets";

  static readonly ASSET_TAXONOMY_ID: string = "Asset Download Portal";

  static readonly DOCTYPE_ASSET: string = "AMAsset";

  static readonly DOCTYPE_DOCUMENT_ASSET: string = "AMDocumentAsset";

  static readonly DOCTYPE_PICTURE_ASSET: string = "AMPictureAsset";

  static readonly DOCTYPE_VIDEO_ASSET: string = "AMVideoAsset";

  static readonly DOCTYPE_ASSET_TAXONOMY: string = "AMTaxonomy";

  static readonly PROPERTY_ASSET_ORIGINAL: string = "original";

  static readonly PROPERTY_ASSET_THUMBNAIL: string = "thumbnail";

  static readonly PROPERTY_ASSET_DOWNLOAD: string = "download";

  static readonly PROPERTY_ASSET_WEB: string = "web";

  static readonly PROPERTY_ASSET_PRINT: string = "print";

  static readonly PROPERTY_ASSET_ASSETTAXONOMY: string = "assetTaxonomy";

  static readonly PROPERTY_ASSET_ASSETTAXONOMY_SEARCH: string = "assettaxonomy";

  static readonly PROPERTY_ASSET_METADATA: string = "metadata";

  static readonly PROPERTY_ASSET_METADATA_CHANNELS: string = "channels";

  static readonly PROPERTY_ASSET_METADATA_REGIONS: string = "regions";

  static readonly PROPERTY_ASSET_METADATA_COPYRIGHT: string = "copyright";

  static readonly PROPERTY_ASSET_METADATA_EXPIRATIONDATE: string = "expirationDate";

  static readonly PROPERTY_ASSET_METADATA_RENDITIONS: string = "renditions";

  static readonly PROPERTY_ASSET_METADATA_PRODUCT_CODES: string = "productIds";

  constructor() {
    throw new Error("Utility class AssetConstants must not be instantiated");
  }
}

export default AssetConstants;
