
/**
 * Interface values for ResourceBundle "BlueprintProcessDefinitions".
 * @see BlueprintProcessDefinitions_properties#INSTANCE
 */
interface BlueprintProcessDefinitions_properties {

  Translation_text: string;
  Translation_state_Translate_text: string;
  Translation_state_TranslateSelf_text: string;
  Translation_state_sendToTranslationService_text: string;
  Translation_state_rollbackTranslation_text: string;
  Translation_state_finishTranslation_text: string;
  Translation_state_Review_text: string;
  Translation_state_translationReviewed_text: string;
  Translation_task_Translate_text: string;
  Translation_task_TranslateSelf_text: string;
  Translation_task_Review_text: string;
  Synchronization_text: string;
  Synchronization_state_Synchronize_text: string;
  Synchronization_state_finishSynchronization_text: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "BlueprintProcessDefinitions".
 * @see BlueprintProcessDefinitions_properties
 */
const BlueprintProcessDefinitions_properties: BlueprintProcessDefinitions_properties = {
  Translation_text: "Translation",
  Translation_state_Translate_text: "Accept Translation Workflow",
  Translation_state_TranslateSelf_text: "Start Translation Workflow",
  Translation_state_sendToTranslationService_text: "Send to Translation Service",
  Translation_state_rollbackTranslation_text: "Reject Changes",
  Translation_state_finishTranslation_text: "Finish Content Localization",
  Translation_state_Review_text: "Review Translation",
  Translation_state_translationReviewed_text: "Finish Content Localization (Translation Reviewed)",
  Translation_task_Translate_text: "Translate",
  Translation_task_TranslateSelf_text: "Translate",
  Translation_task_Review_text: "Review",
  Synchronization_text: "Synchronization",
  Synchronization_state_Synchronize_text: "Synchronize",
  Synchronization_state_finishSynchronization_text: "Synchronization Finished",
};

export default BlueprintProcessDefinitions_properties;
