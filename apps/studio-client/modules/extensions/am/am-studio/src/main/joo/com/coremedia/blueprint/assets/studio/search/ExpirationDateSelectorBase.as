package com.coremedia.blueprint.assets.studio.search {

import com.coremedia.ui.components.StatefulDateField;
import com.coremedia.ui.data.ValueExpression;

import ext.container.Container;

import net.jangaroo.ext.Exml;

[ResourceBundle('com.coremedia.blueprint.assets.studio.AMStudioPlugin')]
public class ExpirationDateSelectorBase extends Container {
  public function ExpirationDateSelectorBase(config:ExpirationDateSelectorBase = null) {
    var defaults: ExpirationDateSelectorBase = ExpirationDateSelectorBase({});
    defaults.dateKey = "byDate";
    super(Exml.apply(defaults, config));

    var dateField:StatefulDateField = StatefulDateField(down('datefield'));
    dateField.on('select', doChange);
    dateField.on('afterrender', datefieldRendered);

    selectedKeyValueExpression.addChangeListener(onSelectedKeyChange);
  }

  /**
   * The key that triggers the datefield visibility.
   */
  [ExtConfig]
  public var dateKey:String;

  /**
   * A value expression that will be bound to the selected combo box entry.
   */
  [ExtConfig]
  public var selectedKeyValueExpression:ValueExpression;

  /**
   * A value expression that will be bound to the selected date or null if no datefield is displayed.
   */
  [ExtConfig]
  public var selectedDateValueExpression:ValueExpression;

  private function onSelectedKeyChange(source:ValueExpression):void {
    if (source.getValue() === dateKey) {
      if (selectedDateValueExpression.getValue() === null) {
        selectedDateValueExpression.setValue(new Date());
      }
    } else {
      selectedDateValueExpression.setValue(null);
    }
  }

  private static function doChange(dateField:StatefulDateField):void {
    var value:* = dateField.getValue();
    dateField.fireEvent('change', dateField, value, dateField.originalValue);
    dateField.originalValue = value;
  }

  private static function datefieldRendered(dateField:StatefulDateField):void {
    dateField.getEl().on('blur', function():void {
      doChange(dateField);
    });
  }

  internal function comboboxEntryTransformer(keys:Array):Array {
    return keys.map(function (key:String):Object {
      return {
        id: key,
        name: resourceManager.getString('com.coremedia.blueprint.assets.studio.AMStudioPlugin', 'Filter_ExpirationDate_' + key + '_text') || key
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
