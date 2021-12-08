
/**
 * Interface values for ResourceBundle "AMValidators".
 * @see AMValidators_properties#INSTANCE
 */
interface AMValidators_properties {

  Validator_UNKNOWN_CHANNEL_text: string;
  Validator_UNKNOWN_REGION_text: string;
  Validator_METADATA_PROPERTY_NOT_OF_TYPE_STRUCT_text: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "AMValidators".
 * @see AMValidators_properties
 */
const AMValidators_properties: AMValidators_properties = {
  Validator_UNKNOWN_CHANNEL_text: "The channel '{0}' is not configured.",
  Validator_UNKNOWN_REGION_text: "The region '{0}' is not configured.",
  Validator_METADATA_PROPERTY_NOT_OF_TYPE_STRUCT_text: "The property '{0}' must be of type Struct.",
};

export default AMValidators_properties;
