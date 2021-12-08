import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import EsDocTypes_properties from "./EsDocTypes_properties";

/**
 * Overrides of ResourceBundle "EsDocTypes_properties" for locale "ja".
 * @see EsDocTypes_properties
 */
ResourceBundleUtil.override(EsDocTypes_properties, {
  CMMail_displayName: "Emailテンプレート",
  CMMail_from_displayName: "差出人",
  CMMail_from_emptyText: "ここに送信者のEmailアドレスを入力します",
  CMMail_subject_displayName: "件名",
  CMMail_subject_emptyText: "ここにEmailの件名を入力します",
  CMMail_text_displayName: "Emailテキスト",
  CMMail_text_emptyText: " ",
  CMMail_contentType_displayName: "Content-Type (text/plain, text/html)",
  CMMail_contentType_emptyText: "text/plain,text/html",
  ESDynamicList_displayName: "動的エラスティックソーシャルリスト",
  ESDynamicList_interval_displayName: "間隔",
  ESDynamicList_aggregationType_displayName: "リスト",
  ESDynamicList_channel_displayName: "コンテキスト",
});
