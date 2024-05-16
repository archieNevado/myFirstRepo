import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import TopicPages_properties from "./TopicPages_properties";

/**
 * Overrides of ResourceBundle "TopicPages" for Locale "de".
 * @see TopicPages_properties#INSTANCE
 */
ResourceBundleUtil.override(TopicPages_properties, {
  TopicPages_administration_title: "Themenseiten",
  TopicPages_grid_header_name: "Schlagwort",
  TopicPages_search_title: "Suche",
  TopicPages_grid_header_page: "Seite",
  TopicPages_grid_header_options: "Seite anzeigen",
  TopicPages_search_emptyText: "Suchen…",
  TopicPages_search_search_tooltip: "Suche starten",
  TopicPages_taxonomy_combo_title: "Schlagwort anzeigen",
  TopicPages_taxonomy_combo_emptyText: "Keine Schlagworte verfügbar",
  TopicPages_create_link: "Neue angepasste Seite erzeugen",
  TopicPages_no_preferred_site: "Keine präferierte Site ausgewählt",
  TopicPages_no_channel_configured_title: "Fehler",
  TopicPages_no_channel_configured: "Die Themenseiten Konfiguration für die präferierte Site '{0}' ist nicht mit einem Default-Layout für neue Themenseiten verknüpft.",
  TopicPages_name: "Themenseite",
  TopicPages_filtered: "Es werden nicht alle Themenseiten dargestellt, bitte geben Sie einen Suchbegriff an um die Anzahl zu reduzieren",
  TopicPages_deletion_title: "Themenseite löschen",
  TopicPages_deletion_tooltip: "Themenseite löschen",
  TopicPages_deletion_text: "Möchten Sie die Themenseite '{0}' wirklich löschen?",
  TopicPages_root_channel_checked_out_title: "Fehler",
  TopicPages_root_channel_checked_out_msg: "Die Hauptseite der präferierten Site '{0}' wird von einem anderen Benutzer bearbeitet.",
  TopicPages_root_channel_not_found_title: "Fehler",
  TopicPages_root_channel_not_found_msg: "Die Hauptseite der präferierten Site '{0}' konnte nicht aufgelöst werden. Die Aktualisierung der Themenseitenverlinkung ist fehlgeschlagen.",
  topic_pages_button_tooltip: "Themenseiten öffnen",
  topic_pages_button_no_preferred_site_tooltip: "Bitte wählen Sie eine präferierte Site aus, um die Themenseiten zu öffnen.",
  topic_pages_button_no_topic_page_settings_tooltip: "Es konnte keine Hauptseite für die Themenseiten gefunden werden. Bitte überprüfen Sie die 'TopicPages'-Einstellungen.",
});
