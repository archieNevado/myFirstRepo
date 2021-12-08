import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Facet from "./Facet";
import Facets from "./Facets";

class SearchFacetsImpl extends CatalogObjectImpl implements Facets {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/searchfacets/{siteId:[^/]+}/{catalogAlias:[^/]+}/{categoryId:.+}";

  #facets: Array<any> = null;

  constructor(uri: string) {
    super(uri);
  }

  getFacets(): any {
    if (!this.#facets) {
      const facetObjects: Array<any> = this.get(CatalogObjectPropertyNames.FACETS);
      if (facetObjects === undefined) {
        return undefined;
      }

      if (facetObjects === null) {
        return [];
      }

      this.#facets = [];
      for (const facet of facetObjects) {
        this.#facets.push(new Facet(facet));
      }
    }
    return this.#facets;
  }
}
mixin(SearchFacetsImpl, Facets);

export default SearchFacetsImpl;
