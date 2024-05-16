import Catalog from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Catalog";
import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Marketing from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Marketing";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import catalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/catalogHelper";
import ECommerceCollectionViewExtension from "@coremedia-blueprint/studio-client.main.ec-studio/library/ECommerceCollectionViewExtension";
import ContentTypeNames from "@coremedia/studio-client.cap-rest-client/content/ContentTypeNames";
import { is } from "@jangaroo/runtime";
import ShowInCatalogTreeHelper from "./ShowInCatalogTreeHelper";

class LivecontextCollectionViewExtension extends ECommerceCollectionViewExtension {
  protected static readonly DEFAULT_TYPE_MARKETING_SPOT_RECORD: Record<string, any> = {
    name: ContentTypeNames.CONTENT,
    label: ECommerceStudioPlugin_properties.MarketingSpot_label,
    icon: ECommerceStudioPlugin_properties.MarketingSpot_icon,
  };

  protected static readonly PRODUCT_VARIANT_TYPE_RECORD: Record<string, any> = {
    name: CatalogModel.TYPE_PRODUCT_VARIANT,
    label: ECommerceStudioPlugin_properties.ProductVariant_label,
    icon: ECommerceStudioPlugin_properties.ProductVariant_icon,
  };

  protected static readonly CATEGORY_TYPE_RECORD: Record<string, any> = {
    name: CatalogModel.TYPE_CATEGORY,
    label: ECommerceStudioPlugin_properties.Category_label,
    icon: ECommerceStudioPlugin_properties.Category_icon,
  };

  protected static readonly MARKETING_SPOT_TYPE_RECORD: Record<string, any> = {
    name: CatalogModel.TYPE_MARKETING_SPOT,
    label: ECommerceStudioPlugin_properties.MarketingSpot_label,
    icon: ECommerceStudioPlugin_properties.MarketingSpot_icon,
  };

  constructor() {
    super();
  }

  override getAvailableSearchTypes(folder: any): Array<any> {
    if (is(folder, CatalogObject)) {
      if (is(folder, Marketing)) {
        return [LivecontextCollectionViewExtension.DEFAULT_TYPE_MARKETING_SPOT_RECORD];
      }
      const availableSearchTypes = [ECommerceCollectionViewExtension.DEFAULT_TYPE_PRODUCT_RECORD, LivecontextCollectionViewExtension.PRODUCT_VARIANT_TYPE_RECORD];
      if (is(folder, Store)) {
        const store: Store = catalogHelper.getActiveStoreExpression().getValue();
        if (store && store.isMarketingEnabled()) {
          availableSearchTypes.push(LivecontextCollectionViewExtension.MARKETING_SPOT_TYPE_RECORD);
        }
      }
      // category search is only available if category root or catalog is selected.
      // category search within the category tree is not possible since category drill down is not supported.
      if (is(folder, Store) || is(folder, Catalog) || (is(folder, Category) && folder.getParent() == null)) {
        availableSearchTypes.push(LivecontextCollectionViewExtension.CATEGORY_TYPE_RECORD);
      }
      return availableSearchTypes;
    }
    return null;
  }

  override showInTree(contents: Array<any>, view: string = null, treeModelId: string = null): void {
    new ShowInCatalogTreeHelper(contents).showItems(treeModelId);
  }
}

export default LivecontextCollectionViewExtension;
