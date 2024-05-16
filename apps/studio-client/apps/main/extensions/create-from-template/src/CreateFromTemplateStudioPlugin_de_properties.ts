import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CreateFromTemplateStudioPlugin_properties from "./CreateFromTemplateStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "CreateFromTemplateStudioPlugin" for Locale "de".
 * @see CreateFromTemplateStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(CreateFromTemplateStudioPlugin_properties, {
  text: "Seite aus Vorlage",
  folders_text: "Ordner",
  channel_folder_text: "Basisordner der Seite",
  editorial_folder_text: "Basisordner der Inhalte",
  choose_template_text: "Vorlage für die Seite",
  template_create_missing_value: "Dieses Feld darf nicht leer sein.",
  name_not_valid_value: "Name ist nicht gültig.",
  page_folder_combo_validation_message: "Dieses Feld darf nicht leer sein und der Ordner darf nicht existieren.",
  no_parent_page_selected_warning: "Es wird eine Seite erstellt, die nicht Bestandteil der Navigation ist. Sind Sie sicher?",
  no_parent_page_selected_warning_buttonText: "Erstellen",
  editor_folder_could_not_create_message: "Der Ordner für die Inhalte konnte nicht angelegt werden.",
  page_folder_could_not_create_message: "Der Ordner für die Seite konnte nicht angelegt werden.",
  name_label: "Name",
  name_text: "Ein neuer Ordner mit diesem Namen wird in den angegebenen Basisordnern erstellt.",
  name_empty_text: "Geben Sie einen Inhaltsnamen an.",
  template_chooser_empty_text: "Bitte wählen Sie ein Template aus.",
  parent_label: "Übergeordnete Seite",
  dialog_title: "{0} anlegen",
  quick_create_tooltip: "Neuen Inhalt anlegen",
});
