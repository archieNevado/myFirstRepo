import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import BlueprintTabs_properties from "./BlueprintTabs_properties";

/**
 * Overrides of ResourceBundle "BlueprintTabs" for Locale "ja".
 * @see BlueprintTabs_properties#INSTANCE
 */
ResourceBundleUtil.override(BlueprintTabs_properties, {
  Tab_content_title: "コンテンツ",
  Tab_taxonomy_title: "分類",
  Tab_extras_title: "メタデータ",
  Tab_details_title: "設定",
  Tab_locale_title: "言語",
  Tab_system_title: "システム",
  Tab_personalization_title: "カスタマイズ",
  Tab_standard_title: "コンテンツ",
});
