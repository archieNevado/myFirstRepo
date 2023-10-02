package com.coremedia.ecommerce.studio.helper {

import com.coremedia.cms.studio.multisite.models.sites.Site;
import com.coremedia.ecommerce.studio.catalogHelper;
import com.coremedia.ecommerce.studio.model.Catalog;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

/**
 * Utilities for dealing with store instances.
 */
public class StoreUtil {

  public static function getStoreForSiteExpression(site:Site):ValueExpression {
    return ValueExpressionFactory.createFromFunction(getStoreForSite, site);
  }

  public static function getStoreForSite(site:Site):Store {
    var siteId:String = site.getId();
    return catalogHelper.getValidatedStore(siteId);
  }

  public static function getRootCategoryForStoreExpression(store:Store):ValueExpression {
    return ValueExpressionFactory.createFromFunction(getRootCategoryForStore, store);
  }

  public static function getRootCategoryForStore(store:Store):Category {
    if(store === undefined) {
      return undefined;
    }
    if(store is Store) {
      var defaultCatalog:Catalog = store.getDefaultCatalog();
      if (defaultCatalog === undefined) {
        return undefined;
      }
      if (defaultCatalog === null) {
        return store.getRootCategory();
      }
      return defaultCatalog.getRootCategory();
    }
    return null;
  }

  public static function getActiveStore():Store {
    return catalogHelper.getActiveStore();
  }
}
}
