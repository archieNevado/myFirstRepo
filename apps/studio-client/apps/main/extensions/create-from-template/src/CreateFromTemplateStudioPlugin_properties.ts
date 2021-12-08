
/**
 * Interface values for ResourceBundle "CreateFromTemplateStudioPlugin".
 * @see CreateFromTemplateStudioPlugin_properties#INSTANCE
 */
interface CreateFromTemplateStudioPlugin_properties {

/**
 *templates
 */
  text: string;
  channel_folder_text: string;
  editorial_folder_text: string;
  folders_text: string;
  choose_template_text: string;
/**
 * Default Path for Page Templates
 */
  template_paths: string;
/**
 *Validation
 */
  template_create_missing_value: string;
  name_not_valid_value: string;
  page_folder_combo_validation_message: string;
  no_parent_page_selected_warning: string;
  no_parent_page_selected_warning_buttonText: string;
  editor_folder_could_not_create_message: string;
  page_folder_could_not_create_message: string;
/**
 *Properties
 */
  name_label: string;
  name_text: string;
  name_empty_text: string;
  template_chooser_empty_text: string;
  parent_label: string;
/**
 *Dialog
 */
  dialog_title: string;
/**
 *menus and button
 */
  quick_create_tooltip: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "CreateFromTemplateStudioPlugin".
 * @see CreateFromTemplateStudioPlugin_properties
 */
const CreateFromTemplateStudioPlugin_properties: CreateFromTemplateStudioPlugin_properties = {
  text: "Page from Template",
  channel_folder_text: "Base Folder for Page",
  editorial_folder_text: "Base Folder for Content",
  folders_text: "Folders",
  choose_template_text: "Template for the Page",
  template_paths: "Options/Settings/Templates/CMChannel,/Settings/Options/Settings/Templates/CMChannel",
  template_create_missing_value: "This field must not be empty.",
  name_not_valid_value: "Name is not valid.",
  page_folder_combo_validation_message: "The field must not be empty and the folder must not exist.",
  no_parent_page_selected_warning: "This will create a new page which is not part of the navigation structure. Are you sure?",
  no_parent_page_selected_warning_buttonText: "Create",
  editor_folder_could_not_create_message: "Could not create the folder for the editorial content.",
  page_folder_could_not_create_message: "Could not create the folder for the navigation.",
  name_label: "Name",
  name_text: "A new folder with this name is created below the base folders.",
  name_empty_text: "Enter name of content item.",
  template_chooser_empty_text: "Please choose a template.",
  parent_label: "Navigation Parent",
  dialog_title: "New {0}",
  quick_create_tooltip: "Create new content item",
};

export default CreateFromTemplateStudioPlugin_properties;
