import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CustomTimeZones_properties from "./CustomTimeZones_properties";

/**
 * Overrides of ResourceBundle "CustomTimeZones" for Locale "ja".
 * @see CustomTimeZones_properties#INSTANCE
 */
ResourceBundleUtil.override(CustomTimeZones_properties, {
  "America/New_York": "アメリカ - ニューヨーク",
  "America/Los_Angeles": "アメリカ - ロサンゼルス",
  "Europe/Berlin": "ヨーロッパ - ベルリン",
  "Europe/London": "ヨーロッパ - ロンドン",
});
