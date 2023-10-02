
/**
 * Interface values for ResourceBundle "CustomTimeZones".
 * @see CustomTimeZones_properties#INSTANCE
 */
interface CustomTimeZones_properties {
  "America/New_York": string;
  "America/Los_Angeles": string;
  "Europe/Berlin": string;
  "Europe/London": string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "CustomTimeZones".
 * @see CustomTimeZones_properties
 */
const CustomTimeZones_properties: CustomTimeZones_properties = {
  "America/New_York": "America - New York",
  "America/Los_Angeles": "America - Los Angeles",
  "Europe/Berlin": "Europe - Berlin",
  "Europe/London": "Europe - London",
};

export default CustomTimeZones_properties;
