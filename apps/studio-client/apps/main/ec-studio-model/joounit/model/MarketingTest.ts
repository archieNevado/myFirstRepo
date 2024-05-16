import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as } from "@jangaroo/runtime";
import Marketing from "../../src/model/Marketing";

class MarketingTest extends AbstractCatalogTest {

  #marketing: Marketing = null;

  override setUp(): void {
    super.setUp();
    this.#marketing = as(beanFactory._.getRemoteBean("livecontext/marketing/HeliosSiteId"), Marketing);
  }

  testMarketing(): void {
    as(this.#marketing, RemoteBean).load(this.addAsync((): void =>
      Assert.assertEquals(3, this.#marketing.getMarketingSpots().length)
    , 500));
  }
}

export default MarketingTest;
