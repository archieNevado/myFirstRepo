import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import BlueprintValidators_properties from "./BlueprintValidators_properties";

/**
 * Overrides of ResourceBundle "Validators" for Locale "de".
 * @see Validators_properties#INSTANCE
 */
ResourceBundleUtil.override(BlueprintValidators_properties, { Validator_FilenameValidator_text: "Der Dateiname darf keins der folgenden Zeichen enthalten: '\\ / : * ? \" < > |'" });
