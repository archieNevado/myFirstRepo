import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import BlueprintValidators_properties from "./BlueprintValidators_properties";

/**
 * Overrides of ResourceBundle "Validators" for Locale "ja".
 * @see Validators_properties#INSTANCE
 */
ResourceBundleUtil.override(BlueprintValidators_properties, { Validator_FilenameValidator_text: "ファイル名には、次の文字を使用できません： '\\ / : * ? \" < > |'" });
