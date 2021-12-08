
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
  googleanalytics_webpropertyid_val: string;
  googleanalytics_webpropertyid_mask: string;
  googleanalytics_webpropertyid_text: string;
/**
 * display name of the service provider
 */
  googleanalytics_service_provider: string;
  googleanalytics_p12file: string;
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
  googleanalytics_webpropertyid_val: "^UA\\-\\d+\\-\\d+$",
  googleanalytics_webpropertyid_mask: "[UA\\d-]",
  googleanalytics_webpropertyid_text: "Invalid Web Property ID. A valid value would be 'UA-12345678-1'.",
  googleanalytics_service_provider: "Google Analytics",
  googleanalytics_p12file: "P12 Key File",
};

export default GoogleAnalyticsStudioPlugin_properties;
