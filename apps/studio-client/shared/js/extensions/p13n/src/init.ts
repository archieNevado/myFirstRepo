import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
import P13nDocTypes_properties from "./P13nDocTypes_properties";
import typePersonalizedContent from "./icons/type-personalized-content.svg";
import userPersonalization from "./icons/user-personalization.svg";
import usersPersonalization from "./icons/users-personalization.svg";

contentTypeLocalizationRegistry.addLocalization("CMSelectionRules", {
  displayName: P13nDocTypes_properties.CMSelectionRules_displayName,
  description: P13nDocTypes_properties.CMSelectionRules_description,
  svgIcon: typePersonalizedContent,
  properties: {
    title: {
      displayName: P13nDocTypes_properties.CMSelectionRules_title_displayName,
      description: P13nDocTypes_properties.CMSelectionRules_title_description,
      emptyText: P13nDocTypes_properties.CMSelectionRules_title_emptyText,
    },
    text: {
      displayName: P13nDocTypes_properties.CMSelectionRules_text_displayName,
      description: P13nDocTypes_properties.CMSelectionRules_text_description,
    },
    rules: {
      displayName: P13nDocTypes_properties.CMSelectionRules_rules_displayName,
      description: P13nDocTypes_properties.CMSelectionRules_rules_description,
    },
    defaultContent: {
      displayName: P13nDocTypes_properties.CMSelectionRules_defaultContent_displayName,
      description: P13nDocTypes_properties.CMSelectionRules_defaultContent_description,
      emptyText: P13nDocTypes_properties.CMSelectionRules_defaultContent_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMSegment", {
  displayName: P13nDocTypes_properties.CMSegment_displayName,
  description: P13nDocTypes_properties.CMSegment_description,
  svgIcon: usersPersonalization,
  properties: {
    description: {
      displayName: P13nDocTypes_properties.CMSegment_description_displayName,
      description: P13nDocTypes_properties.CMSegment_description_description,
      emptyText: P13nDocTypes_properties.CMSegment_description_emptyText,
    },
    conditions: {
      displayName: P13nDocTypes_properties.CMSegment_conditions_displayName,
      description: P13nDocTypes_properties.CMSegment_conditions_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMUserProfile", {
  displayName: P13nDocTypes_properties.CMUserProfile_displayName,
  description: P13nDocTypes_properties.CMUserProfile_description,
  svgIcon: userPersonalization,
  properties: {
    profileSettings: {
      displayName: P13nDocTypes_properties.CMUserProfile_profileSettings_displayName,
      description: P13nDocTypes_properties.CMUserProfile_profileSettings_description,
      emptyText: P13nDocTypes_properties.CMUserProfile_profileSettings_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMP13NSearch", {
  displayName: P13nDocTypes_properties.CMP13NSearch_displayName,
  description: P13nDocTypes_properties.CMP13NSearch_description,
  svgIcon: typePersonalizedContent,
  properties: {
    documentType: {
      displayName: P13nDocTypes_properties.CMP13NSearch_documentType_displayName,
      description: P13nDocTypes_properties.CMP13NSearch_documentType_description,
      emptyText: P13nDocTypes_properties.CMP13NSearch_documentType_emptyText,
    },
    searchQuery: {
      displayName: P13nDocTypes_properties.CMP13NSearch_searchQuery_displayName,
      description: P13nDocTypes_properties.CMP13NSearch_searchQuery_description,
      emptyText: P13nDocTypes_properties.CMP13NSearch_searchQuery_emptyText,
    },
    maxLength: {
      displayName: P13nDocTypes_properties.CMP13NSearch_maxLength_displayName,
      description: P13nDocTypes_properties.CMP13NSearch_maxLength_description,
      emptyText: P13nDocTypes_properties.CMP13NSearch_maxLength_emptyText,
    },
    defaultContent: {
      displayName: P13nDocTypes_properties.CMP13NSearch_defaultContent_displayName,
      emptyText: P13nDocTypes_properties.CMP13NSearch_defaultContent_emptyText,
    },
    searchContext: {
      displayName: P13nDocTypes_properties.CMP13NSearch_searchContext_displayName,
      emptyText: P13nDocTypes_properties.CMP13NSearch_searchContext_emptyText,
    },
  },
});