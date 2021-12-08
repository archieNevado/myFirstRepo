import Previewable from "@coremedia/studio-client.client-core/data/Previewable";
import { mixin } from "@jangaroo/runtime";
import Catalog from "./Catalog";
import CatalogObject from "./CatalogObject";
import SearchFacets from "./SearchFacets";

abstract class Category implements CatalogObject, Previewable {

  /**
   * Return a list of child categories and products
   *
   * @see CatalogObjectPropertyNames#CHILDREN
   */
  abstract getChildren(): Array<any>/* Vector.<CatalogObject> */;

  /**
   * Return a list of objects that hold {@link CategoryChildData}
   *
   * @see CatalogObjectPropertyNames#CHILDREN_DATA
   */
  abstract getChildrenData(): Array<any>/* Vector.<CategoryChildData> */;

  /**
   * Return list of child categories, sorted by name (case insensitive)
   *
   * @see CatalogObjectPropertyNames#SUB_CATEGORIES
   */
  abstract getSubCategories(): Array<any>/* Vector.<Category> */;

  /**
   * Return list of direct child Products, sorted by name (case insensitive)
   *
   * @see CatalogObjectPropertyNames#SUB_CATEGORIES
   */
  abstract getProducts(): Array<any>/* Vector.<Product> */;

  abstract getThumbnailUrl(): string;

  /**
   * Returns the parent category
   * Returns null, if this is the top category
   *
   * @return the parent category
   *
   * @see CatalogObjectPropertyNames#PARENT
   */
  abstract getParent(): Category;

  /**
   * Returns the catalog of this category
   *
   * @return the catalog
   *
   * @see CatalogObjectPropertyNames#CATALOG
   */
  abstract getCatalog(): Catalog;

  /**
   * @return The display name for a category
   * @see CatalogObjectPropertyNames#DISPLAY_NAME
   */
  abstract getDisplayName(): string;

  abstract getVisuals(): Array<any>;

  abstract getPictures(): Array<any>;

  abstract getDownloads(): Array<any>;

  abstract getSearchFacets(): SearchFacets;
}
interface Category extends CatalogObject, Previewable{}

mixin(Category, CatalogObject, Previewable);

export default Category;
