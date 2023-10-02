
/**
 * Interface values for ResourceBundle "EsAnalyticsStudioPlugin".
 * @see EsAnalyticsStudioPlugin_properties#INSTANCE
 */
interface EsAnalyticsStudioPlugin_properties {

  chart_container_label: string;
  chart_data_unavailable: string;
  chart_time_stamp_unavailable: string;
  chart_time_range_label: string;
  chart_time_stamp_update: string;
  shortDateFormat: string;
  dateFormat: string;
  chart_label_page_views: string;
  chart_label_publications: string;
  chart_last_7_days: string;
  chart_title_page_views: string;
  chart_title_publications: string;
  chart_last_30_days: string;
  widget_title: string;
  widget_type: string;
  widget_description: string;
  widget_combo_root_channel_label: string;
  widget_title_channel_undefined: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "EsAnalyticsStudioPlugin".
 * @see EsAnalyticsStudioPlugin_properties
 */
const EsAnalyticsStudioPlugin_properties: EsAnalyticsStudioPlugin_properties = {
  chart_container_label: "Unique Page Impressions for This Content Item",
  chart_data_unavailable: "No data available",
  chart_time_stamp_unavailable: "-",
  chart_time_range_label: "Interval",
  chart_time_stamp_update: "Last fetched:",
  shortDateFormat: "Y/m/d",
  dateFormat: "Y/m/d h:i a",
  chart_label_page_views: "Page Views",
  chart_label_publications: "Publications",
  chart_last_7_days: "Last 7 Days",
  chart_title_page_views: "Page Views",
  chart_title_publications: "Publications",
  chart_last_30_days: "Last 30 Days",
  widget_title: "Performance",
  widget_type: "Site Performance",
  widget_description: "Site Performance Statistics",
  widget_combo_root_channel_label: "Site",
  widget_title_channel_undefined: "No Site Selected",
};

export default EsAnalyticsStudioPlugin_properties;
