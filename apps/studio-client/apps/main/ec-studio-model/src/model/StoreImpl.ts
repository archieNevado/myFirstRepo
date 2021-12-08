import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import AsyncComputation from "@coremedia/studio-client.client-core/util/AsyncComputation";
import { bind, mixin } from "@jangaroo/runtime";
import int from "@jangaroo/runtime/int";
import { AnyFunction } from "@jangaroo/runtime/types";
import Catalog from "./Catalog";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Category from "./Category";
import Marketing from "./Marketing";
import Segments from "./Segments";
import Store from "./Store";

class StoreImpl extends CatalogObjectImpl implements Store {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/store/{siteId:[^/]+}";

  #siteId: string = null;

  readonly #resolvedUrls: Record<string, any> = {};

  readonly #URL_INVALIDATION_INTERVAL: int = 5000;

  constructor(uri: string, vars: any) {
    super(uri);
    this.#siteId = vars["siteId"];
  }

  getChildrenData(): Array<any> {
    return this.get(CatalogObjectPropertyNames.CHILDREN_DATA);
  }

  getStoreId(): string {
    return this.get(CatalogObjectPropertyNames.STORE_ID);
  }

  override getSiteId(): string {
    return this.#siteId;
  }

  getTopLevel(): Array<any> {
    return this.get("topLevel");
  }

  getMarketing(): Marketing {
    return this.get("marketing");
  }

  isMarketingEnabled(): boolean {
    return this.get("marketingEnabled");
  }

  getSegments(): Segments {
    return this.get(CatalogObjectPropertyNames.SEGMENTS);
  }

  getCatalogs(): Array<any> {
    return this.get(CatalogObjectPropertyNames.CATALOGS);
  }

  getDefaultCatalog(): Catalog {
    return this.get(CatalogObjectPropertyNames.DEFAULT_CATALOG);
  }

  isMultiCatalog(): boolean {
    return this.get(CatalogObjectPropertyNames.MULTI_CATALOG);
  }

  getRootCategory(): Category {
    return this.get(CatalogObjectPropertyNames.ROOT_CATEGORY);
  }

  getTimeZoneId(): string {
    return this.get("timeZoneId");
  }

  getVendorName(): string {
    return this.get(CatalogObjectPropertyNames.VENDOR_NAME);
  }

  override getStore(): Store {
    return this;
  }

  resolveShopUrlForPbe(shopUrl: string): RemoteBean {
    let resolvedUrl: any = this.#resolvedUrls[shopUrl];
    if (undefined === resolvedUrl) {
      const asyncUrlComputation = new AsyncComputation(bind(this, this.#requestShopUrl));
      resolvedUrl = {
        at: new Date(),
        async: asyncUrlComputation,
      };
    }

    if (undefined === this.#resolvedUrls[shopUrl] || resolvedUrl.at < new Date().getTime() - this.#URL_INVALIDATION_INTERVAL) {
      this.#resolvedUrls[shopUrl] = resolvedUrl;
      resolvedUrl.at = new Date();
      resolvedUrl.async.trigger(0, false, shopUrl);
    }
    return resolvedUrl.async.getValue();
  }

  #requestShopUrl(callback: AnyFunction, shopUrl: string): void {
    const urlResolveUri = this.getUriPath() + "/urlService";
    const remoteServiceMethod = new RemoteServiceMethod(urlResolveUri, "POST", true, true);
    remoteServiceMethod.request({ shopUrl: shopUrl },
      (response: RemoteServiceMethodResponse): void => {
        callback(response.getResponseJSON());
      });
  }
}
mixin(StoreImpl, Store);

export default StoreImpl;
