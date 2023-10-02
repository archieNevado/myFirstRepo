import Previewable from "@coremedia/studio-client.client-core/data/Previewable";
import { mixin } from "@jangaroo/runtime";
import Catalog from "./Catalog";
import CatalogObject from "./CatalogObject";
import Category from "./Category";
import ProductAttribute from "./ProductAttribute";
import ProductVariant from "./ProductVariant";

abstract class Product implements CatalogObject, Previewable {
  /**
   * Gets the current category. If it is possible to have multiple category associated to a product the first (or leading)
   * category should be returned.
   * @return the current category
   * @see CatalogObjectPropertyNames#CATEGORY
   */
  abstract getCategory(): Category;

  /**
   * Returns the catalog of this product
   *
   * @return the catalog
   *
   * @see CatalogObjectPropertyNames#CATALOG
   */
  abstract getCatalog(): Catalog;

  abstract getThumbnailUrl(): string;

  abstract getLongDescription(): string;

  abstract getOfferPrice(): number;

  abstract getListPrice(): number;

  abstract getCurrency(): string;

  abstract getDescribingAttributes(): Array<ProductAttribute>;

  abstract getVariants(): Array<ProductVariant>;

  abstract getVisuals(): Array<any>;

  abstract getPictures(): Array<any>;

  abstract getDownloads(): Array<any>;

}
interface Product extends CatalogObject, Previewable{}

mixin(Product, CatalogObject, Previewable);

export default Product;
