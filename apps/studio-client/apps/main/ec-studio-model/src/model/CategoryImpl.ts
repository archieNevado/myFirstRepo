import Previews from "@coremedia/studio-client.client-core/data/Previews";
import { as, mixin } from "@jangaroo/runtime";
import Catalog from "./Catalog";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Category from "./Category";
import CategoryChildData from "./CategoryChildData";
import SearchFacets from "./SearchFacets";

class CategoryImpl extends CatalogObjectImpl implements Category {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/category/{siteId:[^/]+}/{catalogAlias:[^/]+}/{externalId:.+}";

  constructor(uri: string, vars: any) {
    super(uri);
  }

  getChildrenData(): Array<any> {
    const childrenDataRaw = as(this.get(CatalogObjectPropertyNames.CHILDREN_DATA), Array);
    if (!childrenDataRaw) {
      return childrenDataRaw;
    }
    return childrenDataRaw.map((childDataRaw: any): CategoryChildData =>
      new CategoryChildData(childDataRaw),
    );
  }

  getChildren(): Array<any> {
    return this.get(CatalogObjectPropertyNames.CHILDREN);
  }

  getSubCategories(): Array<any> {
    return this.get(CatalogObjectPropertyNames.SUB_CATEGORIES);
  }

  getThumbnailUrl(): string {
    return this.get(CatalogObjectPropertyNames.THUMBNAIL_URL);
  }

  getDefaultPreviewUrl(): string {
    return this.get(CatalogObjectPropertyNames.PREVIEW_URL);
  }

  hasMultiPreviews(): boolean {
    return true;
  }

  getPreviews(): Previews {
    return this.get(CatalogObjectPropertyNames.PREVIEWS);
  }

  getParent(): Category {
    return this.get(CatalogObjectPropertyNames.PARENT);
  }

  getCatalog(): Catalog {
    return this.get(CatalogObjectPropertyNames.CATALOG);
  }

  getDisplayName(): string {
    return this.get(CatalogObjectPropertyNames.DISPLAY_NAME);
  }

  getProducts(): Array<any> {
    return this.get(CatalogObjectPropertyNames.PRODUCTS);
  }

  getVisuals(): Array<any> {
    return this.get(CatalogObjectPropertyNames.VISUALS);
  }

  getPictures(): Array<any> {
    return this.get(CatalogObjectPropertyNames.PICTURES);
  }

  getDownloads(): Array<any> {
    return this.get(CatalogObjectPropertyNames.DOWNLOADS);
  }

  getSearchFacets(): SearchFacets {
    return this.get(CatalogObjectPropertyNames.SEARCH_FACETS);
  }
}
mixin(CategoryImpl, Category);

export default CategoryImpl;
