
/**
 * Interface values for ResourceBundle "NewContentSettingsStudioPlugin".
 * @see NewContentSettingsStudioPlugin_properties#INSTANCE
 */
interface NewContentSettingsStudioPlugin_properties {

/**
 *#######################################################################################################################
 * The default content types to apply for the link lists create menu.
 *#######################################################################################################################
 */
  default_link_list_contentTypes: string;
  default_new_content_menu_contentTypes: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "NewContentSettingsStudioPlugin".
 * @see NewContentSettingsStudioPlugin_properties
 */
const NewContentSettingsStudioPlugin_properties: NewContentSettingsStudioPlugin_properties = {
  default_link_list_contentTypes: "CMArticle,CMCollection,CMDownload,CMGallery,CMImageMap,CMTeaser,CMChannel,CMPicture,CMQueryList,CMVideo",
  default_new_content_menu_contentTypes: "CMArticle,CMCollection,CMPicture",
};

export default NewContentSettingsStudioPlugin_properties;
