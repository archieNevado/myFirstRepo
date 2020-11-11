package com.coremedia.ecommerce.studio.model {
/**
 * This is the data that the Studio Server backend delivers for a child
 */
public class CategoryChildData {


  public function CategoryChildData(childDataRaw:Object) {
    this.displayName = childDataRaw.hasOwnProperty("displayName") ? childDataRaw.displayName : null;
    this.isVirtual = childDataRaw.hasOwnProperty("isVirtual") ? childDataRaw.isVirtual : null;
    this.child = childDataRaw.hasOwnProperty("child") ? childDataRaw.child : null;
  }

  /**
   * The display name for the current child. Use this instead of {@link CatalogObject#getName}, to prevent an unnecessary
   * loading of the Remote Bean
   */
  public var displayName:String;
  /**
   * The flag that marks the child as so called 'link/hyperlink'. A virtual child will be displayed as a link node,
   * when clicking on it the selection jumps to the actual node
   */
  public var isVirtual:Boolean;

  /**
   * the child itself.
   */
  public var child:CatalogObject;

}
}
