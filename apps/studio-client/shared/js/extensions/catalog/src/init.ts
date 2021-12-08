import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
import CatalogDocTypes_properties from "./CatalogDocTypes_properties";
import typeCategory from "./icons/type-category.svg";
import typeProduct from "./icons/type-product.svg";

contentTypeLocalizationRegistry.addLocalization("CMProduct", {
  displayName: CatalogDocTypes_properties.CMProduct_displayName,
  svgIcon: typeProduct,
  properties: {
    productName: {
      displayName: CatalogDocTypes_properties.CMProduct_productName_displayName,
      description: CatalogDocTypes_properties.CMProduct_productName_description,
      emptyText: CatalogDocTypes_properties.CMProduct_productName_emptyText,
    },
    productCode: {
      displayName: CatalogDocTypes_properties.CMProduct_productCode_displayName,
      emptyText: CatalogDocTypes_properties.CMProduct_productCode_emptyText,
    },
    teaserText: {
      displayName: CatalogDocTypes_properties.CMProduct_teaserText_displayName,
      description: CatalogDocTypes_properties.CMProduct_teaserText_description,
      emptyText: CatalogDocTypes_properties.CMProduct_teaserText_emptyText,
    },
    detailText: {
      displayName: CatalogDocTypes_properties.CMProduct_detailText_displayName,
      description: CatalogDocTypes_properties.CMProduct_detailText_description,
      emptyText: CatalogDocTypes_properties.CMProduct_detailText_emptyText,
    },
    downloads: {
      displayName: CatalogDocTypes_properties.CMProduct_downloads_displayName,
      description: CatalogDocTypes_properties.CMProduct_downloads_description,
      emptyText: CatalogDocTypes_properties.CMProduct_downloads_emptyText,
    },
    contexts: {
      displayName: CatalogDocTypes_properties.CMProduct_contexts_displayName,
      description: CatalogDocTypes_properties.CMProduct_contexts_description,
      emptyText: CatalogDocTypes_properties.CMProduct_contexts_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMCategory", {
  displayName: CatalogDocTypes_properties.CMCategory_displayName,
  svgIcon: typeCategory,
  properties: {
    title: {
      displayName: CatalogDocTypes_properties.CMCategory_title_displayName,
      description: CatalogDocTypes_properties.CMCategory_title_description,
      emptyText: CatalogDocTypes_properties.CMCategory_title_emptyText,
    },
    categoryName: {
      displayName: CatalogDocTypes_properties.CMCategory_categoryName_displayName,
      emptyText: CatalogDocTypes_properties.CMCategory_categoryName_emptyText,
    },
    displayName: { displayName: CatalogDocTypes_properties.CMCategory_displayName_displayName },
    teaserText: {
      displayName: CatalogDocTypes_properties.CMCategory_teaserText_displayName,
      emptyText: CatalogDocTypes_properties.CMCategory_teaserText_emptyText,
    },
    detailText: {
      displayName: CatalogDocTypes_properties.CMCategory_detailText_displayName,
      emptyText: CatalogDocTypes_properties.CMCategory_detailText_emptyText,
    },
  },
});
