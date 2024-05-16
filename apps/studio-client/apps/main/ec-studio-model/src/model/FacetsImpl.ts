import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Facets from "./Facets";

class FacetsImpl extends CatalogObjectImpl implements Facets {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/facets/{siteId:[^/]+}/{catalogAlias:[^/]+}/{categoryId:.+}";

  constructor(uri: string) {
    super(uri);
  }

  getFacets(): any {
    return this.get(CatalogObjectPropertyNames.FACETS);
  }
}
mixin(FacetsImpl, Facets);

export default FacetsImpl;
