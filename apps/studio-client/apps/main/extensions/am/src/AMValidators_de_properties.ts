import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import AMValidators_properties from "./AMValidators_properties";

/**
 * Overrides of ResourceBundle "AMValidators" for Locale "de".
 * @see AMValidators_properties#INSTANCE
 */
ResourceBundleUtil.override(AMValidators_properties, {
  Validator_UNKNOWN_CHANNEL_text: "Der Kanal '{0}' ist nicht konfiguriert.",
  Validator_UNKNOWN_REGION_text: "Die Region '{0}' ist nicht konfiguriert.",
  Validator_METADATA_PROPERTY_NOT_OF_TYPE_STRUCT_text: "Die Property '{0}' muss vom Typ Struct sein.",
});
