import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as } from "@jangaroo/runtime";
import Store from "../../src/model/Store";

class StoreTest extends AbstractCatalogTest {

  #store: Store = null;

  override setUp(): void {
    super.setUp();
    this.#store = as(beanFactory._.getRemoteBean("livecontext/store/HeliosSiteId"), Store);
  }

  testStore(): void {
    as(this.#store, RemoteBean).load(this.addAsync((): void => {
      Assert.assertEquals("PerfectChefESite", this.#store.getName());
      Assert.assertEquals(2, this.#store.getTopLevel().length);
      Assert.assertEquals("10851", this.#store.getStoreId());
    }, 500));
  }
}

export default StoreTest;
