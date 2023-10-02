
/**
 * Interface values for ResourceBundle "ThemeSelector".
 * @see ThemeSelector_properties#INSTANCE
 */
interface ThemeSelector_properties {

  ThemeSelector_default_text: string;
  ThemeSelector_default_description: string;
  ThemeSelector_no_image_tooltip: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ThemeSelector".
 * @see ThemeSelector_properties
 */
const ThemeSelector_properties: ThemeSelector_properties = {
  ThemeSelector_default_text: "No Theme Selected",
  ThemeSelector_default_description: "Theme is inherited from Master or fallback to System default.",
  ThemeSelector_no_image_tooltip: "No detailed image information available",
};

export default ThemeSelector_properties;
