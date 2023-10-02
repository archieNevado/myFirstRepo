import CatalogObject from "./CatalogObject";

abstract class Contracts extends CatalogObject {

  /**
   * Returns a list of available contracts for this user.
   * @return
   */
  abstract getContracts(): Array<any>;

}

export default Contracts;
