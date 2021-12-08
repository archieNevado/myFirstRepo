import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
import EsDocTypes_properties from "./EsDocTypes_properties";

contentTypeLocalizationRegistry.addLocalization("CMMail", {
  displayName: EsDocTypes_properties.CMMail_displayName,
  properties: {
    from: {
      displayName: EsDocTypes_properties.CMMail_from_displayName,
      emptyText: EsDocTypes_properties.CMMail_from_emptyText,
    },
    subject: {
      displayName: EsDocTypes_properties.CMMail_subject_displayName,
      emptyText: EsDocTypes_properties.CMMail_subject_emptyText,
    },
    text: {
      displayName: EsDocTypes_properties.CMMail_text_displayName,
      emptyText: EsDocTypes_properties.CMMail_text_emptyText,
    },
    contentType: {
      displayName: EsDocTypes_properties.CMMail_contentType_displayName,
      emptyText: EsDocTypes_properties.CMMail_contentType_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("ESDynamicList", {
  displayName: EsDocTypes_properties.ESDynamicList_displayName,
  properties: {
    interval: { displayName: EsDocTypes_properties.ESDynamicList_interval_displayName },
    aggregationType: { displayName: EsDocTypes_properties.ESDynamicList_aggregationType_displayName },
    channel: { displayName: EsDocTypes_properties.ESDynamicList_channel_displayName },
  },
});
