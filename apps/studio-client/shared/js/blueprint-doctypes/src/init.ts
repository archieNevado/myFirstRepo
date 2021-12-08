import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
import BlueprintDoctypesDocTypes_properties from "./BlueprintDoctypesDocTypes_properties";
import dictionary from "./icons/dictionary.svg";
import tag from "./icons/tag.svg";
import type360View from "./icons/type-360-view.svg";
import typeArticle from "./icons/type-article.svg";
import typeCollection from "./icons/type-collection.svg";
import typeCss from "./icons/type-css.svg";
import typeDownload from "./icons/type-download.svg";
import typeDynamicList from "./icons/type-dynamic-list.svg";
import typeExternalLink from "./icons/type-external-link.svg";
import typeHtmlFragment from "./icons/type-html-fragment.svg";
import typeImageGallery from "./icons/type-image-gallery.svg";
import typeImageMap from "./icons/type-image-map.svg";
import typeInteractive from "./icons/type-interactive.svg";
import typeAudio from "./icons/type-audio.svg";
import typeJavascript from "./icons/type-javascript.svg";
import typeMedia from "./icons/type-media.svg";
import typeObject from "./icons/type-object.svg";
import typePage from "./icons/type-page.svg";
import typePicture from "./icons/type-picture.svg";
import typeQuery from "./icons/type-query.svg";
import typeResourceBundle from "./icons/type-resource-bundle.svg";
import typeSettings from "./icons/type-settings.svg";
import typeSiteIndicator from "./icons/type-site-indicator.svg";
import typeTheming from "./icons/type-theming.svg";
import typeVideo from "./icons/type-video.svg";
import typeViewtype from "./icons/type-viewtype.svg";
import typeSitemap from "./icons/type-sitemap.svg";
import typeSymbol from "./icons/type-symbol.svg";
import typeTeaser from "./icons/type-teaser.svg";
import user from "./icons/user.svg";

contentTypeLocalizationRegistry.addLocalization("CMAbstractCode", {
  displayName: BlueprintDoctypesDocTypes_properties.CMAbstractCode_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMAbstractCode_description,
  svgIcon: typeObject,
  properties: {
    code: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAbstractCode_code_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMAbstractCode_code_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMAbstractCode_code_emptyText,
    },
    dataUrl: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAbstractCode_dataUrl_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMAbstractCode_dataUrl_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMAbstractCode_dataUrl_emptyText,
    },
    description: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAbstractCode_description_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMAbstractCode_description_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMAbstractCode_description_emptyText,
    },
    ieExpression: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAbstractCode_ieExpression_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMAbstractCode_ieExpression_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMAbstractCode_ieExpression_emptyText,
    },
    ieRevealed: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAbstractCode_ieRevealed_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMAbstractCode_ieRevealed_description,
    },
    include: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAbstractCode_include_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMAbstractCode_include_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMJavaScript", {
  displayName: BlueprintDoctypesDocTypes_properties.CMJavaScript_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMJavaScript_description,
  svgIcon: typeJavascript,
  properties: {
    inHead: {
      displayName: BlueprintDoctypesDocTypes_properties.CMJavaScript_inHead_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMJavaScript_inHead_description,
    },
    code: {
      displayName: BlueprintDoctypesDocTypes_properties.CMJavaScript_code_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMJavaScript_code_description,
    },
    include: {
      displayName: BlueprintDoctypesDocTypes_properties.CMJavaScript_include_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMJavaScript_include_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMAction", {
  displayName: BlueprintDoctypesDocTypes_properties.CMAction_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMAction_description,
  svgIcon: typeObject,
  properties: {
    id: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAction_id_displayName,
      emptyText: BlueprintDoctypesDocTypes_properties.CMAction_id_emptyText,
    },
    type: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAction_type_displayName,
      emptyText: BlueprintDoctypesDocTypes_properties.CMAction_type_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMArticle", {
  displayName: BlueprintDoctypesDocTypes_properties.CMArticle_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMArticle_description,
  svgIcon: typeArticle,
  properties: {
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMArticle_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMArticle_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMArticle_title_emptyText,
    },
    detailText: {
      displayName: BlueprintDoctypesDocTypes_properties.CMArticle_detailText_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMArticle_detailText_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMArticle_detailText_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMMedia", {
  displayName: BlueprintDoctypesDocTypes_properties.CMMedia_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMMedia_description,
  svgIcon: typeMedia,
  properties: {
    localSettings: {
      properties: {
        playerSettings: {
          properties: {
            muted: { displayName: BlueprintDoctypesDocTypes_properties.CMMedia_localSettings_playerSettings_muted_displayName },
            loop: { displayName: BlueprintDoctypesDocTypes_properties.CMMedia_localSettings_playerSettings_loop_displayName },
            autoplay: { displayName: BlueprintDoctypesDocTypes_properties.CMMedia_localSettings_playerSettings_autoplay_displayName },
            hideControls: { displayName: BlueprintDoctypesDocTypes_properties.CMMedia_localSettings_playerSettings_hideControls_displayName },
          },
        },
      },
    },
    alt: {
      displayName: BlueprintDoctypesDocTypes_properties.CMMedia_alt_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMMedia_alt_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMMedia_alt_emptyText,
    },
    caption: {
      displayName: BlueprintDoctypesDocTypes_properties.CMMedia_caption_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMMedia_caption_description,
    },
    copyright: {
      displayName: BlueprintDoctypesDocTypes_properties.CMMedia_copyright_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMMedia_copyright_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMMedia_copyright_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMAudio", {
  displayName: BlueprintDoctypesDocTypes_properties.CMAudio_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMAudio_description,
  svgIcon: typeAudio,
  properties: {
    data: { displayName: BlueprintDoctypesDocTypes_properties.CMAudio_data_displayName },
    dataUrl: {
      displayName: BlueprintDoctypesDocTypes_properties.CMAudio_dataUrl_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMAudio_dataUrl_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMAudio_dataUrl_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMChannel", {
  displayName: BlueprintDoctypesDocTypes_properties.CMChannel_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMChannel_description,
  svgIcon: typePage,
  properties: {
    footer: {
      displayName: BlueprintDoctypesDocTypes_properties.CMChannel_footer_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMChannel_footer_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMChannel_footer_emptyText,
    },
    header: {
      displayName: BlueprintDoctypesDocTypes_properties.CMChannel_header_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMChannel_header_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMChannel_header_emptyText,
    },
    picture: {
      displayName: BlueprintDoctypesDocTypes_properties.CMChannel_picture_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMChannel_picture_description,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMChannel_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMChannel_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMChannel_title_emptyText,
    },
    placement: {
      displayName: BlueprintDoctypesDocTypes_properties.CMChannel_placement_displayName,
      properties: { placements_2: { properties: { layout: { displayName: BlueprintDoctypesDocTypes_properties.CMChannel_placement_placements_2_layout_displayName } } } },
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMCollection", {
  displayName: BlueprintDoctypesDocTypes_properties.CMCollection_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMCollection_description,
  svgIcon: typeCollection,
  properties: {
    items: {
      displayName: BlueprintDoctypesDocTypes_properties.CMCollection_items_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMCollection_items_description,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMCollection_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMCollection_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMCollection_title_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMContext", {
  displayName: BlueprintDoctypesDocTypes_properties.CMContext_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMContext_description,
  svgIcon: typeObject,
});

contentTypeLocalizationRegistry.addLocalization("CMCSS", {
  displayName: BlueprintDoctypesDocTypes_properties.CMCSS_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMCSS_description,
  svgIcon: typeCss,
  properties: {
    code: {
      displayName: BlueprintDoctypesDocTypes_properties.CMCSS_code_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMCSS_code_description,
    },
    ieRevealed: {
      displayName: BlueprintDoctypesDocTypes_properties.CMCSS_ieRevealed_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMCSS_ieRevealed_description,
    },
    media: {
      displayName: BlueprintDoctypesDocTypes_properties.CMCSS_media_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMCSS_media_description,
    },
    include: {
      displayName: BlueprintDoctypesDocTypes_properties.CMCSS_include_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMCSS_include_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("Dictionary", {
  displayName: BlueprintDoctypesDocTypes_properties.Dictionary_displayName,
  description: BlueprintDoctypesDocTypes_properties.Dictionary_description,
  svgIcon: dictionary,
});

contentTypeLocalizationRegistry.addLocalization("CMDownload", {
  displayName: BlueprintDoctypesDocTypes_properties.CMDownload_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMDownload_description,
  svgIcon: typeDownload,
  properties: {
    data: {
      displayName: BlueprintDoctypesDocTypes_properties.CMDownload_data_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMDownload_data_description,
    },
    filename: {
      displayName: BlueprintDoctypesDocTypes_properties.CMDownload_filename_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMDownload_filename_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMDownload_filename_emptyText,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMDownload_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMDownload_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMDownload_title_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMDynamicList", {
  displayName: BlueprintDoctypesDocTypes_properties.CMDynamicList_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMDynamicList_description,
  svgIcon: typeDynamicList,
  properties: {
    maxLength: {
      displayName: BlueprintDoctypesDocTypes_properties.CMDynamicList_maxLength_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMDynamicList_maxLength_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMDynamicList_maxLength_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMExternalLink", {
  displayName: BlueprintDoctypesDocTypes_properties.CMExternalLink_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMExternalLink_description,
  svgIcon: typeExternalLink,
  properties: {
    url: {
      displayName: BlueprintDoctypesDocTypes_properties.CMExternalLink_url_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMExternalLink_url_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMExternalLink_url_emptyText,
    },
    openInNewTab: {
      displayName: BlueprintDoctypesDocTypes_properties.CMExternalLink_openInNewTab_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMExternalLink_openInNewTab_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMExternalProduct", { properties: { title: { displayName: BlueprintDoctypesDocTypes_properties.CMExternalProduct_title_displayName } } });

contentTypeLocalizationRegistry.addLocalization("CMFolderProperties", {
  displayName: BlueprintDoctypesDocTypes_properties.CMFolderProperties_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMFolderProperties_description,
  svgIcon: typeObject,
  properties: {
    contexts: {
      displayName: BlueprintDoctypesDocTypes_properties.CMFolderProperties_contexts_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMFolderProperties_contexts_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMFolderProperties_contexts_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMGallery", {
  displayName: BlueprintDoctypesDocTypes_properties.CMGallery_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMGallery_description,
  svgIcon: typeImageGallery,
  properties: {
    items: {
      displayName: BlueprintDoctypesDocTypes_properties.CMGallery_items_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMGallery_items_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMGallery_items_emptyText,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMGallery_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMGallery_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMGallery_title_emptyText,
    },
    detailText: { displayName: BlueprintDoctypesDocTypes_properties.CMGallery_detailText_displayName },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMSpinner", {
  displayName: BlueprintDoctypesDocTypes_properties.CMSpinner_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMSpinner_description,
  svgIcon: type360View,
  properties: {
    sequence: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSpinner_sequence_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSpinner_sequence_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSpinner_sequence_emptyText,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSpinner_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSpinner_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSpinner_title_emptyText,
    },
    detailText: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSpinner_detailText_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSpinner_detailText_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSpinner_detailText_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMHasContexts", {
  displayName: BlueprintDoctypesDocTypes_properties.CMHasContexts_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMHasContexts_description,
  svgIcon: typeObject,
  properties: {
    contexts: {
      displayName: BlueprintDoctypesDocTypes_properties.CMHasContexts_contexts_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMHasContexts_contexts_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMHasContexts_contexts_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMHTML", {
  displayName: BlueprintDoctypesDocTypes_properties.CMHTML_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMHTML_description,
  svgIcon: typeHtmlFragment,
  properties: {
    data: {
      displayName: BlueprintDoctypesDocTypes_properties.CMHTML_data_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMHTML_data_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMImage", {
  displayName: BlueprintDoctypesDocTypes_properties.CMImage_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMImage_description,
  svgIcon: typeObject,
  properties: {
    data: {
      displayName: BlueprintDoctypesDocTypes_properties.CMImage_data_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMImage_data_description,
    },
    description: {
      displayName: BlueprintDoctypesDocTypes_properties.CMImage_description_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMImage_description_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMImage_description_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMImageMap", {
  displayName: BlueprintDoctypesDocTypes_properties.CMImageMap_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMImageMap_description,
  svgIcon: typeImageMap,
  properties: {
    pictures: { displayName: BlueprintDoctypesDocTypes_properties.CMImageMap_pictures_displayName },
    localSettings: {
      properties: {
        "image-map": { displayName: BlueprintDoctypesDocTypes_properties["CMImageMap_localSettings_image-map_displayName"] },
        overlay: {
          displayName: BlueprintDoctypesDocTypes_properties.CMImageMap_localSettings_overlay_displayName,
          properties: {
            displayTitle: { displayName: BlueprintDoctypesDocTypes_properties.CMImageMap_localSettings_overlay_displayTitle_displayName },
            displayShortText: { displayName: BlueprintDoctypesDocTypes_properties.CMImageMap_localSettings_overlay_displayShortText_displayName },
            displayPicture: { displayName: BlueprintDoctypesDocTypes_properties.CMImageMap_localSettings_overlay_displayPicture_displayName },
          },
        },
      },
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMInteractive", {
  displayName: BlueprintDoctypesDocTypes_properties.CMInteractive_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMInteractive_description,
  svgIcon: typeInteractive,
  properties: {
    data: { displayName: BlueprintDoctypesDocTypes_properties.CMInteractive_data_displayName },
    dataUrl: {
      displayName: BlueprintDoctypesDocTypes_properties.CMInteractive_dataUrl_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMInteractive_dataUrl_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMInteractive_dataUrl_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMLinkable", {
  displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMLinkable_description,
  svgIcon: typeObject,
  properties: {
    keywords: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_keywords_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_keywords_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_keywords_emptyText,
    },
    linkedSettings: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_linkedSettings_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_linkedSettings_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_linkedSettings_emptyText,
    },
    localSettings: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_localSettings_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_localSettings_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_localSettings_emptyText,
    },
    locationTaxonomy: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_locationTaxonomy_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_locationTaxonomy_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_locationTaxonomy_emptyText,
    },
    segment: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_segment_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_segment_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_segment_emptyText,
    },
    subjectTaxonomy: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_subjectTaxonomy_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_subjectTaxonomy_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_subjectTaxonomy_emptyText,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_title_emptyText,
    },
    validFrom: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_validFrom_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_validFrom_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_validFrom_emptyText,
    },
    validTo: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_validTo_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_validTo_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_validTo_emptyText,
    },
    viewtype: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_viewtype_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_viewtype_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_viewtype_emptyText,
    },
    extDisplayedDate: { displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_extDisplayedDate_displayName },
    htmlTitle: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_htmlTitle_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_htmlTitle_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_htmlTitle_emptyText,
    },
    htmlDescription: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLinkable_htmlDescription_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLinkable_htmlDescription_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLinkable_htmlDescription_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMLocalized", {
  displayName: BlueprintDoctypesDocTypes_properties.CMLocalized_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMLocalized_description,
  svgIcon: typeObject,
  properties: {
    locale: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLocalized_locale_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLocalized_locale_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLocalized_locale_emptyText,
    },
    master: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLocalized_master_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLocalized_master_description,
    },
    masterVersion: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLocalized_masterVersion_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLocalized_masterVersion_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLocalized_masterVersion_emptyText,
    },
    ignoreUpdates: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLocalized_ignoreUpdates_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLocalized_ignoreUpdates_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMLocTaxonomy", {
  displayName: BlueprintDoctypesDocTypes_properties.CMLocTaxonomy_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMLocTaxonomy_description,
  svgIcon: tag,
  properties: {
    latitudeLongitude: { displayName: BlueprintDoctypesDocTypes_properties.CMLocTaxonomy_latitudeLongitude_displayName },
    postcode: {
      displayName: BlueprintDoctypesDocTypes_properties.CMLocTaxonomy_postcode_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMLocTaxonomy_postcode_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMLocTaxonomy_postcode_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMMail", {
  displayName: BlueprintDoctypesDocTypes_properties.CMMail_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMMail_description,
  svgIcon: typeObject,
});

contentTypeLocalizationRegistry.addLocalization("CMNavigation", {
  displayName: BlueprintDoctypesDocTypes_properties.CMNavigation_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMNavigation_description,
  svgIcon: typeObject,
  properties: {
    children: {
      displayName: BlueprintDoctypesDocTypes_properties.CMNavigation_children_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMNavigation_children_description,
    },
    css: {
      displayName: BlueprintDoctypesDocTypes_properties.CMNavigation_css_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMNavigation_css_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMNavigation_css_emptyText,
    },
    hidden: {
      displayName: BlueprintDoctypesDocTypes_properties.CMNavigation_hidden_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMNavigation_hidden_description,
    },
    hiddenInSitemap: {
      displayName: BlueprintDoctypesDocTypes_properties.CMNavigation_hiddenInSitemap_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMNavigation_hiddenInSitemap_description,
    },
    javaScript: {
      displayName: BlueprintDoctypesDocTypes_properties.CMNavigation_javaScript_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMNavigation_javaScript_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMNavigation_javaScript_emptyText,
    },
    theme: {
      displayName: BlueprintDoctypesDocTypes_properties.CMNavigation_theme_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMNavigation_theme_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMNavigation_theme_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMObject", {
  displayName: BlueprintDoctypesDocTypes_properties.CMObject_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMObject_description,
  svgIcon: typeObject,
});

contentTypeLocalizationRegistry.addLocalization("CMPerson", {
  displayName: BlueprintDoctypesDocTypes_properties.CMPerson_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMPerson_description,
  svgIcon: user,
  properties: {
    firstName: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPerson_firstName_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPerson_firstName_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPerson_firstName_emptyText,
    },
    lastName: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPerson_lastName_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPerson_lastName_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPerson_lastName_emptyText,
    },
    displayName: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPerson_displayName_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPerson_displayName_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPerson_displayName_emptyText,
    },
    eMail: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPerson_eMail_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPerson_eMail_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPerson_eMail_emptyText,
    },
    organization: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPerson_organization_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPerson_organization_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPerson_organization_emptyText,
    },
    jobTitle: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPerson_jobTitle_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPerson_jobTitle_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPerson_jobTitle_emptyText,
    },
    teaserText: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPerson_teaserText_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPerson_teaserText_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPerson_teaserText_emptyText,
    },
    detailText: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPerson_detailText_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPerson_detailText_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPerson_detailText_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMPicture", {
  displayName: BlueprintDoctypesDocTypes_properties.CMPicture_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMPicture_description,
  svgIcon: typePicture,
  properties: {
    localSettings: {
      properties: {
        disableCropping: {
          displayName: BlueprintDoctypesDocTypes_properties.CMPicture_localSettings_disableCropping_displayName,
          description: BlueprintDoctypesDocTypes_properties.CMPicture_localSettings_disableCropping_description,
        },
      },
    },
    data: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPicture_data_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPicture_data_description,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPicture_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPicture_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPicture_title_emptyText,
    },
    detailText: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPicture_detailText_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPicture_detailText_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPicture_detailText_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMPlaceholder", {
  displayName: BlueprintDoctypesDocTypes_properties.CMPlaceholder_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMPlaceholder_description,
  svgIcon: typeObject,
  properties: {
    id: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPlaceholder_id_displayName,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPlaceholder_id_emptyText,
    },
    viewtype: {
      displayName: BlueprintDoctypesDocTypes_properties.CMPlaceholder_viewtype_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMPlaceholder_viewtype_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMPlaceholder_viewtype_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("Query", {
  displayName: BlueprintDoctypesDocTypes_properties.Query_displayName,
  description: BlueprintDoctypesDocTypes_properties.Query_description,
  svgIcon: typeQuery,
});

contentTypeLocalizationRegistry.addLocalization("CMQueryList", {
  displayName: BlueprintDoctypesDocTypes_properties.CMQueryList_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMQueryList_description,
  properties: {
    localSettings: {
      properties: {
        limit: { emptyText: BlueprintDoctypesDocTypes_properties.CMQueryList_localSettings_limit_emptyText },
        fq: {
          properties: {
            documents: { displayName: BlueprintDoctypesDocTypes_properties.CMQueryList_localSettings_fq_documents_displayName },
            authors: { displayName: BlueprintDoctypesDocTypes_properties.CMQueryList_localSettings_fq_authors_displayName },
          },
        },
      },
    },
    extendedItems: { displayName: BlueprintDoctypesDocTypes_properties.CMQueryList_extendedItems_displayName },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMSettings", {
  displayName: BlueprintDoctypesDocTypes_properties.CMSettings_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMSettings_description,
  svgIcon: typeSettings,
  properties: {
    settings: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSettings_settings_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSettings_settings_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSettings_settings_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("Preferences", { displayName: BlueprintDoctypesDocTypes_properties.Preferences_displayName });

contentTypeLocalizationRegistry.addLocalization("EditorPreferences", {
  displayName: BlueprintDoctypesDocTypes_properties.EditorPreferences_displayName,
  properties: {
    data: {
      description: BlueprintDoctypesDocTypes_properties.EditorPreferences_data_description,
      emptyText: BlueprintDoctypesDocTypes_properties.EditorPreferences_data_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("EditorProfile", { displayName: BlueprintDoctypesDocTypes_properties.EditorProfile_displayName });

contentTypeLocalizationRegistry.addLocalization("CMSite", {
  displayName: BlueprintDoctypesDocTypes_properties.CMSite_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMSite_description,
  svgIcon: typeSiteIndicator,
  properties: {
    root: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSite_root_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSite_root_description,
    },
    id: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSite_id_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSite_id_description,
    },
    name: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSite_name_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSite_name_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSite_name_emptyText,
    },
    locale: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSite_locale_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSite_locale_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSite_locale_emptyText,
    },
    master: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSite_master_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSite_master_description,
    },
    siteManagerGroup: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSite_siteManagerGroup_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSite_siteManagerGroup_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSite_siteManagerGroup_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMSitemap", {
  displayName: BlueprintDoctypesDocTypes_properties.CMSitemap_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMSitemap_description,
  svgIcon: typeSitemap,
  properties: {
    root: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSitemap_root_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSitemap_root_description,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSitemap_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSitemap_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSitemap_title_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMSymbol", {
  displayName: BlueprintDoctypesDocTypes_properties.CMSymbol_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMSymbol_description,
  svgIcon: typeSymbol,
  properties: {
    description: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSymbol_description_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSymbol_description_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMSymbol_description_emptyText,
    },
    icon: {
      displayName: BlueprintDoctypesDocTypes_properties.CMSymbol_icon_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMSymbol_icon_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMTaxonomy", {
  displayName: BlueprintDoctypesDocTypes_properties.CMTaxonomy_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMTaxonomy_description,
  svgIcon: tag,
  properties: {
    children: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTaxonomy_children_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTaxonomy_children_description,
    },
    externalReference: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTaxonomy_externalReference_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTaxonomy_externalReference_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTaxonomy_externalReference_emptyText,
    },
    value: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTaxonomy_value_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTaxonomy_value_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTaxonomy_value_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMTeasable", {
  displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMTeasable_description,
  svgIcon: typeObject,
  properties: {
    detailText: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_detailText_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTeasable_detailText_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTeasable_detailText_emptyText,
    },
    notSearchable: { displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_notSearchable_displayName },
    pictures: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_pictures_displayName,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTeasable_pictures_emptyText,
    },
    related: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_related_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTeasable_related_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTeasable_related_emptyText,
    },
    teaserText: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_teaserText_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTeasable_teaserText_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTeasable_teaserText_emptyText,
    },
    teaserTitle: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_teaserTitle_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTeasable_teaserTitle_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTeasable_teaserTitle_emptyText,
    },
    authors: { displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_authors_displayName },
    localSettings: { properties: { teaserSettings: { properties: { renderLinkToDetailPage: { displayName: BlueprintDoctypesDocTypes_properties.CMTeasable_localSettings_teaserSettings_renderLinkToDetailPage_displayName } } } } },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMTeaser", {
  displayName: BlueprintDoctypesDocTypes_properties.CMTeaser_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMTeaser_description,
  svgIcon: typeTeaser,
  properties: {
    target: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTeaser_target_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTeaser_target_description,
    },
    targets: { displayName: BlueprintDoctypesDocTypes_properties.CMTeaser_targets_displayName },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMVideo", {
  displayName: BlueprintDoctypesDocTypes_properties.CMVideo_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMVideo_description,
  svgIcon: typeVideo,
  properties: {
    data: { displayName: BlueprintDoctypesDocTypes_properties.CMVideo_data_displayName },
    dataUrl: {
      displayName: BlueprintDoctypesDocTypes_properties.CMVideo_dataUrl_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMVideo_dataUrl_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMVideo_dataUrl_emptyText,
    },
    title: {
      displayName: BlueprintDoctypesDocTypes_properties.CMVideo_title_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMVideo_title_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMVideo_title_emptyText,
    },
    detailText: { displayName: BlueprintDoctypesDocTypes_properties.CMVideo_detailText_displayName },
    timeLine: {
      displayName: BlueprintDoctypesDocTypes_properties.CMVideo_timeLine_displayName,
      properties: { defaultTarget: { displayName: BlueprintDoctypesDocTypes_properties.CMVideo_timeLine_defaultTarget_displayName } },
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMViewtype", {
  displayName: BlueprintDoctypesDocTypes_properties.CMViewtype_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMViewtype_description,
  svgIcon: typeViewtype,
  properties: {
    layout: {
      displayName: BlueprintDoctypesDocTypes_properties.CMViewtype_layout_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMViewtype_layout_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMViewtype_layout_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMTheme", {
  displayName: BlueprintDoctypesDocTypes_properties.CMTheme_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMTheme_description,
  svgIcon: typeTheming,
  properties: {
    resourceBundles: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_resourceBundles_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_resourceBundles_description,
    },
    templateSets: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_templateSets_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_templateSets_description,
    },
    javaScriptLibs: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_javaScriptLibs_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_javaScriptLibs_description,
    },
    javaScripts: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_javaScripts_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_javaScripts_description,
    },
    css: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_css_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_css_description,
    },
    viewRepositoryName: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_viewRepositoryName_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_viewRepositoryName_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTheme_viewRepositoryName_emptyText,
    },
    icon: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_icon_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_icon_description,
    },
    description: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_description_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_description_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTheme_description_emptyText,
    },
    detailText: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTheme_detailText_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTheme_detailText_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTheme_detailText_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMResourceBundle", {
  displayName: BlueprintDoctypesDocTypes_properties.CMResourceBundle_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMResourceBundle_description,
  svgIcon: typeResourceBundle,
  properties: {
    localizations: {
      displayName: BlueprintDoctypesDocTypes_properties.CMResourceBundle_localizations_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMResourceBundle_localizations_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMResourceBundle_localizations_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMVisual", {
  displayName: BlueprintDoctypesDocTypes_properties.CMVisual_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMVisual_description,
  svgIcon: typeObject,
  properties: {
    dataUrl: {
      displayName: BlueprintDoctypesDocTypes_properties.CMVisual_dataUrl_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMVisual_dataUrl_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMVisual_dataUrl_emptyText,
    },
    height: {
      displayName: BlueprintDoctypesDocTypes_properties.CMVisual_height_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMVisual_height_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMVisual_height_emptyText,
    },
    width: {
      displayName: BlueprintDoctypesDocTypes_properties.CMVisual_width_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMVisual_width_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMVisual_width_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMTemplateSet", {
  displayName: BlueprintDoctypesDocTypes_properties.CMTemplateSet_displayName,
  description: BlueprintDoctypesDocTypes_properties.CMTemplateSet_description,
  svgIcon: typeDownload,
  properties: {
    description: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTemplateSet_description_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTemplateSet_description_description,
      emptyText: BlueprintDoctypesDocTypes_properties.CMTemplateSet_description_emptyText,
    },
    archive: {
      displayName: BlueprintDoctypesDocTypes_properties.CMTemplateSet_archive_displayName,
      description: BlueprintDoctypesDocTypes_properties.CMTemplateSet_archive_description,
    },
  },
});
