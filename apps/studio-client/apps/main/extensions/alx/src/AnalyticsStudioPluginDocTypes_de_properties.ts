import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import AnalyticsStudioPluginDocTypes_properties from "./AnalyticsStudioPluginDocTypes_properties";

/**
 * Overrides of ResourceBundle "AnalyticsStudioPluginDocTypes" for Locale "de".
 * @see AnalyticsStudioPluginDocTypes_properties#INSTANCE
 */
ResourceBundleUtil.override(AnalyticsStudioPluginDocTypes_properties, {
  CMALXBaseList_text: "Analytics Basis Liste",
  CMALXPageList_text: "Analytics Page Liste",
  CMALXPageList_documentType_text: "Inhaltstyp",
  CMALXPageList_documentType_emptyText: "Gewünschter Inhaltstyp.",
  CMALXPageList_baseChannel_text: "Basis Kanal",
  CMALXPageList_baseChannel_emptyText: "Geben Sie hier den Basis Kanal an.",
  CMALXPageList_defaultContent_text: "Standardinhalt",
  CMALXPageList_defaultContent_emptyText: "Geben Sie den Standardinhalt an, der bei leerer Analytics Page Liste angezeigt wird.",
  CMALXBaseList_maxLength_text: "Maximale Länge",
  CMALXBaseList_maxLength_emptyText: "Geben Sie die maximale Länge der Analytics Page Liste an.",
  CMALXBaseList_timeRange_text: "Zeitspanne",
  CMALXBaseList_timeRange_emptyText: "Geben Sie die Zeitspanne der abzuholenden Daten (in Tagen bis heute) an.",
  CMALXBaseList_analyticsProvider_text: "Name des Analytics Anbieters",
  CMALXBaseList_analyticsProvider_emptyText: "Geben Sie den Namen des Analytics Anbieters an.",
  CMALXEventList_text: "Analytics Event Liste",
  CMALXEventList_category_text: "Event Kategorie",
  CMALXEventList_category_emptyText: "Geben Sie einen Namen für eine Gruppe von zu trackenden Objekten ein (z. B. 'Videos')",
  CMALXEventList_action_text: "Event Aktion",
  CMALXEventList_action_emptyText: "Geben Sie den Typ des Events/der Interaktion ein, die getrackt werden soll (z. B. 'Play gedrückt')",
  CMALXEventList_defaultContent_text: "Standardinhalt",
  "CMChannel_localSettings.analyticsProvider_text": "Name des Standard Analytics Anbieters",
  "CMChannel_localSettings.analyticsProvider_emptyText": "Geben Sie den Namen des Standard Analytics Anbieters an.",
});
