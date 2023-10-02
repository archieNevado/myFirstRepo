import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "AMStudioPlugin".
 * @see AMStudioPlugin_properties#INSTANCE
 */
interface AMStudioPlugin_properties {
  "LicenseName_asset-management": string;

  Tab_renditions_title: string;
  Tab_metadata_title: string;
  Tab_state_title: string;
  PropertyGroup_original_label: string;
  PropertyGroup_metadata_label: string;
  PropertyGroup_rights_label: string;
  PropertyGroup_thumbnail_label: string;
  PropertyGroup_download_label: string;
  PropertyGroup_web_label: string;
  PropertyGroup_web_referrers_label: string;
  PropertyGroup_print_label: string;
  PropertyGroup_state_label: string;
  PropertyGroup_categories_label: string;
  PropertyGroup_product_codes_label: string;
  PropertyGroup_product_codes_textfield_empty_text: string;
  PropertyGroup_asset_label: string;
  Action_createCMPictureFromAMPictureAsset_icon: string;
  Action_createCMPictureFromAMPictureAsset_text: string;
  Action_createCMPictureFromAMPictureAsset_tooltip: string;
  Action_createCMVideoFromAMVideoAsset_icon: string;
  Action_createCMVideoFromAMVideoAsset_text: string;
  Action_createCMVideoFromAMVideoAsset_tooltip: string;
  ExpirationDate_dateFormat: string;
  Filter_RightsChannels_text: string;
  Filter_RightsRegions_text: string;
  Asset_metadata_channels_print_text: string;
  Asset_metadata_channels_mobile_text: string;
  Asset_metadata_channels_web_text: string;
  Asset_metadata_channels_social_text: string;
  Asset_metadata_regions_USA_text: string;
  Asset_metadata_regions_Europe_text: string;
  Filter_ExpirationDate_text: string;
  Filter_ExpirationDate_any_text: string;
  Filter_ExpirationDate_inOneDay_text: string;
  Filter_ExpirationDate_inOneWeek_text: string;
  Filter_ExpirationDate_inTwoWeeks_text: string;
  Filter_ExpirationDate_inOneMonth_text: string;
  Filter_ExpirationDate_byDate_text: string;
  Column_ExpirationDate_text: string;
  CollectionView_assetRootFolder_icon: string;
  Rendition_downloadable: string;
  EditedContents_showAssets_label: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "AMStudioPlugin".
 * @see AMStudioPlugin_properties
 */
const AMStudioPlugin_properties: AMStudioPlugin_properties = {
  "LicenseName_asset-management": "CoreMedia Advanced Asset Management",
  Tab_renditions_title: "Renditions",
  Tab_metadata_title: "Metadata",
  Tab_state_title: "State",
  PropertyGroup_original_label: "Original",
  PropertyGroup_metadata_label: "Metadata",
  PropertyGroup_rights_label: "Rights",
  PropertyGroup_thumbnail_label: "Thumbnail",
  PropertyGroup_download_label: "Download",
  PropertyGroup_web_label: "Web",
  PropertyGroup_web_referrers_label: "Localized Web Assets",
  PropertyGroup_print_label: "Print",
  PropertyGroup_state_label: "State",
  PropertyGroup_categories_label: "Asset Categories",
  PropertyGroup_product_codes_label: "Product Codes",
  PropertyGroup_product_codes_textfield_empty_text: "Enter a product code here.",
  PropertyGroup_asset_label: "Asset",
  Action_createCMPictureFromAMPictureAsset_icon: CoreIcons_properties.create_type_picture,
  Action_createCMPictureFromAMPictureAsset_text: "Create localized web asset",
  Action_createCMPictureFromAMPictureAsset_tooltip: "Create new picture content from the given asset.",
  Action_createCMVideoFromAMVideoAsset_icon: CoreIcons_properties.create_type_video,
  Action_createCMVideoFromAMVideoAsset_text: "Create localized web asset",
  Action_createCMVideoFromAMVideoAsset_tooltip: "Create new video content from the given asset.",
  ExpirationDate_dateFormat: "m/d/Y",
  Filter_RightsChannels_text: "Rights: Channels",
  Filter_RightsRegions_text: "Rights: Regions",
  Asset_metadata_channels_print_text: "Print",
  Asset_metadata_channels_mobile_text: "Mobile",
  Asset_metadata_channels_web_text: "Web",
  Asset_metadata_channels_social_text: "Social",
  Asset_metadata_regions_USA_text: "USA",
  Asset_metadata_regions_Europe_text: "Europe",
  Filter_ExpirationDate_text: "Expiration",
  Filter_ExpirationDate_any_text: "Any",
  Filter_ExpirationDate_inOneDay_text: "In 1 Day",
  Filter_ExpirationDate_inOneWeek_text: "In 1 Week",
  Filter_ExpirationDate_inTwoWeeks_text: "In 2 Weeks",
  Filter_ExpirationDate_inOneMonth_text: "In 1 Month",
  Filter_ExpirationDate_byDate_text: "By…",
  Column_ExpirationDate_text: "Expiration Date",
  CollectionView_assetRootFolder_icon: CoreIcons_properties.asset,
  Rendition_downloadable: "Make available in Download Portal",
  EditedContents_showAssets_label: "Include Assets",
};

export default AMStudioPlugin_properties;
