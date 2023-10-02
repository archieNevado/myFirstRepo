import CatalogObject from "./CatalogObject";

/**
 * This is the data that the Studio Server backend delivers for a child
 */
class CategoryChildData {

  constructor(childDataRaw: any) {
    this.displayName = childDataRaw.hasOwnProperty("displayName") ? childDataRaw.displayName : null;
    this.isVirtual = childDataRaw.hasOwnProperty("isVirtual") ? childDataRaw.isVirtual : null;
    this.child = childDataRaw.hasOwnProperty("child") ? childDataRaw.child : null;
  }

  /**
   * The display name for the current child. Use this instead of {@link CatalogObject#getName}, to prevent an unnecessary
   * loading of the Remote Bean
   */
  displayName: string = null;

  /**
   * The flag that marks the child as so called 'link/hyperlink'. A virtual child will be displayed as a link node,
   * when clicking on it the selection jumps to the actual node
   */
  isVirtual: boolean = false;

  /**
   * the child itself.
   */
  child: CatalogObject = null;

}

export default CategoryChildData;
