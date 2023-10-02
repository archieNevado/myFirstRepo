import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "SFMCP13NStudioPlugin".
 * @see SFMCP13NStudioPlugin_properties#INSTANCE
 */
interface SFMCP13NStudioPlugin_properties {

/**
 * Localization properties for remove journey action
 */
  Action_removeJourney_text: string;
  Action_removeJourney_tooltip: string;
  Action_removeJourney_icon: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "SFMCP13NStudioPlugin".
 * @see SFMCP13NStudioPlugin_properties
 */
const SFMCP13NStudioPlugin_properties: SFMCP13NStudioPlugin_properties = {
  Action_removeJourney_text: "Remove",
  Action_removeJourney_tooltip: "Remove the journey from the persona",
  Action_removeJourney_icon: CoreIcons_properties.remove_small,
};

export default SFMCP13NStudioPlugin_properties;
