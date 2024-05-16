import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as } from "@jangaroo/runtime";
import Category from "../../src/model/Category";
import Product from "../../src/model/Product";

class ProductTest extends AbstractCatalogTest {

  #product: Product = null;

  #leafCategory: Category = null;

  override setUp(): void {
    super.setUp();
    this.#product = as(beanFactory._.getRemoteBean("livecontext/product/HeliosSiteId/catalog/" + AbstractCatalogTest.ORANGES_EXTERNAL_ID), Product);
    this.#leafCategory = as(beanFactory._.getRemoteBean("livecontext/category/HeliosSiteId/catalog/Fruit"), Category);
  }

  testProduct(): void {
    as(this.#product, RemoteBean).load(this.addAsync((): void => {
      Assert.assertEquals(AbstractCatalogTest.ORANGES_NAME, this.#product.getName());
      Assert.assertEquals(AbstractCatalogTest.ORANGES_EXTERNAL_ID, this.#product.getExternalId());
      Assert.assertTrue(this.#product.getId().indexOf(AbstractCatalogTest.ORANGES_ID) == 0);
      Assert.assertEquals(this.#leafCategory, this.#product.getCategory());
    }, 500));
  }
}

export default ProductTest;
