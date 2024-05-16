import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { as } from "@jangaroo/runtime";
import MarketingSpot from "../../src/model/MarketingSpot";

class MarketingSpotTest extends AbstractCatalogTest {

  #marketingSpot: MarketingSpot = null;

  override setUp(): void {
    super.setUp();
    this.#marketingSpot = as(beanFactory._.getRemoteBean("livecontext/marketingspot/HeliosSiteId/spot1"), MarketingSpot);
  }

  testMarketingSpots(): void {
    as(this.#marketingSpot, RemoteBean).load(this.addAsync((): void =>
      Assert.assertEquals(true, this.#marketingSpot.getMarketing() !== null)
    , 500));
  }
}

export default MarketingSpotTest;
