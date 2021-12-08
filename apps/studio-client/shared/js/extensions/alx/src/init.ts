import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
import AlxDocTypes_properties from "./AlxDocTypes_properties";

contentTypeLocalizationRegistry.addLocalization("CMALXBaseList", {
  displayName: AlxDocTypes_properties.CMALXBaseList_displayName,
  properties: {
    maxLength: {
      displayName: AlxDocTypes_properties.CMALXBaseList_maxLength_displayName,
      emptyText: AlxDocTypes_properties.CMALXBaseList_maxLength_emptyText,
    },
    timeRange: {
      displayName: AlxDocTypes_properties.CMALXBaseList_timeRange_displayName,
      emptyText: AlxDocTypes_properties.CMALXBaseList_timeRange_emptyText,
    },
    analyticsProvider: {
      displayName: AlxDocTypes_properties.CMALXBaseList_analyticsProvider_displayName,
      emptyText: AlxDocTypes_properties.CMALXBaseList_analyticsProvider_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMALXPageList", {
  displayName: AlxDocTypes_properties.CMALXPageList_displayName,
  properties: {
    documentType: {
      displayName: AlxDocTypes_properties.CMALXPageList_documentType_displayName,
      emptyText: AlxDocTypes_properties.CMALXPageList_documentType_emptyText,
    },
    baseChannel: {
      displayName: AlxDocTypes_properties.CMALXPageList_baseChannel_displayName,
      emptyText: AlxDocTypes_properties.CMALXPageList_baseChannel_emptyText,
    },
    defaultContent: {
      displayName: AlxDocTypes_properties.CMALXPageList_defaultContent_displayName,
      emptyText: AlxDocTypes_properties.CMALXPageList_defaultContent_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMALXEventList", {
  displayName: AlxDocTypes_properties.CMALXEventList_displayName,
  properties: {
    category: {
      displayName: AlxDocTypes_properties.CMALXEventList_category_displayName,
      emptyText: AlxDocTypes_properties.CMALXEventList_category_emptyText,
    },
    action: {
      displayName: AlxDocTypes_properties.CMALXEventList_action_displayName,
      emptyText: AlxDocTypes_properties.CMALXEventList_action_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMChannel", {
  properties: {
    localSettings: {
      properties: {
        analyticsProvider: {
          displayName: AlxDocTypes_properties.CMChannel_localSettings_analyticsProvider_displayName,
          emptyText: AlxDocTypes_properties.CMChannel_localSettings_analyticsProvider_emptyText,
        },
      },
    },
  },
});
