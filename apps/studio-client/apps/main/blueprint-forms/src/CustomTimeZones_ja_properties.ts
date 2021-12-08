import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CustomTimeZones_properties from "./CustomTimeZones_properties";

/**
 * Overrides of ResourceBundle "CustomTimeZones" for Locale "ja".
 * @see CustomTimeZones_properties#INSTANCE
 */
ResourceBundleUtil.override(CustomTimeZones_properties, {
  "America/New_York": "ニューヨーク（アメリカ）",
  "America/Los_Angeles": "ロサンゼルス（アメリカ）",
  "Europe/Berlin": "ベルリン（ヨーロッパ）",
  "Europe/London": "ロンドン（ヨーロッパ）",
});
