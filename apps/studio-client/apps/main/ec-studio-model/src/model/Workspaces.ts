import CatalogObject from "./CatalogObject";

abstract class Workspaces extends CatalogObject {

  /**
   * Returns a list of available workspaces for this store.
   * @return
   */
  abstract getWorkspaces(): Array<any>;

}

export default Workspaces;
