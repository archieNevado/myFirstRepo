import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import AlxDocTypes_properties from "./AlxDocTypes_properties";

/**
 * Overrides of ResourceBundle "AlxDocTypes_properties" for locale "de".
 * @see AlxDocTypes_properties
 */
ResourceBundleUtil.override(AlxDocTypes_properties, {
  CMALXBaseList_displayName: "Analytics Basis Liste",
  CMALXBaseList_maxLength_displayName: "Maximale Länge",
  CMALXBaseList_maxLength_emptyText: "Geben Sie die maximale Länge der Analytics Page Liste an.",
  CMALXBaseList_timeRange_displayName: "Zeitspanne",
  CMALXBaseList_timeRange_emptyText: "Geben Sie die Zeitspanne der abzuholenden Daten (in Tagen bis heute) an.",
  CMALXBaseList_analyticsProvider_displayName: "Name des Analytics Anbieters",
  CMALXBaseList_analyticsProvider_emptyText: "Geben Sie den Namen des Analytics Anbieters an.",
  CMALXPageList_displayName: "Analytics Page Liste",
  CMALXPageList_documentType_displayName: "Inhaltstyp",
  CMALXPageList_documentType_emptyText: "Gewünschter Inhaltstyp.",
  CMALXPageList_baseChannel_displayName: "Basis Kanal",
  CMALXPageList_baseChannel_emptyText: "Geben Sie hier den Basis Kanal an.",
  CMALXPageList_defaultContent_displayName: "Standardinhalt",
  CMALXPageList_defaultContent_emptyText: "Geben Sie den Standardinhalt an, der bei leerer Analytics Page Liste angezeigt wird.",
  CMALXEventList_displayName: "Analytics Event Liste",
  CMALXEventList_category_displayName: "Event Kategorie",
  CMALXEventList_category_emptyText: "Geben Sie einen Namen für eine Gruppe von zu trackenden Objekten ein (z. B. 'Videos')",
  CMALXEventList_action_displayName: "Event Aktion",
  CMALXEventList_action_emptyText: "Geben Sie den Typ des Events/der Interaktion ein, die getrackt werden soll (z. B. 'Play gedrückt')",
  CMChannel_localSettings_analyticsProvider_displayName: "Name des Standard Analytics Anbieters",
  CMChannel_localSettings_analyticsProvider_emptyText: "Geben Sie den Namen des Standard Analytics Anbieters an.",
});
