import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import BlueprintProcessDefinitions_properties from "./BlueprintProcessDefinitions_properties";

/**
 * Overrides of ResourceBundle "BlueprintProcessDefinitions" for Locale "de".
 * @see BlueprintProcessDefinitions_properties#INSTANCE
 */
ResourceBundleUtil.override(BlueprintProcessDefinitions_properties, {
  Translation_text: "Übersetzung",
  Translation_state_Translate_text: "Übersetzungs-Workflow annehmen",
  Translation_state_TranslateSelf_text: "Übersetzungs-Workflow starten",
  Translation_state_sendToTranslationService_text: "An Übersetzungsdienst senden",
  Translation_state_rollbackTranslation_text: "Änderungen verwerfen",
  Translation_state_finishTranslation_text: "Lokalisierung abschließen",
  Translation_state_Review_text: "Übersetzung prüfen",
  Translation_state_translationReviewed_text: "Lokalisierung abschließen (Übersetzung geprüft)",
  Translation_task_Translate_text: "Übersetzen",
  Translation_task_TranslateSelf_text: "Übersetzen",
  Translation_task_Review_text: "Prüfen",
  Synchronization_text: "Synchronisation",
  Synchronization_state_Synchronize_text: "Synchronisieren",
  Synchronization_state_finishSynchronization_text: "Synchronisation abgeschlossen",
});
