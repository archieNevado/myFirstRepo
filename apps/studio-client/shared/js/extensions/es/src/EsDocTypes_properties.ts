interface EsDocTypes_properties {
  CMMail_displayName: string;
  CMMail_from_displayName: string;
  CMMail_from_emptyText: string;
  CMMail_subject_displayName: string;
  CMMail_subject_emptyText: string;
  CMMail_text_displayName: string;
  CMMail_text_emptyText: string;
  CMMail_contentType_displayName: string;
  CMMail_contentType_emptyText: string;
  ESDynamicList_displayName: string;
  ESDynamicList_interval_displayName: string;
  ESDynamicList_aggregationType_displayName: string;
  ESDynamicList_channel_displayName: string;
}

const EsDocTypes_properties: EsDocTypes_properties = {
  CMMail_displayName: "Email Template",
  CMMail_from_displayName: "From",
  CMMail_from_emptyText: "Enter the email address of the sender here.",
  CMMail_subject_displayName: "Subject",
  CMMail_subject_emptyText: "Enter the subject of the email here.",
  CMMail_text_displayName: "Email Text",
  CMMail_text_emptyText: " ",
  CMMail_contentType_displayName: "Content-Type (text/plain, text/html)",
  CMMail_contentType_emptyText: "text/plain, text/html",
  ESDynamicList_displayName: "Dynamic Elastic Social List",
  ESDynamicList_interval_displayName: "Interval",
  ESDynamicList_aggregationType_displayName: "List",
  ESDynamicList_channel_displayName: "Context",
};

export default EsDocTypes_properties;
