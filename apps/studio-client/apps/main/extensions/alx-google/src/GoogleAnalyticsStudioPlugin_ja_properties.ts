import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "GoogleAnalyticsStudioPlugin" for Locale "ja".
 * @see GoogleAnalyticsStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(GoogleAnalyticsStudioPlugin_properties, {
  SpacerTitle_navigation: "ナビゲーション",
  SpacerTitle_retrieval: "取得設定",
  SpacerTitle_layout: "レイアウト",
  SpacerTitle_googleanalytics: "Googleアナリティクス",
  SpacerTitle_googleanalytics_studio_config: "Studio設定",
  googleanalytics_fav_btn_text: "Google",
  googleanalytics_fav_btn_tooltip: "Googleアナリティクスを開く",
  googleanalytics_preview_btn_tooltip: "Googleアナリティクスレポートを開く",
  googleanalytics_webpropertyid_val: "^UA\\-\\d+\\-\\d+$",
  googleanalytics_webpropertyid_mask: "[UA\\d-]",
  googleanalytics_webpropertyid_text: "無効なウェブプロパティID。有効な値は、「UA-12345678-1」のようになります。",
  googleanalytics_service_provider: "Googleアナリティクス",
});
