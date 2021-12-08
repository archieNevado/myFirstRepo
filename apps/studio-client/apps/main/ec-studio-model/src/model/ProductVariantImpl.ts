import { mixin } from "@jangaroo/runtime";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Product from "./Product";
import ProductAttribute from "./ProductAttribute";
import ProductImpl from "./ProductImpl";
import ProductPropertyNames from "./ProductPropertyNames";
import ProductVariant from "./ProductVariant";

class ProductVariantImpl extends ProductImpl implements ProductVariant {
  static override readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/sku/{siteId:[^/]+}/{catalogAlias:[^/]+}/{externalId:.+}";

  constructor(uri: string) {
    super(uri);
  }

  getParent(): Product {
    return this.get(CatalogObjectPropertyNames.PARENT);
  }

  getDefiningAttributes(): Array<ProductAttribute> {
    return this.get(ProductPropertyNames.DEFINING_ATTRIBUTES);
  }

}
mixin(ProductVariantImpl, ProductVariant);

export default ProductVariantImpl;
