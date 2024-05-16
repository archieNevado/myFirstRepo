import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import P13nDocTypes_properties from "./P13nDocTypes_properties";

/**
 * Overrides of ResourceBundle "P13nDocTypes_properties" for locale "de".
 * @see P13nDocTypes_properties
 */
ResourceBundleUtil.override(P13nDocTypes_properties, {
  CMSelectionRules_displayName: "Personalisierter Inhalt",
  CMSelectionRules_description: "Eine Liste mit Inhalten aus denen ein Inhalt anhand von Regeln ausgewählt wird",
  CMSelectionRules_title_displayName: "Titel",
  CMSelectionRules_title_description: "Titel für den personalisierten Inhalt",
  CMSelectionRules_title_emptyText: "Geben Sie hier den Titel ein",
  CMSelectionRules_text_displayName: "Beschreibender Text",
  CMSelectionRules_text_description: "Beschreibender Text",
  CMSelectionRules_rules_displayName: "Regeln",
  CMSelectionRules_rules_description: "Eine Liste von Inhalten mit Regeln. Die Regeln werden ihrer Reihenfolge nach ausgewertet und der erste passende Inhalt ausgewählt.",
  CMSelectionRules_defaultContent_displayName: "Standardinhalt",
  CMSelectionRules_defaultContent_description: "Der Standardinhalt wird benutzt, wenn keine Regel zutrifft oder die Regeln Fehler enthalten",
  CMSelectionRules_defaultContent_emptyText: "Ziehen Sie Inhalte aus der Bibliothek hierher.",
  CMSegment_displayName: "Kunden-Segment",
  CMSegment_description: "Ein Segment gruppiert eine Menge von Website Kunden anhand von Bedingungen in benannte Segmente",
  CMSegment_description_displayName: "Segmentbeschreibung",
  CMSegment_description_description: "Beschreibung des definierten Segmentes",
  CMSegment_description_emptyText: "Fügen Sie eine interne Beschreibung hinzu",
  CMSegment_conditions_displayName: "Segmentbedingungen",
  CMSegment_conditions_description: "Bedingungen, die erfüllt sein müssen, damit ein Kunde diesem Segment zugeordnet wird",
  CMUserProfile_displayName: "Kundenpersona",
  CMUserProfile_description: "Kundenpersona werden zur Simulation und Preview von personalisierten Inhalten benutzt. Sie definieren Profileigenschaften von virtuellen Benutzern",
  CMUserProfile_profileSettings_displayName: "Kontextdaten",
  CMUserProfile_profileSettings_description: "Eine Liste mit Schlüssel/Wert Paaren zur Konfiguration des Kundenpersona Kontext",
  CMUserProfile_profileSettings_emptyText: "Geben Sie hier die Kontextdaten ein. Beispiel: interest.sports=true.",
  CMP13NSearch_displayName: "Personalisierte Suche",
  CMP13NSearch_description: "Personalisierte Suchen werden dafür genutzt, Suchanfragen mit Context Informationen und anderen Erweiterungen zu füllen/zu erstellen",
  CMP13NSearch_documentType_displayName: "Inhaltstyp",
  CMP13NSearch_documentType_description: "Geben Sie hier den Documenttyp an.",
  CMP13NSearch_documentType_emptyText: "Geben Sie hier den Documenttyp an.",
  CMP13NSearch_searchQuery_displayName: "Suchanfrage",
  CMP13NSearch_searchQuery_description: "Geben Sie hier die Suchanfrage an.",
  CMP13NSearch_searchQuery_emptyText: "Geben Sie hier die personalisierte Suchanfrage ein, wie zum Beispiel 'name:Angebot* AND userKeywords(limit:-1, field:keywords, threshold:0.6, context:myContext)'",
  CMP13NSearch_maxLength_displayName: "Maximale Anzahl Ergebnisse",
  CMP13NSearch_maxLength_description: "Geben Sie hier die maximale Anzahl der Ergebnisse an",
  CMP13NSearch_maxLength_emptyText: "Maximale Anzahl der Ergebnisse",
  CMP13NSearch_defaultContent_displayName: "Standardinhalt",
  CMP13NSearch_defaultContent_emptyText: "Ziehen Sie Inhalte aus der Bibliothek hierher.",
  CMP13NSearch_searchContext_displayName: "Suche ab der Seitenebene",
  CMP13NSearch_searchContext_emptyText: "Ziehen Sie einen Navigationskontext aus der Bibliothek hierher.",
});
