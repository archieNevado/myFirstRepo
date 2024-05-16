interface AlxDocTypes_properties {
  CMALXBaseList_displayName: string;
  CMALXBaseList_maxLength_displayName: string;
  CMALXBaseList_maxLength_emptyText: string;
  CMALXBaseList_timeRange_displayName: string;
  CMALXBaseList_timeRange_emptyText: string;
  CMALXBaseList_analyticsProvider_displayName: string;
  CMALXBaseList_analyticsProvider_emptyText: string;
  CMALXPageList_displayName: string;
  CMALXPageList_documentType_displayName: string;
  CMALXPageList_documentType_emptyText: string;
  CMALXPageList_baseChannel_displayName: string;
  CMALXPageList_baseChannel_emptyText: string;
  CMALXPageList_defaultContent_displayName: string;
  CMALXPageList_defaultContent_emptyText: string;
  CMALXEventList_displayName: string;
  CMALXEventList_category_displayName: string;
  CMALXEventList_category_emptyText: string;
  CMALXEventList_action_displayName: string;
  CMALXEventList_action_emptyText: string;
  CMChannel_localSettings_analyticsProvider_displayName: string;
  CMChannel_localSettings_analyticsProvider_emptyText: string;
}

const AlxDocTypes_properties: AlxDocTypes_properties = {
  CMALXBaseList_displayName: "Analytics Base List",
  CMALXBaseList_maxLength_displayName: "Max Length",
  CMALXBaseList_maxLength_emptyText: "Enter the maximum length of the Analytics Page List.",
  CMALXBaseList_timeRange_displayName: "Time Range",
  CMALXBaseList_timeRange_emptyText: "Enter the time range of the data to include (in days before today).",
  CMALXBaseList_analyticsProvider_displayName: "ID of the Analytics Provider",
  CMALXBaseList_analyticsProvider_emptyText: "Enter the ID of the analytics provider.",
  CMALXPageList_displayName: "Analytics Page List",
  CMALXPageList_documentType_displayName: "Content Type",
  CMALXPageList_documentType_emptyText: "Desired content type.",
  CMALXPageList_baseChannel_displayName: "Base Channel",
  CMALXPageList_baseChannel_emptyText: "Enter the base channel under which the desired content items are located.",
  CMALXPageList_defaultContent_displayName: "Default Content",
  CMALXPageList_defaultContent_emptyText: "Add default content by dragging it from the library here.",
  CMALXEventList_displayName: "Analytics Event List",
  CMALXEventList_category_displayName: "Event Category",
  CMALXEventList_category_emptyText: "Enter the name supplied for a group of objects to track (e.g. 'Videos')",
  CMALXEventList_action_displayName: "Event Action",
  CMALXEventList_action_emptyText: "Enter the type of event or interaction to track (e.g. 'Play pressed')",
  CMChannel_localSettings_analyticsProvider_displayName: "Name of the Default Analytics Provider",
  CMChannel_localSettings_analyticsProvider_emptyText: "Enter the ID of the default analytics provider.",
};

export default AlxDocTypes_properties;
