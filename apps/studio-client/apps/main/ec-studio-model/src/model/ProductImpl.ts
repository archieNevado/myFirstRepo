import Previews from "@coremedia/studio-client.client-core/data/Previews";
import { mixin } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import Catalog from "./Catalog";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Category from "./Category";
import Product from "./Product";
import ProductAttribute from "./ProductAttribute";
import ProductPropertyNames from "./ProductPropertyNames";
import ProductVariant from "./ProductVariant";

class ProductImpl extends CatalogObjectImpl implements Product {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/product/{siteId:[^/]+}/{catalogAlias:[^/]+}/{externalId:.+}";

  constructor(uri: string) {
    super(uri);
  }

  getCategory(): Category {
    return this.get(CatalogObjectPropertyNames.CATEGORY);
  }

  getCatalog(): Catalog {
    return this.get(CatalogObjectPropertyNames.CATALOG);
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

  getOfferPrice(): number {
    return this.get(ProductPropertyNames.OFFER_PRICE);
  }

  getListPrice(): number {
    return this.get(ProductPropertyNames.LIST_PRICE);
  }

  getCurrency(): string {
    return this.get(ProductPropertyNames.CURRENCY);
  }

  getVariants(): Array<ProductVariant> {
    return this.get(ProductPropertyNames.VARIANTS);
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

  getLongDescription(): string {
    return this.get(CatalogObjectPropertyNames.LONG_DESCRIPTION);
  }

  getDescribingAttributes(): Array<ProductAttribute> {
    return this.get(ProductPropertyNames.DESCRIBING_ATTRIBUTES);
  }

  override invalidate(callback: AnyFunction = null): void {
    if (!this.hasListeners()) {
      super.invalidate();
      return;
    }

    const thiz: any = this;
    super.invalidate((): void => {
      callback && callback(thiz);
      //all product variants need to be invalidated as well
      const variants: Array<any> = this.getVariants() || [];
      for (const variant of variants as ProductVariant[]) {
        variant.invalidate();
      }
    });
  }
}
mixin(ProductImpl, Product);

export default ProductImpl;
