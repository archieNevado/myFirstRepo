import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import AMStudioPlugin_properties from "./AMStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "AMStudioPlugin" for Locale "ja".
 * @see AMStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(AMStudioPlugin_properties, {
  Tab_renditions_title: "レンディション",
  Tab_metadata_title: "メタデータ",
  Tab_state_title: "状態",
  PropertyGroup_original_label: "元のファイル",
  PropertyGroup_metadata_label: "メタデータ",
  PropertyGroup_rights_label: "権限",
  PropertyGroup_thumbnail_label: "サムネイル",
  PropertyGroup_web_label: "ウェブ",
  PropertyGroup_web_referrers_label: "ローカライズされたウェブ資産",
  PropertyGroup_print_label: "印刷",
  PropertyGroup_state_label: "状態",
  PropertyGroup_categories_label: "資産カテゴリー",
  PropertyGroup_product_codes_label: "商品コード",
  PropertyGroup_product_codes_textfield_empty_text: "商品コードをこちらに入力。",
  PropertyGroup_asset_label: "資産",
  Action_createCMPictureFromAMPictureAsset_tooltip: "所定の資産から新たな写真コンテンツを作成。",
  Action_createCMPictureFromAMPictureAsset_text: "ローカライズされたウェブ資産を作成",
  Action_createCMVideoFromAMVideoAsset_tooltip: "所定の資産から新たなビデオコンテンツを作成。",
  Action_createCMVideoFromAMVideoAsset_text: "ローカライズされたウェブ資産を作成",
  ExpirationDate_dateFormat: "Y年m月d日",
  Filter_RightsChannels_text: "権限：チャンネル",
  Filter_RightsRegions_text: "権限：地域",
  Asset_metadata_channels_print_text: "印刷",
  Asset_metadata_channels_mobile_text: "モバイル",
  Asset_metadata_channels_web_text: "ウェブ",
  Asset_metadata_channels_social_text: "ソーシャル",
  Asset_metadata_regions_USA_text: "米国",
  Asset_metadata_regions_Europe_text: "ヨーロッパ",
  Filter_ExpirationDate_text: "失効",
  Filter_ExpirationDate_any_text: "どれでも",
  Filter_ExpirationDate_inOneDay_text: "1日以内",
  Filter_ExpirationDate_inOneWeek_text: "1週間以内",
  Filter_ExpirationDate_inTwoWeeks_text: "2週間以内",
  Filter_ExpirationDate_inOneMonth_text: "1か月以内",
  Filter_ExpirationDate_byDate_text: "…まで",
  Column_ExpirationDate_text: "失効日",
  Rendition_downloadable: "ダウンロードポータルで使用できるようにする",
  EditedContents_showAssets_label: "資産を含める",
});
