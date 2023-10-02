import CatalogObject from "./CatalogObject";

abstract class Segments extends CatalogObject {

  /**
   * Returns a list of available segments for this store.
   * @return
   */
  abstract getSegments(): Array<any>;

}

export default Segments;
