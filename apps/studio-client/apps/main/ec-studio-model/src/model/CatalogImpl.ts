import { mixin } from "@jangaroo/runtime";
import Catalog from "./Catalog";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Category from "./Category";

class CatalogImpl extends CatalogObjectImpl implements Catalog {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/catalog/{siteId:[^/]+}/{externalId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }

  getRootCategory(): Category {
    return this.get(CatalogObjectPropertyNames.ROOT_CATEGORY);
  }

  isDefault(): boolean {
    return this.get(CatalogObjectPropertyNames.DEFAULT);
  }
}
mixin(CatalogImpl, Catalog);

export default CatalogImpl;
