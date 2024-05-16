import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as } from "@jangaroo/runtime";
import Category from "../../src/model/Category";

class CategoryTest extends AbstractCatalogTest {

  #rootCategory: Category = null;

  #topCategory: Category = null;

  #leafCategory: Category = null;

  override setUp(): void {
    super.setUp();
    this.#rootCategory = as(beanFactory._.getRemoteBean("livecontext/category/HeliosSiteId/catalog/ROOT"), Category);
    this.#topCategory = as(beanFactory._.getRemoteBean("livecontext/category/HeliosSiteId/catalog/Grocery"), Category);
    this.#leafCategory = as(beanFactory._.getRemoteBean("livecontext/category/HeliosSiteId/catalog/Fruit"), Category);
  }

  testTopCategory(): void {
    as(this.#topCategory, RemoteBean).load(this.addAsync((): void => {
      Assert.assertEquals("Grocery", this.#topCategory.getName());
      Assert.assertTrue(this.#topCategory.getId().indexOf("ibm:///catalog/category/Grocery") == 0);
      Assert.assertEquals(2, this.#topCategory.getChildren().length);
      Assert.assertEquals(this.#rootCategory, this.#topCategory.getParent());
    }, 500));
  }

  testLeafCategory(): void {
    as(this.#leafCategory, RemoteBean).load(this.addAsync((): void => {
      Assert.assertEquals("Fruit", this.#leafCategory.getName());
      Assert.assertTrue(this.#leafCategory.getId().indexOf("ibm:///catalog/category/Fruit") == 0);
      Assert.assertEquals(3, this.#leafCategory.getChildren().length);
      Assert.assertNotNull(this.#leafCategory.getParent());
    }, 500));
  }
}

export default CategoryTest;
