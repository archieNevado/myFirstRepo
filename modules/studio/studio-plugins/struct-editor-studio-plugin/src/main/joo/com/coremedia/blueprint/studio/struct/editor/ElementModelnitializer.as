package com.coremedia.blueprint.studio.struct.editor {
/**
 * Initializer for new nodes.
 */
public class ElementModelnitializer {

  /**
   * Type depending node initialization, so that XML is always valid.
   * @param model
   */
  public static function initNodeDefaults(model:ElementModel):void {
    var type:int = model.getType();
    if(type === ElementConstants.ELEMENT_INT_PROPERTY) {
      model.set(ElementConstants.VALUE_PROPERTY,0);
    }
    else if(type === ElementConstants.ELEMENT_BOOLEAN_PROPERTY) {
      model.set(ElementConstants.VALUE_PROPERTY,false);
    }
  }
}
}