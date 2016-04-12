package com.coremedia.blueprint.assets.studio.search {

import com.coremedia.blueprint.assets.studio.AMStudioPlugin_properties;
import com.coremedia.blueprint.assets.studio.config.expirationDateSelector;
import com.coremedia.ui.data.ValueExpression;

import ext.Container;
import ext.form.DateField;

public class ExpirationDateSelectorBase extends Container {
  public function ExpirationDateSelectorBase(config:expirationDateSelector = null) {
    super(config);

    var dateField:DateField = findByType('datefield')[0];
    dateField.on('select', doChange);
    dateField.on('afterrender', datefieldRendered);

    selectedKeyValueExpression.addChangeListener(onSelectedKeyChange);
  }

  internal native function get selectedKeyValueExpression():ValueExpression;

  internal native function get selectedDateValueExpression():ValueExpression;

  internal native function get dateKey():ValueExpression;

  private function onSelectedKeyChange(source:ValueExpression):void {
    if (source.getValue() === dateKey) {
      if (selectedDateValueExpression.getValue() === null) {
        selectedDateValueExpression.setValue(new Date());
      }
    } else {
      selectedDateValueExpression.setValue(null);
    }
  }

  private static function doChange(dateField:DateField):void {
    var value:* = dateField.getValue();
    dateField.fireEvent('change', dateField, value, dateField.startValue);
    dateField.startValue = value;
  }

  private static function datefieldRendered(dateField:DateField):void {
    dateField.getEl().on('blur', function():void {
      doChange(dateField);
    });
  }

  internal function comboboxEntryTransformer(keys:Array):Object {
    return keys.map(function (key:String):Object {
      return {
        id: key,
        name: AMStudioPlugin_properties.INSTANCE['Filter_ExpirationDate_' + key + '_text'] || key
      };
    });
  }

  internal function datefieldVisibilityTransformer(code:String):Boolean {
    return code === dateKey;
  }

  override protected function onDestroy():void {
    selectedKeyValueExpression.removeChangeListener(onSelectedKeyChange);
    super.onDestroy();
  }
}
}
