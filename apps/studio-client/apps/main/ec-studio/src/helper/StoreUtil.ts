import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import RemoteBeanUtil from "@coremedia/studio-client.client-core/data/RemoteBeanUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Site from "@coremedia/studio-client.multi-site-models/Site";
import { as, is } from "@jangaroo/runtime";

/**
 * Utilities for dealing with store instances.
 */
class StoreUtil {

  static getStoreForSiteExpression(site: Site): ValueExpression {
    return ValueExpressionFactory.createFromFunction(StoreUtil.getStoreForSite, site);
  }

  static getStoreForSite(site: Site): Store {
    const siteId = site.getId();
    return StoreUtil.getValidatedStore(siteId);
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

  /**
   * Return the store for the given siteId if it exists, null otherwise.
   */
  static getValidatedStore(siteId: string): Store {
    if (siteId === undefined) {
      return undefined;
    }
    if (siteId === null) {
      return null;
    }
    const store = as(beanFactory._.getRemoteBean("livecontext/store/" + siteId), Store);
    // only the server knows if the store exists, so load and check if it's ID has some value
    const accessible = RemoteBeanUtil.isAccessible(store);
    if (accessible === undefined) {
      return undefined;
    }

    return accessible ? store : null;
  }

  static getActiveStore(): Store {
    const siteId = editorContext._.getSitesService().getPreferredSiteId();
    return StoreUtil.getValidatedStore(siteId);
  }
}

export default StoreUtil;
