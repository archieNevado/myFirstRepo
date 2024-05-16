import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
import LcDocTypes_properties from "./LcDocTypes_properties";
import typeAugmentedCategory from "./icons/type-augmented-category.svg";
import typeAugmentedProduct from "./icons/type-augmented-product.svg";
import typeExternalPage from "./icons/type-external-page.svg";
import typeMarketingSpot from "./icons/type-marketing-spot.svg";
import typeProductList from "./icons/type-product-list.svg";
import typeProductTeaser from "./icons/type-product-teaser.svg";

contentTypeLocalizationRegistry.addLocalization("CMMarketingSpot", {
  displayName: LcDocTypes_properties.CMMarketingSpot_displayName,
  svgIcon: typeMarketingSpot,
  properties: {
    externalId: {
      displayName: LcDocTypes_properties.CMMarketingSpot_externalId_displayName,
      emptyText: LcDocTypes_properties.CMMarketingSpot_externalId_emptyText,
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMProductTeaser", {
  displayName: LcDocTypes_properties.CMProductTeaser_displayName,
  svgIcon: typeProductTeaser,
  properties: {
    externalId: {
      displayName: LcDocTypes_properties.CMProductTeaser_externalId_displayName,
      emptyText: LcDocTypes_properties.CMProductTeaser_externalId_emptyText,
    },
    pictures: { displayName: LcDocTypes_properties.CMProductTeaser_pictures_displayName },
    localSettings: { properties: { shopNow: { displayName: LcDocTypes_properties.CMProductTeaser_localSettings_shopNow_displayName } } },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMProductList", {
  displayName: LcDocTypes_properties.CMProductList_displayName,
  svgIcon: typeProductList,
  properties: {
    externalId: {
      displayName: LcDocTypes_properties.CMProductList_externalId_displayName,
      emptyText: LcDocTypes_properties.CMProductList_externalId_emptyText,
    },
    items: { displayName: LcDocTypes_properties.CMProductList_items_displayName },
    localSettings: {
      properties: {
        productList: {
          displayName: LcDocTypes_properties.CMProductList_localSettings_productList_displayName,
          properties: {
            orderBy: {
              displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_displayName,
              emptyText: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_emptyText,
              properties: {
                ORDER_BY_TYPE_BRAND_ASC: { displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_ORDER_BY_TYPE_BRAND_ASC_displayName },
                ORDER_BY_TYPE_CATEGORY_ASC: { displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_ORDER_BY_TYPE_CATEGORY_ASC_displayName },
                ORDER_BY_TYPE_PRICE_ASC: { displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_ORDER_BY_TYPE_PRICE_ASC_displayName },
                ORDER_BY_TYPE_PRICE_DSC: { displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_ORDER_BY_TYPE_PRICE_DSC_displayName },
                ORDER_BY_TYPE_NAME_ASC: { displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_ORDER_BY_TYPE_NAME_ASC_displayName },
                ORDER_BY_TYPE_NAME_DSC: { displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_ORDER_BY_TYPE_NAME_DSC_displayName },
                ORDER_BY_TYPE_RELEVANCE: { displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_ORDER_BY_TYPE_RELEVANCE_displayName },
                ORDER_BY_TYPE_TOP_RATED: { displayName: LcDocTypes_properties.CMProductList_localSettings_productList_orderBy_ORDER_BY_TYPE_TOP_RATED_displayName },
              },
            },
            offset: {
              displayName: LcDocTypes_properties.CMProductList_localSettings_productList_offset_displayName,
              emptyText: LcDocTypes_properties.CMProductList_localSettings_productList_offset_emptyText,
            },
            maxLength: {
              displayName: LcDocTypes_properties.CMProductList_localSettings_productList_maxLength_displayName,
              emptyText: LcDocTypes_properties.CMProductList_localSettings_productList_maxLength_emptyText,
            },
          },
        },
      },
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMChannel", { properties: { localSettings: { properties: { shopNow: { displayName: LcDocTypes_properties.CMChannel_localSettings_shopNow_displayName } } } } });

contentTypeLocalizationRegistry.addLocalization("CMTeaser", { properties: { localSettings: { properties: { useTeaserTargetValidity: { displayName: LcDocTypes_properties.CMTeaser_localSettings_useTeaserTargetValidity_displayName } } } } });

contentTypeLocalizationRegistry.addLocalization("CMImageMap", {
  properties: {
    localSettings: {
      properties: {
        overlay: {
          properties: {
            displayDefaultPrice: { displayName: LcDocTypes_properties.CMImageMap_localSettings_overlay_displayDefaultPrice_displayName },
            displayDiscountedPrice: { displayName: LcDocTypes_properties.CMImageMap_localSettings_overlay_displayDiscountedPrice_displayName },
            displayOutOfStockLink: { displayName: LcDocTypes_properties.CMImageMap_localSettings_overlay_displayOutOfStockLink_displayName },
            hideOutOfStockProducts: { displayName: LcDocTypes_properties.CMImageMap_localSettings_overlay_hideOutOfStockProducts_displayName },
          },
        },
      },
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMExternalChannel", {
  displayName: LcDocTypes_properties.CMExternalChannel_displayName,
  description: LcDocTypes_properties.CMExternalChannel_description,
  svgIcon: typeAugmentedCategory,
  properties: {
    externalId: {
      displayName: LcDocTypes_properties.CMExternalChannel_externalId_displayName,
      description: LcDocTypes_properties.CMExternalChannel_externalId_description,
    },
    pdpPagegrid: {
      displayName: LcDocTypes_properties.CMExternalChannel_pdpPagegrid_displayName,
      properties: { placements_2: { properties: { layout: { displayName: LcDocTypes_properties.CMExternalChannel_pdpPagegrid_placements_2_layout_displayName } } } },
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMExternalProduct", {
  displayName: LcDocTypes_properties.CMExternalProduct_displayName,
  description: LcDocTypes_properties.CMExternalProduct_description,
  svgIcon: typeAugmentedProduct,
  properties: {
    externalId: {
      displayName: LcDocTypes_properties.CMExternalProduct_externalId_displayName,
      description: LcDocTypes_properties.CMExternalProduct_externalId_description,
    },
    pdpPagegrid: {
      displayName: LcDocTypes_properties.CMExternalProduct_pdpPagegrid_displayName,
      properties: { placements_2: { properties: { layout: { displayName: LcDocTypes_properties.CMExternalProduct_pdpPagegrid_placements_2_layout_displayName } } } },
    },
  },
});

contentTypeLocalizationRegistry.addLocalization("CMExternalPage", {
  displayName: LcDocTypes_properties.CMExternalPage_displayName,
  description: LcDocTypes_properties.CMExternalPage_description,
  svgIcon: typeExternalPage,
  properties: {
    externalId: {
      displayName: LcDocTypes_properties.CMExternalPage_externalId_displayName,
      emptyText: LcDocTypes_properties.CMExternalPage_externalId_emptyText,
    },
    externalUriPath: {
      displayName: LcDocTypes_properties.CMExternalPage_externalUriPath_displayName,
      emptyText: LcDocTypes_properties.CMExternalPage_externalUriPath_emptyText,
    },
  },
});
