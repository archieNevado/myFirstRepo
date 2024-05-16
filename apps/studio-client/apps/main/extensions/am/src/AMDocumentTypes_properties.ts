import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "AMDocumentTypes".
 * @see AMDocumentTypes_properties#INSTANCE
 */
interface AMDocumentTypes_properties {

/**
 * --- AMAsset ---
 */
  AMAsset_text: string;
  AMAsset_toolTip: string;
  AMAsset_icon: string;
  AMAsset_original_text: string;
  AMAsset_original_toolTip: string;
  AMAsset_thumbnail_text: string;
  AMAsset_thumbnail_toolTip: string;
  "AMAsset_metadata.copyright_text": string;
  "AMAsset_metadata.copyright_toolTip": string;
  "AMAsset_metadata.copyright_emptyText": string;
  "AMAsset_metadata.expirationDate_text": string;
  "AMAsset_metadata.expirationDate_emptyText": string;
  "AMAsset_metadata.channels_text": string;
  "AMAsset_metadata.regions_text": string;
  AMAsset_keywords_text: string;
  AMAsset_keywords_toolTip: string;
  AMAsset_keywords_emptyText: string;
  AMAsset_locationTaxonomy_text: string;
  AMAsset_locationTaxonomy_toolTip: string;
  AMAsset_locationTaxonomy_emptyText: string;
  AMAsset_subjectTaxonomy_text: string;
  AMAsset_subjectTaxonomy_toolTip: string;
  AMAsset_subjectTaxonomy_emptyText: string;
  AMAsset_assetTaxonomy_text: string;
  AMAsset_assetTaxonomy_toolTip: string;
  AMAsset_assetTaxonomy_emptyText: string;
/**
 * --- AMPictureAsset ---
 */
  AMPictureAsset_text: string;
  AMPictureAsset_doctype: string;
  AMPictureAsset_toolTip: string;
  AMPictureAsset_icon: string;
  AMPictureAsset_web_text: string;
  AMPictureAsset_web_toolTip: string;
  AMPictureAsset_print_text: string;
  AMPictureAsset_print_toolTip: string;
/**
 * --- AMPictureAsset ---
 */
  AMVideoAsset_text: string;
  AMVideoAsset_doctype: string;
  AMVideoAsset_toolTip: string;
  AMVideoAsset_icon: string;
  AMVideoAsset_web_text: string;
  AMVideoAsset_web_toolTip: string;
/**
 * --- AMDocumentAsset ---
 */
  AMDocumentAsset_text: string;
  AMDocumentAsset_toolTip: string;
  AMDocumentAsset_icon: string;
  AMDocumentAsset_download_text: string;
  AMDocumentAsset_download_toolTip: string;
/**
 * --- AMTaxonomy ---
 */
  AMTaxonomy_text: string;
  AMTaxonomy_toolTip: string;
  AMTaxonomy_icon: string;
  AMTaxonomy_value_text: string;
  AMTaxonomy_value_toolTip: string;
  AMTaxonomy_value_emptyText: string;
  AMTaxonomy_assetThumbnail_name: string;
  AMTaxonomy_assetThumbnail_label: string;
  AMTaxonomy_assetThumbnail_emptyText: string;
/**
 * --- extension of CMPicture ---
 */
  CMPicture_asset_text: string;
  CMPicture_asset_tooltip: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "AMDocumentTypes".
 * @see AMDocumentTypes_properties
 */
const AMDocumentTypes_properties: AMDocumentTypes_properties = {
  AMAsset_text: "Asset",
  AMAsset_toolTip: "A base type for assets",
  AMAsset_icon: CoreIcons_properties.type_asset_object,
  AMAsset_original_text: "Original",
  AMAsset_original_toolTip: "Original asset file in an editable form",
  AMAsset_thumbnail_text: "Thumbnail",
  AMAsset_thumbnail_toolTip: "Thumbnail image representing the asset",
  "AMAsset_metadata.copyright_text": "Copyright",
  "AMAsset_metadata.copyright_toolTip": "Information about author and copyright",
  "AMAsset_metadata.copyright_emptyText": "Enter information about author and copyright here.",
  "AMAsset_metadata.expirationDate_text": "Expiration Date",
  "AMAsset_metadata.expirationDate_emptyText": "Does Not Expire",
  "AMAsset_metadata.channels_text": "Channels",
  "AMAsset_metadata.regions_text": "Regions",
  AMAsset_keywords_text: "Free Keywords",
  AMAsset_keywords_toolTip: "Free keywords that are used for seo optimizations",
  AMAsset_keywords_emptyText: "Enter free keywords here.",
  AMAsset_locationTaxonomy_text: "Location Tags",
  AMAsset_locationTaxonomy_toolTip: "Location Tag",
  AMAsset_locationTaxonomy_emptyText: "Add content by dragging it from the Library here.",
  AMAsset_subjectTaxonomy_text: "Subject Tags",
  AMAsset_subjectTaxonomy_toolTip: "Subject tag",
  AMAsset_subjectTaxonomy_emptyText: "Add content by dragging it from the Library here.",
  AMAsset_assetTaxonomy_text: "Asset Category",
  AMAsset_assetTaxonomy_toolTip: "Asset Category",
  AMAsset_assetTaxonomy_emptyText: "Add content by dragging it from the Library here.",
  AMPictureAsset_text: "Picture Asset",
  AMPictureAsset_doctype: "AMPictureAsset",
  AMPictureAsset_toolTip: "A picture asset",
  AMPictureAsset_icon: CoreIcons_properties.type_asset_picture,
  AMPictureAsset_web_text: "Web Rendition",
  AMPictureAsset_web_toolTip: "A rendition of the asset for web delivery",
  AMPictureAsset_print_text: "Print Rendition",
  AMPictureAsset_print_toolTip: "A rendition of the asset for printing",
  AMVideoAsset_text: "Video Asset",
  AMVideoAsset_doctype: "AMVideoAsset",
  AMVideoAsset_toolTip: "A video asset",
  AMVideoAsset_icon: CoreIcons_properties.type_asset_video,
  AMVideoAsset_web_text: "Web Rendition",
  AMVideoAsset_web_toolTip: "A rendition of the asset for web delivery",
  AMDocumentAsset_text: "Document Asset",
  AMDocumentAsset_toolTip: "A document asset",
  AMDocumentAsset_icon: CoreIcons_properties.type_asset_document,
  AMDocumentAsset_download_text: "Download Rendition",
  AMDocumentAsset_download_toolTip: "A rendition of the asset for download delivery",
  AMTaxonomy_text: "Asset Category",
  AMTaxonomy_toolTip: "Category for Asset documents",
  AMTaxonomy_icon: "",
  AMTaxonomy_value_text: "Name",
  AMTaxonomy_value_toolTip: "Category name",
  AMTaxonomy_value_emptyText: "Enter a category name here.",
  AMTaxonomy_assetThumbnail_name: "assetThumbnail",
  AMTaxonomy_assetThumbnail_label: "Asset for Thumbnail",
  AMTaxonomy_assetThumbnail_emptyText: "Drag Asset from the library here.",
  CMPicture_asset_text: "Asset",
  CMPicture_asset_tooltip: "The asset from which this picture has been derived",
};

export default AMDocumentTypes_properties;
