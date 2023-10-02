import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import AMValidators_properties from "./AMValidators_properties";

/**
 * Overrides of ResourceBundle "AMValidators" for Locale "ja".
 * @see AMValidators_properties#INSTANCE
 */
ResourceBundleUtil.override(AMValidators_properties, {
  Validator_UNKNOWN_CHANNEL_text: "チャンネル '{0}' が設定されていません。",
  Validator_UNKNOWN_REGION_text: "地域 '{0}' が設定されていません。",
  Validator_METADATA_PROPERTY_NOT_OF_TYPE_STRUCT_text: "プロパティ '{0}' はタイプStructでなければなりません。",
});
