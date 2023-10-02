import CatalogObject from "./CatalogObject";

abstract class Marketing extends CatalogObject {

  /**
   * Returns a list of available marketing spots for this store.
   * @return
   */
  abstract getMarketingSpots(): Array<any>;

  /**
   * Return a mapping of the name of marketing spots to themselves
   *
   * @see CatalogObjectPropertyNames#CHILDREN_DATA
   */
  abstract getChildrenData(): Array<any>;
}

export default Marketing;
