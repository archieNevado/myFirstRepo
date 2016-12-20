package com.coremedia.ecommerce.studio.components.link {
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.mixins.IValidationStateMixin;
import com.coremedia.ui.mixins.ValidationState;

import ext.container.Container;

public class CatalogLinkPropertyFieldBase extends Container implements IValidationStateMixin {

  private var readOnlyExpression:ValueExpression;

  /** @inheritDoc */
  [Bindable]
  public native function validationInit(validationState:ValidationState = undefined):void;

  /** @private */
  [Bindable]
  public native function set validationState(validationState:ValidationState):void;

  /** @inheritDoc */
  [Bindable]
  public native function get validationState():ValidationState;

  /** @private */
  [Bindable]
  public native function set validationStateVE(validationStateVE:ValueExpression):void;

  /** @inheritDoc */
  [Bindable]
  public native function get validationStateVE():ValueExpression;

  public function CatalogLinkPropertyFieldBase(config:CatalogLinkPropertyField = null) {
    super(config);
    validationInit();
  }

  /**
   * Returns value expression for if the capacity is free or can not be calculated.
   */
  protected function getHasFreeCapacityExpression(config:CatalogLinkPropertyField):ValueExpression{
    var hasFreeCapacityExpression:ValueExpression = ValueExpressionFactory.createFromFunction(function ():Boolean {
      if (config.multiple) {
        return true;
      }
      var catalogObjectsExpression:ValueExpression = config.bindTo.extendBy('properties').extendBy(config.propertyName);
      var catalogObjects:Array = catalogObjectsExpression.getValue();
      return !catalogObjects || catalogObjects.length === 0;
    });
    return hasFreeCapacityExpression;
  }

  protected function getReadOnlyExpression(config:*):ValueExpression {
    if (!readOnlyExpression) {
      readOnlyExpression = ValueExpressionFactory.createFromFunction(CatalogLinkFieldBase.getReadOnlyFunction(config));
    }
    return readOnlyExpression;
  }

}

}
