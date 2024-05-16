import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CatalogValidator_properties from "./CatalogValidator_properties";

/**
 * Overrides of ResourceBundle "CatalogValidator" for Locale "de".
 * @see CatalogValidator_properties#INSTANCE
 */
ResourceBundleUtil.override(CatalogValidator_properties, {
  Validator_categoryIsNotLinkedInCatalog_text: "Diese Kategorie ist nicht in den Produkt-Katalog eingebunden und kann nur über die Suche in der Bibliothek wiedergefunden werden.",
  Validator_productIsNotLinkedInCatalog_text: "Dieses Produkt ist nicht in den Produkt-Katalog eingebunden und kann nur über die Suche in der Bibliothek wiedergefunden werden.",
  Validator_category_loop_text: "Die Kategorienhierarchie enthält eine Schleife.",
  Validator_duplicate_category_parent_text: "Die Kategorie hat mehrere Oberkategorien.",
});
