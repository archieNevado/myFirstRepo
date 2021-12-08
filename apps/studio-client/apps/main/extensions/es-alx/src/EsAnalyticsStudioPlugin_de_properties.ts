import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "EsAnalyticsStudioPlugin" for Locale "de".
 * @see EsAnalyticsStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(EsAnalyticsStudioPlugin_properties, {
  chart_container_label: "Eindeutige Seitenaufrufe zu diesem Inhalt",
  chart_data_unavailable: "Keine Daten verfügbar",
  chart_time_stamp_unavailable: "-",
  chart_time_range_label: "Zeitintervall",
  chart_time_stamp_update: "Zuletzt abgeholt:",
  shortDateFormat: "d.m.Y",
  dateFormat: "d.m.Y H:i",
  chart_label_page_views: "Seitenaufrufe",
  chart_label_publications: "Publikationen",
  chart_title_page_views: "Seitenaufrufe",
  chart_title_publications: "Publikationen",
  chart_last_7_days: "Letzte 7 Tage",
  chart_last_30_days: "Letzte 30 Tage",
  widget_title: "Performance",
  widget_type: "Site Performance",
  widget_description: "Site Performance Statistik",
  widget_combo_root_channel_label: "Site",
  widget_title_channel_undefined: "Keine Site ausgewählt",
});
