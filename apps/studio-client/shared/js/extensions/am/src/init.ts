import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
import AmDocTypes_properties from "./AmDocTypes_properties";
import typeAssetDocument from "./icons/type-asset-document.svg";
import typeAssetObject from "./icons/type-asset-object.svg";
import typeAssetPicture from "./icons/type-asset-picture.svg";
import typeAssetVideo from "./icons/type-asset-video.svg";

contentTypeLocalizationRegistry.addLocalization("AMAsset", {
  displayName: AmDocTypes_properties.AMAsset_displayName,
  description: AmDocTypes_properties.AMAsset_description,
  svgIcon: typeAssetObject,
  properties: {
    original: {
      displayName: AmDocTypes_properties.AMAsset_original_displayName,
      description: AmDocTypes_properties.AMAsset_original_description,
    },
    thumbnail: {
      displayName: AmDocTypes_properties.AMAsset_thumbnail_displayName,
      description: AmDocTypes_properties.AMAsset_thumbnail_description,
    },
    metadata: {
      properties: {
        copyright: {
          displayName: AmDocTypes_properties.AMAsset_metadata_copyright_displayName,
          description: AmDocTypes_properties.AMAsset_metadata_copyright_description,
          emptyText: AmDocTypes_properties.AMAsset_metadata_copyright_emptyText,
        },
        expirationDate: {
          displayName: AmDocTypes_properties.AMAsset_metadata_expirationDate_displayName,
          emptyText: AmDocTypes_properties.AMAsset_metadata_expirationDate_emptyText,
        },
        channels: { displayName: AmDocTypes_properties.AMAsset_metadata_channels_displayName },
        regions: { displayName: AmDocTypes_properties.AMAsset_metadata_regions_displayName },
      },
    },
    keywords: {
      displayName: AmDocTypes_properties.AMAsset_keywords_displayName,
      description: AmDocTypes_properties.AMAsset_keywords_description,
      emptyText: AmDocTypes_properties.AMAsset_keywords_emptyText,
    },
    locationTaxonomy: {
      displayName: AmDocTypes_properties.AMAsset_locationTaxonomy_displayName,
      description: AmDocTypes_properties.AMAsset_locationTaxonomy_description,
      emptyText: AmDocTypes_properties.AMAsset_locationTaxonomy_emptyText,
    },
    subjectTaxonomy: {
      displayName: AmDocTypes_properties.AMAsset_subjectTaxonomy_displayName,
      description: AmDocTypes_properties.AMAsset_subjectTaxonomy_description,
      emptyText: AmDocTypes_properties.AMAsset_subjectTaxonomy_emptyText,
    },
    assetTaxonomy: {
      displayName: AmDocTypes_properties.AMAsset_assetTaxonomy_displayName,
      description: AmDocTypes_properties.AMAsset_assetTaxonomy_description,
      emptyText: AmDocTypes_properties.AMAsset_assetTaxonomy_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("AMPictureAsset", {
  displayName: AmDocTypes_properties.AMPictureAsset_displayName,
  description: AmDocTypes_properties.AMPictureAsset_description,
  svgIcon: typeAssetPicture,
  properties: {
    web: {
      displayName: AmDocTypes_properties.AMPictureAsset_web_displayName,
      description: AmDocTypes_properties.AMPictureAsset_web_description,
    },
    print: {
      displayName: AmDocTypes_properties.AMPictureAsset_print_displayName,
      description: AmDocTypes_properties.AMPictureAsset_print_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("AMVideoAsset", {
  displayName: AmDocTypes_properties.AMVideoAsset_displayName,
  description: AmDocTypes_properties.AMVideoAsset_description,
  svgIcon: typeAssetVideo,
  properties: {
    web: {
      displayName: AmDocTypes_properties.AMVideoAsset_web_displayName,
      description: AmDocTypes_properties.AMVideoAsset_web_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("AMDocumentAsset", {
  displayName: AmDocTypes_properties.AMDocumentAsset_displayName,
  description: AmDocTypes_properties.AMDocumentAsset_description,
  svgIcon: typeAssetDocument,
  properties: {
    download: {
      displayName: AmDocTypes_properties.AMDocumentAsset_download_displayName,
      description: AmDocTypes_properties.AMDocumentAsset_download_description,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("AMTaxonomy", {
  displayName: AmDocTypes_properties.AMTaxonomy_displayName,
  description: AmDocTypes_properties.AMTaxonomy_description,
  properties: {
    value: {
      displayName: AmDocTypes_properties.AMTaxonomy_value_displayName,
      description: AmDocTypes_properties.AMTaxonomy_value_description,
      emptyText: AmDocTypes_properties.AMTaxonomy_value_emptyText,
    },
    assetThumbnail: { emptyText: AmDocTypes_properties.AMTaxonomy_assetThumbnail_emptyText },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMPicture", { properties: { asset: { displayName: AmDocTypes_properties.CMPicture_asset_displayName } } });
