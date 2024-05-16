import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import GoogleAnalyticsStudioPluginContentTypes_properties from "./GoogleAnalyticsStudioPluginContentTypes_properties";

/**
 * Overrides of ResourceBundle "GoogleAnalyticsStudioPluginContentTypes" for Locale "ja".
 * @see GoogleAnalyticsStudioPluginContentTypes_properties#INSTANCE
 */
ResourceBundleUtil.override(GoogleAnalyticsStudioPluginContentTypes_properties, {
  // text and true_text are kept same deliberately
  "CMChannel_localSettings.googleAnalytics.disabled_text": "無効",
  "CMChannel_localSettings.googleAnalytics.disabled_true_text": "無効",
  "CMChannel_localSettings.googleAnalytics.homeUrl_text": "ホームURL",
  "CMChannel_localSettings.googleAnalytics.homeUrl_emptyText": "ここにGoogleアナリティクスのホームURLを入力します。",
  "CMChannel_localSettings.googleAnalytics.pageReport_text": "ページレポート",
  "CMChannel_localSettings.googleAnalytics.pageReport_emptyText": "ここにGoogleアナリティクスのページレポート名を入力します（デフォルトは「content-pages」）。",
  "CMChannel_localSettings.googleAnalytics.limit_text": "取得制限",
  "CMChannel_localSettings.googleAnalytics.limit_emptyText": "ここに収集する最大レコード数を入力します。",
  "CMChannel_localSettings.googleAnalytics.interval_text": "取得間隔",
  "CMChannel_localSettings.googleAnalytics.interval_emptyText": "ここにデータを収集する間隔（分単位）を入力します。0は取得を無効にします。",
  "CMALXBaseList_localSettings.googleAnalytics.limit_text": "取得制限",
  "CMALXBaseList_localSettings.googleAnalytics.limit_emptyText": "ここに収集する最大レコード数を入力します。",
  "CMALXBaseList_localSettings.googleAnalytics.interval_text": "取得間隔",
  "CMALXBaseList_localSettings.googleAnalytics.interval_emptyText": "ここにデータを収集する間隔（分単位）を入力します。0は取得を無効にします。",
});
