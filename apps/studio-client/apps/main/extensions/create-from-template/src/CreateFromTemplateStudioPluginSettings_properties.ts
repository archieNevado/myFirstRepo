
/**
 * Interface values for ResourceBundle "CreateFromTemplateStudioPluginSettings".
 * @see CreateFromTemplateStudioPluginSettings_properties#INSTANCE
 */
interface CreateFromTemplateStudioPluginSettings_properties {

/**
 * The Doctype that represents a Page
 */
  doctype: string;
/**
 * Default Path for Page Templates
 */
  template_paths: string;
/**
 * Image Size (in px)
 */
  template_icon_size: string;
/**
 * Path fragment for templates
 */
  template_folder_fragment: string;
/**
 * Property of the Page Doctype, which contains the navigation child documents
 */
  children_property: string;
/**
 * Property names of the form model bean
 */
  parent_property: string;
  template_property: string;
  editorial_folder_property: string;
/**
 * Name of the CMSymbol, which identifies a valid tempate below the given folders
 */
  template_descriptor_name: string;
  template_descriptor_type: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "CreateFromTemplateStudioPluginSettings".
 * @see CreateFromTemplateStudioPluginSettings_properties
 */
const CreateFromTemplateStudioPluginSettings_properties: CreateFromTemplateStudioPluginSettings_properties = {
  doctype: "CMChannel",
  template_paths: "Options/Settings/Templates/CMChannel,/Settings/Options/Settings/Templates/CMChannel",
  template_icon_size: "140",
  template_folder_fragment: "Templates",
  children_property: "children",
  parent_property: "parentChannel",
  template_property: "template",
  editorial_folder_property: "editorialFolder",
  template_descriptor_name: "Descriptor",
  template_descriptor_type: "CMSymbol",
};

export default CreateFromTemplateStudioPluginSettings_properties;
