import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import Site from "@coremedia/studio-client.multi-site-models/Site";
import { is } from "@jangaroo/runtime";
import catalogHelper from "../catalogHelper";

/**
 * Utilities for dealing with store instances.
 */
class StoreUtil {

  static getStoreForSiteExpression(site: Site): ValueExpression {
    return ValueExpressionFactory.createFromFunction(StoreUtil.getStoreForSite, site);
  }

  static getStoreForSite(site: Site): Store {
    const siteId = site.getId();
    return catalogHelper.getValidatedStore(siteId);
  }

  static getRootCategoryForStoreExpression(store: Store): ValueExpression {
    return ValueExpressionFactory.createFromFunction(StoreUtil.getRootCategoryForStore, store);
  }

  static getRootCategoryForStore(store: Store): Category {
    if (store === undefined) {
      return undefined;
    }
    if (is(store, Store)) {
      const defaultCatalog = store.getDefaultCatalog();
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

  static getActiveStore(): Store {
    return catalogHelper.getActiveStore();
  }
}

export default StoreUtil;
