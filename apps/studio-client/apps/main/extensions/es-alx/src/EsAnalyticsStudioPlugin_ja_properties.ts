import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "EsAnalyticsStudioPlugin" for Locale "ja".
 * @see EsAnalyticsStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(EsAnalyticsStudioPlugin_properties, {
  chart_container_label: "このコンテンツアイテムに独自のページインプレッション",
  chart_data_unavailable: "利用できるデータはありません",
  chart_time_stamp_unavailable: "-",
  chart_time_range_label: "インターバル",
  chart_time_stamp_update: "最後にフェッチされたのは以下のとおりです。",
  shortDateFormat: "Y年m月d日",
  dateFormat: "Y年m月d日 H:i",
  chart_label_page_views: "ページビュー",
  chart_last_7_days: "直近７日間",
  chart_last_30_days: "直近30日間",
  widget_title: "パフォーマンス",
  widget_type: "サイトパフォーマンス",
  widget_description: "サイトパフォーマンス統計",
  widget_combo_root_channel_label: "サイト",
  widget_title_channel_undefined: "サイトが選択されていません",
});
