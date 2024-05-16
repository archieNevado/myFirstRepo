import { mixin } from "@jangaroo/runtime";
import CatalogObjectImpl from "./CatalogObjectImpl";
import CatalogObjectPropertyNames from "./CatalogObjectPropertyNames";
import Marketing from "./Marketing";

class MarketingImpl extends CatalogObjectImpl implements Marketing {
  static readonly REST_RESOURCE_URI_TEMPLATE: string = "livecontext/marketing/{siteId:[^/]+}";

  constructor(uri: string) {
    super(uri);
  }

  getChildrenData(): Array<any> {
    return this.get(CatalogObjectPropertyNames.CHILDREN_DATA);
  }

  getMarketingSpots(): Array<any> {
    return this.get(CatalogObjectPropertyNames.MARKETING_SPOTS);
  }
}
mixin(MarketingImpl, Marketing);

export default MarketingImpl;
