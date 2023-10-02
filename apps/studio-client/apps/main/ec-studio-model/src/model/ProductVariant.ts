import Product from "./Product";
import ProductAttribute from "./ProductAttribute";

abstract class ProductVariant extends Product {
  /**
   * @return the parent product of this given product variant
   * @see CatalogObjectPropertyNames#PARENT
   */
  abstract getParent(): Product;

  abstract getDefiningAttributes(): Array<ProductAttribute>;

}

export default ProductVariant;
