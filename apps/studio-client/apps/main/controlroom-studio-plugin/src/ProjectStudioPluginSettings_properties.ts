
/**
 * Interface values for ResourceBundle "ProjectStudioPluginSettings".
 * @see ProjectStudioPluginSettings_properties#INSTANCE
 */
interface ProjectStudioPluginSettings_properties {

/**
 *#######################################################################################################################
 * The default content types to apply for the project contents create menu.
 *#######################################################################################################################
 */
  default_project_content_quickcreate_contentTypes: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ProjectStudioPluginSettings".
 * @see ProjectStudioPluginSettings_properties
 */
const ProjectStudioPluginSettings_properties: ProjectStudioPluginSettings_properties = { default_project_content_quickcreate_contentTypes: "CMArticle,CMTeaser,CMPicture,CMVideo,CMDownload,CMQueryList,CMCollection,CMChannel" };

export default ProjectStudioPluginSettings_properties;
