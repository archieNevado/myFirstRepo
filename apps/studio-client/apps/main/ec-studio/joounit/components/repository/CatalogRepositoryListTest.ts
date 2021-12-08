import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as } from "@jangaroo/runtime";
import ECommerceStudioPlugin_properties from "../../../src/ECommerceStudioPlugin_properties";
import CatalogRepositoryList from "../../../src/components/repository/CatalogRepositoryList";
import AugmentationUtil from "../../../src/helper/AugmentationUtil";
import AbstractCatalogStudioTest from "../../AbstractCatalogStudioTest";
import CatalogRepositoryListTestView from "./CatalogRepositoryListTestView";

class CatalogRepositoryListTest extends AbstractCatalogStudioTest {
  #category: Category = null;

  #product: Product = null;

  #viewport: Viewport = null;

  override setUp(): void {
    super.setUp();
    this.#category = as(beanFactory._.getRemoteBean("livecontext/category/HeliosSiteId/catalog/Fruit"), Category);
    this.#product = as(beanFactory._.getRemoteBean("livecontext/product/HeliosSiteId/catalog/" + AbstractCatalogTest.ORANGES_EXTERNAL_ID), Product);

    this.#viewport = new CatalogRepositoryListTestView();
  }

  override tearDown(): void {
    super.tearDown();
    this.#viewport.destroy();
  }

  testCatalogListType(): void {
    const catalogList = as(this.#viewport.getComponent(CollectionViewConstants.LIST_VIEW), CatalogRepositoryList);
    Assert.assertEquals(AugmentationUtil.getTypeLabel(this.#category), ECommerceStudioPlugin_properties.Category_label);
    Assert.assertEquals(AugmentationUtil.getTypeLabel(this.#product), ECommerceStudioPlugin_properties.Product_label);
  }

  testCatalogListTypeCls(): void {
    const catalogList = as(this.#viewport.getComponent(CollectionViewConstants.LIST_VIEW), CatalogRepositoryList);
    Assert.assertEquals(AugmentationUtil.getTypeCls(this.#category), ECommerceStudioPlugin_properties.Category_icon);
    Assert.assertEquals(AugmentationUtil.getTypeCls(this.#product), ECommerceStudioPlugin_properties.Product_icon);
  }
}

export default CatalogRepositoryListTest;
