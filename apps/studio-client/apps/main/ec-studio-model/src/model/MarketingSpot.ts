import CatalogObject from "./CatalogObject";
import Marketing from "./Marketing";

abstract class MarketingSpot extends CatalogObject {

  /**
   * Returns the parent marketing bean.
   * @return
   */
  abstract getMarketing(): Marketing;
}

export default MarketingSpot;
