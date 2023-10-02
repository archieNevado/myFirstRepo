
/**
 * Interface values for ResourceBundle "Validators".
 * @see Validators_properties#INSTANCE
 */
interface BlueprintValidators_properties {

  Validator_FilenameValidator_text: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "Validators".
 * @see Validators_properties
 */
const BlueprintValidators_properties: BlueprintValidators_properties = { Validator_FilenameValidator_text: "The filename must not contain the following characters: '\\ / : * ? \" < > |'" };

export default BlueprintValidators_properties;
