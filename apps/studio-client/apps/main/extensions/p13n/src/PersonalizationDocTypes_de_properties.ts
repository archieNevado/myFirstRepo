import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import PersonalizationDocTypes_properties from "./PersonalizationDocTypes_properties";

/**
 * Overrides of ResourceBundle "PersonalizationDocTypes" for Locale "de".
 * @see PersonalizationDocTypes_properties#INSTANCE
 */
ResourceBundleUtil.override(PersonalizationDocTypes_properties, {
  CMSelectionRules_text: "Personalisierter Inhalt",
  CMSelectionRules_toolTip: "Eine Liste mit Inhalten aus denen ein Inhalt anhand von Regeln ausgewählt wird",
  CMSelectionRules_title_text: "Titel",
  CMSelectionRules_title_emptyText: "Geben Sie hier den Titel ein",
  CMSelectionRules_title_toolTip: "Titel für den personalisierten Inhalt",
  CMSelectionRules_text_text: "Beschreibender Text",
  CMSelectionRules_text_toolTip: "Beschreibender Text",
  CMSelectionRules_rules_text: "Regeln",
  CMSelectionRules_rules_toolTip: "Eine Liste von Inhalten mit Regeln. Die Regeln werden ihrer Reihenfolge nach ausgewertet und der erste passende Inhalt ausgewählt.",
  CMSelectionRules_defaultContent_text: "Standardinhalt",
  CMSelectionRules_defaultContent_emptyText: "Ziehen Sie Inhalte aus der Bibliothek hierher.",
  CMSelectionRules_defaultContent_toolTip: "Der Standardinhalt wird benutzt, wenn keine Regel zutrifft oder die Regeln Fehler enthalten",
  CMSegment_text: "Kunden-Segment",
  CMSegment_toolTip: "Ein Segment gruppiert eine Menge von Website Kunden anhand von Bedingungen in benannte Segmente",
  CMSegment_description_text: "Segmentbeschreibung",
  CMSegment_description_emptyText: "Fügen Sie eine interne Beschreibung hinzu",
  CMSegment_description_toolTip: "Beschreibung des definierten Segmentes",
  CMSegment_conditions_text: "Segmentbedingungen",
  CMSegment_conditions_toolTip: "Bedingungen, die erfüllt sein müssen, damit ein Kunde diesem Segment zugeordnet wird",
  CMUserProfile_text: "Kundenpersona",
  CMUserProfile_toolTip: "Kundenpersona werden zur Simulation und Preview von personalisierten Inhalten benutzt. Sie definieren Profileigenschaften von virtuellen Benutzern",
  CMUserProfile_profileSettings_text: "Kontextdaten",
  CMUserProfile_profileSettings_emptyText: "Geben Sie hier die Kontextdaten ein. Beispiel: interest.sports=true.",
  CMUserProfile_profileSettings_toolTip: "Eine Liste mit Schlüssel/Wert Paaren zur Konfiguration des Kundenpersona Kontext",
  CMUserProfile_favlabel: "Kundenpersona",
  CMP13NSearch_text: "Personalisierte Suche",
  CMP13NSearch_toolTip: "Personalisierte Suchen werden dafür genutzt, Suchanfragen mit Context Informationen und anderen Erweiterungen zu füllen/zu erstellen",
  CMP13NSearch_documentType_text: "Inhaltstyp",
  CMP13NSearch_documentType_toolTip: "Geben Sie hier den Documenttyp an.",
  CMP13NSearch_documentType_emptyText: "Geben Sie hier den Documenttyp an.",
  CMP13NSearch_searchQuery_text: "Suchanfrage",
  CMP13NSearch_searchQuery_toolTip: "Geben Sie hier die Suchanfrage an.",
  CMP13NSearch_searchQuery_emptyText: "Geben Sie hier die personalisierte Suchanfrage ein, wie zum Beispiel 'name:Angebot* AND userKeywords(limit:-1, field:keywords, threshold:0.6, context:myContext)'",
  CMP13NSearch_maxLength_text: "Maximale Anzahl Ergebnisse",
  CMP13NSearch_maxLength_toolTip: "Geben Sie hier die maximale Anzahl der Ergebnisse an",
  CMP13NSearch_maxLength_emptyText: "Maximale Anzahl der Ergebnisse",
  CMP13NSearch_defaultContent_text: "Standardinhalt",
  CMP13NSearch_defaultContent_emptyText: "Ziehen Sie Inhalte aus der Bibliothek hierher.",
  CMP13NSearch_searchContext_text: "Suche ab der Seitenebene",
  CMP13NSearch_searchContext_emptyText: "Ziehen Sie einen Navigationskontext aus der Bibliothek hierher.",
});
