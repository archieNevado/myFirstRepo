import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import GoogleAnalyticsStudioPluginContentTypes_properties from "./GoogleAnalyticsStudioPluginContentTypes_properties";

/**
 * Overrides of ResourceBundle "GoogleAnalyticsStudioPluginContentTypes" for Locale "de".
 * @see GoogleAnalyticsStudioPluginContentTypes_properties#INSTANCE
 */
ResourceBundleUtil.override(GoogleAnalyticsStudioPluginContentTypes_properties, {
  // text and true_text are kept same deliberately
  "CMChannel_localSettings.googleAnalytics.disabled_text": "Deaktiviert",
  "CMChannel_localSettings.googleAnalytics.disabled_true_text": "Deaktiviert",
  "CMChannel_localSettings.googleAnalytics.measurementId_emptyText": "Geben Sie hier die Google Analytics Measurement ID ein.",
  "CMChannel_localSettings.googleAnalytics.homeUrl_emptyText": "Geben Sie hier die Google Analytics Home URL ein.",
  "CMChannel_localSettings.googleAnalytics.pageReport_emptyText": "Geben Sie hier den Google Analytics Page Report Namen ein (der Default ist 'content-pages').",
  "CMChannel_localSettings.googleAnalytics.propertyId_emptyText": "Geben Sie hier die Google Analytics Property ID ein.",
  "CMChannel_localSettings.googleAnalytics.authFile_text": "Service Account Key Datei",
  "CMChannel_localSettings.googleAnalytics.limit_text": "Retrieval Limit",
  "CMChannel_localSettings.googleAnalytics.limit_emptyText": "Geben Sie die maximale Anzahl der zu holenden Datensätze an.",
  "CMChannel_localSettings.googleAnalytics.interval_text": "Retrieval Intervall",
  "CMChannel_localSettings.googleAnalytics.interval_emptyText": "Geben Sie hier das Intervall (in Minuten) an, in dem Daten geholt werden sollen. 0 deaktiviert das Retrieval.",
  "CMALXBaseList_localSettings.googleAnalytics.authFile_text": "Service Account Key Datei",
  "CMALXBaseList_localSettings.googleAnalytics.propertyId_text": "Property ID",
  "CMALXBaseList_localSettings.googleAnalytics.propertyId_emptyText": "Geben Sie hier die Google Analytics Property ID ein.",
  "CMALXBaseList_localSettings.googleAnalytics.limit_text": "Retrieval Limit",
  "CMALXBaseList_localSettings.googleAnalytics.limit_emptyText": "Geben Sie die maximale Anzahl der zu holenden Datensätze an.",
  "CMALXBaseList_localSettings.googleAnalytics.interval_text": "Retrieval Intervall",
  "CMALXBaseList_localSettings.googleAnalytics.interval_emptyText": "Geben Sie hier das Intervall (in Minuten) an, in dem Daten geholt werden sollen. 0 deaktiviert das Retrieval.",
});
