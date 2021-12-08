import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import ThemeSelector_properties from "./ThemeSelector_properties";

/**
 * Overrides of ResourceBundle "ThemeSelector" for Locale "de".
 * @see ThemeSelector_properties#INSTANCE
 */
ResourceBundleUtil.override(ThemeSelector_properties, {
  ThemeSelector_default_text: "Kein Theme ausgewählt",
  ThemeSelector_default_description: "Theme wird vom übergeordneter Seite geerbt bzw. auf den Systemstandard zurückgegriffen.",
});
