import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CatalogActions_properties from "./CatalogActions_properties";

/**
 * Overrides of ResourceBundle "CatalogActions" for Locale "de".
 * @see CatalogActions_properties#INSTANCE
 */
ResourceBundleUtil.override(CatalogActions_properties, {
  Action_unlink_text: "Entfernen",
  Action_unlink_tooltip: "Aus aktueller Kategorie entfernen. In anderen Kategorien behalten.",
  Action_unlink_title: "Verknüpfung entfernen",
  Action_unlink_message: "Soll die Verknüpfung zur Kategorie '{0}' entfernt werden?",
});
