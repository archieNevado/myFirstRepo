import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import AlxDocTypes_properties from "./AlxDocTypes_properties";

/**
 * Overrides of ResourceBundle "AlxDocTypes_properties" for locale "ja".
 * @see AlxDocTypes_properties
 */
ResourceBundleUtil.override(AlxDocTypes_properties, {
  CMALXBaseList_displayName: "アナリティクスベースリスト",
  CMALXBaseList_maxLength_displayName: "最大文字数",
  CMALXBaseList_maxLength_emptyText: "アナリティクスページリストの最大文字数を入力します。",
  CMALXBaseList_timeRange_displayName: "時間範囲",
  CMALXBaseList_timeRange_emptyText: "含めるデータの時間範囲を入力します（本日を除く日数）。",
  CMALXBaseList_analyticsProvider_displayName: "アナリティクスプロバイダーのID",
  CMALXBaseList_analyticsProvider_emptyText: "ここにアナリティクスプロバイダーのIDを入力します。",
  CMALXPageList_displayName: "アナリティクスページリスト",
  CMALXPageList_documentType_displayName: "ドキュメントの種類",
  CMALXPageList_documentType_emptyText: "必要なドキュメントの種類。",
  CMALXPageList_baseChannel_displayName: "ベースチャネル",
  CMALXPageList_baseChannel_emptyText: "希望のドキュメントが置かれているベースチャネルをここに入力します。",
  CMALXPageList_defaultContent_displayName: "デフォルトコンテンツ",
  CMALXPageList_defaultContent_emptyText: "表示するデフォルトドキュメントをライブラリから追加します。",
  CMALXEventList_displayName: "アナリティクスイベントリスト",
  CMALXEventList_category_displayName: "イベントカテゴリ",
  CMALXEventList_category_emptyText: "トラッキングするオブジェクトグループに付けられた名前を入力します（「ビデオ」など）",
  CMALXEventList_action_displayName: "イベントアクション",
  CMALXEventList_action_emptyText: "トラッキングするイベントまたは操作の種類を入力します（「再生を押した」など）",
  CMChannel_localSettings_analyticsProvider_displayName: "デフォルトのアナリティクスプロバイダーの名前",
  CMChannel_localSettings_analyticsProvider_emptyText: "ここにデフォルトのアナリティクスプロバイダーのIDを入力します。",
});
