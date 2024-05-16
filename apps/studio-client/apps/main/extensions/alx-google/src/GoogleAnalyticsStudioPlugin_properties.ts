
/**
 * Interface values for ResourceBundle "GoogleAnalyticsStudioPlugin".
 * @see GoogleAnalyticsStudioPlugin_properties#INSTANCE
 */
interface GoogleAnalyticsStudioPlugin_properties {

/**
 * document metadata form
 */
  SpacerTitle_navigation: string;
  SpacerTitle_retrieval: string;
  SpacerTitle_layout: string;
  SpacerTitle_googleanalytics: string;
  SpacerTitle_googleanalytics_studio_config: string;
/**
 * button in favorites bar
 */
  googleanalytics_fav_btn_text: string;
  googleanalytics_fav_btn_tooltip: string;
  googleanalytics_preview_btn_tooltip: string;
/**
 * report button in preview
 */
  googleanalytics_measurementid_val: string;
  googleanalytics_measurementid_mask: string;
  googleanalytics_measurementid_text: string;
/**
 * display name of the service provider
 */
  googleanalytics_service_provider: string;
  googleanalytics_authfile: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "GoogleAnalyticsStudioPlugin".
 * @see GoogleAnalyticsStudioPlugin_properties
 */
const GoogleAnalyticsStudioPlugin_properties: GoogleAnalyticsStudioPlugin_properties = {
  SpacerTitle_navigation: "Navigation",
  SpacerTitle_retrieval: "Retrieval Configuration",
  SpacerTitle_layout: "Layout",
  SpacerTitle_googleanalytics: "Google Analytics",
  SpacerTitle_googleanalytics_studio_config: "Studio Settings",
  googleanalytics_fav_btn_text: "Google",
  googleanalytics_fav_btn_tooltip: "Open Google Analytics",
  googleanalytics_preview_btn_tooltip: "Open Google Analytics Report",
  googleanalytics_measurementid_val: "^G\\-$",
  googleanalytics_measurementid_mask: "[G\\-]",
  googleanalytics_measurementid_text: "Invalid Measurement ID. A valid value would be 'G-1234ABCD'.",
  googleanalytics_service_provider: "Google Analytics",
  googleanalytics_authfile: "Service Account Key File",
};

export default GoogleAnalyticsStudioPlugin_properties;
