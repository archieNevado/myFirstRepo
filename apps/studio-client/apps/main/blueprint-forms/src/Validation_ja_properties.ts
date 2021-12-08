import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import Validation_properties from "./Validation_properties";

/**
 * Overrides of ResourceBundle "Validation" for Locale "ja".
 * @see Validation_properties#INSTANCE
 */
ResourceBundleUtil.override(Validation_properties, {
  Validator_self_referring_text: "リンクリストにそのリンク自体が含まれます。",
  Validator_channel_loop_text: "ページ階層にループが含まれます。",
  Validator_duplicate_segment_text: "同じ名前のセグメントが同じナビゲーションレベルに既に存在します。",
  Validator_not_in_navigation_text: "このページはナビゲーションの一部ではありません。",
  Validator_LengthValidator_text: "テキスト値が長過ぎます。",
  Validator_duplicate_referrer_text: "ページは複数のページによってリンクされています。",
  Validator_NotEmptyMarkupValidator_text: "このフィールドは空にできません。",
});
