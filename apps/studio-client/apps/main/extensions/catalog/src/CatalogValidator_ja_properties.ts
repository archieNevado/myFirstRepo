import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CatalogValidator_properties from "./CatalogValidator_properties";

/**
 * Overrides of ResourceBundle "CatalogValidator" for Locale "ja".
 * @see CatalogValidator_properties#INSTANCE
 */
ResourceBundleUtil.override(CatalogValidator_properties, {
  Validator_categoryIsNotLinkedInCatalog_text: "このカテゴリーは商品カタログでリンクしておらず、ライブラリ検索でしか検索できません。",
  Validator_productIsNotLinkedInCatalog_text: "この商品は商品カタログでリンクしておらず、ライブラリ検索でしか検索できません。",
  Validator_category_loop_text: "カテゴリー階層にループが含まれています。",
  Validator_duplicate_category_parent_text: "カテゴリーは複数の親カテゴリーによりリンクしています。",
});
