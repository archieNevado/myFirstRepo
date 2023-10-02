import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "CatalogActions".
 * @see CatalogActions_properties#INSTANCE
 */
interface CatalogActions_properties {

/**
 * Localization properties for UnlinkAction
 */
  Action_unlink_text: string;
  Action_unlink_tooltip: string;
  Action_unlink_icon: string;
  Action_unlink_title: string;
  Action_unlink_message: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "CatalogActions".
 * @see CatalogActions_properties
 */
const CatalogActions_properties: CatalogActions_properties = {
  Action_unlink_text: "Remove",
  Action_unlink_tooltip: "Remove from current Category. Keep in other Categories.",
  Action_unlink_icon: CoreIcons_properties.remove_small,
  Action_unlink_title: "Remove Item",
  Action_unlink_message: "Remove the current item from the category '{0}'?",
};

export default CatalogActions_properties;
