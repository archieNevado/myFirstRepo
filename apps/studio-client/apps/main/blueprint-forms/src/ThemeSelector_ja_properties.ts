import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import ThemeSelector_properties from "./ThemeSelector_properties";

/**
 * Overrides of ResourceBundle "ThemeSelector" for Locale "ja".
 * @see ThemeSelector_properties#INSTANCE
 */
ResourceBundleUtil.override(ThemeSelector_properties, {
  ThemeSelector_default_text: "テーマが選択されていません",
  ThemeSelector_default_description: "テーマはマスターから引き継がれるか、システムのデフォルトが代替で使用されます。",
});
