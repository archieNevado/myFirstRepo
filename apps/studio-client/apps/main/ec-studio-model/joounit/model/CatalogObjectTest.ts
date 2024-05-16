import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as } from "@jangaroo/runtime";
import CatalogObject from "../../src/model/CatalogObject";

class CatalogObjectTest extends AbstractCatalogTest {

  #catalogObject: CatalogObject = null;

  override setUp(): void {
    super.setUp();
    this.#catalogObject = as(beanFactory._.getRemoteBean("livecontext/store/HeliosSiteId"), CatalogObject);
  }

  testCatalogObject(): void {
    as(this.#catalogObject, RemoteBean).load(this.addAsync((): void =>
      Assert.assertEquals("PerfectChefESite", this.#catalogObject.getName())
    , 500));
  }

}

export default CatalogObjectTest;
