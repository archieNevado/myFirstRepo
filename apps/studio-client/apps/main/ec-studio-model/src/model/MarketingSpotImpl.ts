import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Marketing from "./Marketing";
import MarketingSpot from "./MarketingSpot";

class MarketingSpotImpl extends CatalogObjectImpl implements MarketingSpot {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/marketingspot/{siteId:[^/]+}/{externalId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }

  getMarketing(): Marketing {
    return this.get(CatalogObjectPropertyNames.MARKETING);
  }
}
mixin(MarketingSpotImpl, MarketingSpot);

export default MarketingSpotImpl;
