import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CustomTimeZones_properties from "./CustomTimeZones_properties";

/**
 * Overrides of ResourceBundle "CustomTimeZones" for Locale "de".
 * @see CustomTimeZones_properties#INSTANCE
 */
ResourceBundleUtil.override(CustomTimeZones_properties, {
  "America/New_York": "Amerika - New York",
  "America/Los_Angeles": "Amerika - Los Angeles",
  "Europe/Berlin": "Europa - Berlin",
  "Europe/London": "Europa - London",
});
