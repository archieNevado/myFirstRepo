package com.coremedia.livecontext.studio.library {
import com.coremedia.cap.content.ContentTypeNames;
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ecommerce.studio.catalogHelper;
import com.coremedia.ecommerce.studio.library.ECommerceCollectionViewExtension;
import com.coremedia.ecommerce.studio.model.Catalog;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Marketing;
import com.coremedia.ecommerce.studio.model.Store;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.ecommerce.studio.ECommerceStudioPlugin')]
public class LivecontextCollectionViewExtension extends ECommerceCollectionViewExtension {
  protected static const DEFAULT_TYPE_MARKETING_SPOT_RECORD:Object = {
    name: ContentTypeNames.CONTENT,
    label: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'MarketingSpot_label'),
    icon: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'MarketingSpot_icon')
  };

  protected static const PRODUCT_VARIANT_TYPE_RECORD:Object = {
    name: CatalogModel.TYPE_PRODUCT_VARIANT,
    label: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'ProductVariant_label'),
    icon: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'ProductVariant_icon')
  };

  protected static const CATEGORY_TYPE_RECORD:Object = {
    name: CatalogModel.TYPE_CATEGORY,
    label: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'Category_label'),
    icon: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'Category_icon')
  };

  protected static const MARKETING_SPOT_TYPE_RECORD:Object = {
    name: CatalogModel.TYPE_MARKETING_SPOT,
    label: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'MarketingSpot_label'),
    icon: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'MarketingSpot_icon')
  };

  public function LivecontextCollectionViewExtension() {
    super();
  }

  override public function getAvailableSearchTypes(folder:Object):Array {
    if (folder is CatalogObject) {
      if (folder is Marketing) {
        return [DEFAULT_TYPE_MARKETING_SPOT_RECORD];
      }
      var availableSearchTypes:Array = [DEFAULT_TYPE_PRODUCT_RECORD, PRODUCT_VARIANT_TYPE_RECORD];
      if (folder is Store) {
        var store:Store = catalogHelper.getActiveStoreExpression().getValue();
        if (store && store.isMarketingEnabled()) {
          availableSearchTypes.push(MARKETING_SPOT_TYPE_RECORD);
        }
      }
      // category search is only available if category root or catalog is selected.
      // category search within the category tree is not possible since category drill down is not supported.
      if (folder is Store || folder is Catalog || (folder is Category && folder.getParent() == null)) {
        availableSearchTypes.push(CATEGORY_TYPE_RECORD);
      }
      return availableSearchTypes;
    }
    return null;
  }

  override public function showInTree(contents:Array, view:String = null, treeModelId:String = null):void {
    new ShowInCatalogTreeHelper(contents).showItems(treeModelId);
  }
}
}
