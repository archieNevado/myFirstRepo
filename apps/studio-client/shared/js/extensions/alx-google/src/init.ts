import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
import AlxGoogleDocTypes_properties from "./AlxGoogleDocTypes_properties";

contentTypeLocalizationRegistry.addLocalization("CMChannel", {
  properties: {
    localSettings: {
      properties: {
        googleAnalytics: {
          properties: {
            disabled: { displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_disabled_displayName },
            disabled_true: { displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_disabled_true_displayName },
            measurementId: {
              displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_measurementId_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_measurementId_emptyText,
            },
            homeUrl: {
              displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_homeUrl_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_homeUrl_emptyText,
            },
            pageReport: {
              displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_pageReport_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_pageReport_emptyText,
            },
            propertyId: {
              displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_propertyId_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_propertyId_emptyText,
            },
            authFile: { displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_authFile_displayName },
            limit: {
              displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_limit_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_limit_emptyText,
            },
            interval: {
              displayName: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_interval_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMChannel_localSettings_googleAnalytics_interval_emptyText,
            },
          },
        },
      },
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMALXBaseList", {
  properties: {
    localSettings: {
      properties: {
        googleAnalytics: {
          properties: {
            authFile: { displayName: AlxGoogleDocTypes_properties.CMALXBaseList_localSettings_googleAnalytics_authFile_displayName },
            propertyId: {
              displayName: AlxGoogleDocTypes_properties.CMALXBaseList_localSettings_googleAnalytics_propertyId_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMALXBaseList_localSettings_googleAnalytics_propertyId_emptyText,
            },
            limit: {
              displayName: AlxGoogleDocTypes_properties.CMALXBaseList_localSettings_googleAnalytics_limit_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMALXBaseList_localSettings_googleAnalytics_limit_emptyText,
            },
            interval: {
              displayName: AlxGoogleDocTypes_properties.CMALXBaseList_localSettings_googleAnalytics_interval_displayName,
              emptyText: AlxGoogleDocTypes_properties.CMALXBaseList_localSettings_googleAnalytics_interval_emptyText,
            },
          },
        },
      },
    },
  },
});
