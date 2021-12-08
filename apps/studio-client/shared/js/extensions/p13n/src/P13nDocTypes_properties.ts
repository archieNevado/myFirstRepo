interface P13nDocTypes_properties {
  CMSelectionRules_displayName: string;
  CMSelectionRules_description: string;
  CMSelectionRules_title_displayName: string;
  CMSelectionRules_title_description: string;
  CMSelectionRules_title_emptyText: string;
  CMSelectionRules_text_displayName: string;
  CMSelectionRules_text_description: string;
  CMSelectionRules_rules_displayName: string;
  CMSelectionRules_rules_description: string;
  CMSelectionRules_defaultContent_displayName: string;
  CMSelectionRules_defaultContent_description: string;
  CMSelectionRules_defaultContent_emptyText: string;
  CMSegment_displayName: string;
  CMSegment_description: string;
  CMSegment_description_displayName: string;
  CMSegment_description_description: string;
  CMSegment_description_emptyText: string;
  CMSegment_conditions_displayName: string;
  CMSegment_conditions_description: string;
  CMUserProfile_displayName: string;
  CMUserProfile_description: string;
  CMUserProfile_profileSettings_displayName: string;
  CMUserProfile_profileSettings_description: string;
  CMUserProfile_profileSettings_emptyText: string;
  CMP13NSearch_displayName: string;
  CMP13NSearch_description: string;
  CMP13NSearch_documentType_displayName: string;
  CMP13NSearch_documentType_description: string;
  CMP13NSearch_documentType_emptyText: string;
  CMP13NSearch_searchQuery_displayName: string;
  CMP13NSearch_searchQuery_description: string;
  CMP13NSearch_searchQuery_emptyText: string;
  CMP13NSearch_maxLength_displayName: string;
  CMP13NSearch_maxLength_description: string;
  CMP13NSearch_maxLength_emptyText: string;
  CMP13NSearch_defaultContent_displayName: string;
  CMP13NSearch_defaultContent_emptyText: string;
  CMP13NSearch_searchContext_displayName: string;
  CMP13NSearch_searchContext_emptyText: string;
}

const P13nDocTypes_properties: P13nDocTypes_properties = {
  CMSelectionRules_displayName: "Personalized Content",
  CMSelectionRules_description: "A list of content items from which one is picked based on personalization conditions",
  CMSelectionRules_title_displayName: "Title",
  CMSelectionRules_title_description: "The title for the personalized content",
  CMSelectionRules_title_emptyText: "Enter title here.",
  CMSelectionRules_text_displayName: "Descriptive Text",
  CMSelectionRules_text_description: "Descriptive Text",
  CMSelectionRules_rules_displayName: "Rules",
  CMSelectionRules_rules_description: "A list of content items that is evaluated against the corresponding conditions. The content item of the first matching condition is used.",
  CMSelectionRules_defaultContent_displayName: "Default Content",
  CMSelectionRules_defaultContent_description: "The default content is used if no condition/rule matches or the rules contain errors",
  CMSelectionRules_defaultContent_emptyText: "Add default content by dragging it from the library here.",
  CMSegment_displayName: "Customer Segment",
  CMSegment_description: "A Segment defines conditions for grouping website customers into named segments",
  CMSegment_description_displayName: "Segment Description",
  CMSegment_description_description: "A description that explains the purpose of the given segment",
  CMSegment_description_emptyText: "Enter a description for editorial use.",
  CMSegment_conditions_displayName: "Segment Conditions",
  CMSegment_conditions_description: "The conditions that must be matched by a customer profile to be part of this segment",
  CMUserProfile_displayName: "Customer Persona",
  CMUserProfile_description: "Customer Personas can be used to simulate personalization behavior with predefined profile settings of virtual website users.",
  CMUserProfile_profileSettings_displayName: "Context Data",
  CMUserProfile_profileSettings_description: "A Key/Value list of context data to be used in this persona context",
  CMUserProfile_profileSettings_emptyText: "Enter your context data here. Example: interest.sports=true.",
  CMP13NSearch_displayName: "Personalized Search",
  CMP13NSearch_description: "Personalized searches are used to enhance search queries with context information and/or other extensions.",
  CMP13NSearch_documentType_displayName: "Content Type",
  CMP13NSearch_documentType_description: "Provide a content type",
  CMP13NSearch_documentType_emptyText: "Enter a content type.",
  CMP13NSearch_searchQuery_displayName: "Search Query",
  CMP13NSearch_searchQuery_description: "Enter a personalized search query",
  CMP13NSearch_searchQuery_emptyText: "Enter a personalized search query, e.g. 'name:Offer* AND userKeywords(limit:-1, field:keywords, threshold:0.6, context:myContext)'",
  CMP13NSearch_maxLength_displayName: "Maximum Number of Results",
  CMP13NSearch_maxLength_description: "Enter the maximum number of desired results",
  CMP13NSearch_maxLength_emptyText: "Maximum Number of Results",
  CMP13NSearch_defaultContent_displayName: "Default Content",
  CMP13NSearch_defaultContent_emptyText: "Add default content by dragging it from the library here.",
  CMP13NSearch_searchContext_displayName: "Search Starting from the Site Level",
  CMP13NSearch_searchContext_emptyText: "Add one or more navigation context items.",
};

export default P13nDocTypes_properties;
