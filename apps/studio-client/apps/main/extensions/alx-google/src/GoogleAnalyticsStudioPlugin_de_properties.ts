import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "GoogleAnalyticsStudioPlugin" for Locale "de".
 * @see GoogleAnalyticsStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(GoogleAnalyticsStudioPlugin_properties, {
  SpacerTitle_navigation: "Navigation",
  SpacerTitle_retrieval: "Retrieval Konfiguration",
  SpacerTitle_layout: "Layout",
  googleanalytics_measurementid_text: "Ungültige Measurement ID. Ein valides Beispiel ist 'G-1234ABCD'.",
  SpacerTitle_googleanalytics_studio_config: "Studio Einstellungen",
  googleanalytics_fav_btn_tooltip: "Google Analytics öffnen",
  googleanalytics_preview_btn_tooltip: "Google Analytics Report öffnen",
  googleanalytics_authfile: "Service Account Key Datei",
});
