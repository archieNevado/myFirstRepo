import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "PersonalizationDocTypes".
 * @see PersonalizationDocTypes_properties#INSTANCE
 */
interface PersonalizationDocTypes_properties {

  CMSelectionRules_text: string;
  CMSelectionRules_toolTip: string;
  CMSelectionRules_title_text: string;
  CMSelectionRules_title_emptyText: string;
  CMSelectionRules_title_toolTip: string;
  CMSelectionRules_text_text: string;
  CMSelectionRules_text_toolTip: string;
  CMSelectionRules_rules_text: string;
  CMSelectionRules_rules_toolTip: string;
  CMSelectionRules_defaultContent_text: string;
  CMSelectionRules_defaultContent_emptyText: string;
  CMSelectionRules_defaultContent_toolTip: string;
  CMSelectionRules_favlabel: string;
  CMSegment_text: string;
  CMSegment_toolTip: string;
  CMSegment_description_text: string;
  CMSegment_description_emptyText: string;
  CMSegment_description_toolTip: string;
  CMSegment_conditions_text: string;
  CMSegment_conditions_toolTip: string;
  CMUserProfile_text: string;
  CMUserProfile_toolTip: string;
  CMUserProfile_profileSettings_text: string;
  CMUserProfile_profileSettings_emptyText: string;
  CMUserProfile_profileSettings_toolTip: string;
  CMUserProfile_favlabel: string;
  CMP13NSearch_text: string;
  CMP13NSearch_toolTip: string;
  CMP13NSearch_documentType_text: string;
  CMP13NSearch_documentType_toolTip: string;
  CMP13NSearch_documentType_emptyText: string;
  CMP13NSearch_searchQuery_text: string;
  CMP13NSearch_searchQuery_toolTip: string;
  CMP13NSearch_searchQuery_emptyText: string;
  CMP13NSearch_maxLength_text: string;
  CMP13NSearch_maxLength_toolTip: string;
  CMP13NSearch_maxLength_emptyText: string;
  CMP13NSearch_defaultContent_text: string;
  CMP13NSearch_defaultContent_emptyText: string;
  CMP13NSearch_searchContext_text: string;
  CMP13NSearch_searchContext_emptyText: string;
  CMSelectionRules_icon: string;
  CMStream_icon: string;
  CMSegment_icon: string;
  CMUserProfile_icon: string;
  CMP13NSearch_icon: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "PersonalizationDocTypes".
 * @see PersonalizationDocTypes_properties
 */
const PersonalizationDocTypes_properties: PersonalizationDocTypes_properties = {
  CMSelectionRules_text: "Personalized Content",
  CMSelectionRules_toolTip: "A list of content items from which one is picked based on personalization conditions",
  CMSelectionRules_title_text: "Title",
  CMSelectionRules_title_emptyText: "Enter title here.",
  CMSelectionRules_title_toolTip: "The title for the personalized content",
  CMSelectionRules_text_text: "Descriptive Text",
  CMSelectionRules_text_toolTip: "Descriptive Text",
  CMSelectionRules_rules_text: "Rules",
  CMSelectionRules_rules_toolTip: "A list of content items that is evaluated against the corresponding conditions. The content item of the first matching condition is used.",
  CMSelectionRules_defaultContent_text: "Default Content",
  CMSelectionRules_defaultContent_emptyText: "Add default content by dragging it from the library here.",
  CMSelectionRules_defaultContent_toolTip: "The default content is used if no condition/rule matches or the rules contain errors",
  CMSelectionRules_favlabel: "Personalized Content",
  CMSegment_text: "Customer Segment",
  CMSegment_toolTip: "A Segment defines conditions for grouping website customers into named segments",
  CMSegment_description_text: "Segment Description",
  CMSegment_description_emptyText: "Enter a description for editorial use.",
  CMSegment_description_toolTip: "A description that explains the purpose of the given segment",
  CMSegment_conditions_text: "Segment Conditions",
  CMSegment_conditions_toolTip: "The conditions that must be matched by a customer profile to be part of this segment",
  CMUserProfile_text: "Customer Persona",
  CMUserProfile_toolTip: "Customer Personas can be used to simulate personalization behavior with predefined profile settings of virtual website users.",
  CMUserProfile_profileSettings_text: "Context Data",
  CMUserProfile_profileSettings_emptyText: "Enter your context data here. Example: interest.sports=true.",
  CMUserProfile_profileSettings_toolTip: "A Key/Value list of context data to be used in this persona context",
  CMUserProfile_favlabel: "Customer Persona",
  CMP13NSearch_text: "Personalized Search",
  CMP13NSearch_toolTip: "Personalized searches are used to enhance search queries with context information and/or other extensions.",
  CMP13NSearch_documentType_text: "Content Type",
  CMP13NSearch_documentType_toolTip: "Provide a content type",
  CMP13NSearch_documentType_emptyText: "Enter a content type.",
  CMP13NSearch_searchQuery_text: "Search Query",
  CMP13NSearch_searchQuery_toolTip: "Enter a personalized search query",
  CMP13NSearch_searchQuery_emptyText: "Enter a personalized search query, e.g. 'name:Offer* AND userKeywords(limit:-1, field:keywords, threshold:0.6, context:myContext)'",
  CMP13NSearch_maxLength_text: "Maximum Number of Results",
  CMP13NSearch_maxLength_toolTip: "Enter the maximum number of desired results",
  CMP13NSearch_maxLength_emptyText: "Maximum Number of Results",
  CMP13NSearch_defaultContent_text: "Default Content",
  CMP13NSearch_defaultContent_emptyText: "Add default content by dragging it from the library here.",
  CMP13NSearch_searchContext_text: "Search Starting from the Site Level",
  CMP13NSearch_searchContext_emptyText: "Add one or more navigation context items.",
  CMSelectionRules_icon: CoreIcons_properties.type_personalized_content,
  CMStream_icon: CoreIcons_properties.type_personalized_content,
  CMSegment_icon: CoreIcons_properties.users_personalization,
  CMUserProfile_icon: CoreIcons_properties.user_personalization,
  CMP13NSearch_icon: CoreIcons_properties.type_personalized_content,
};

export default PersonalizationDocTypes_properties;
