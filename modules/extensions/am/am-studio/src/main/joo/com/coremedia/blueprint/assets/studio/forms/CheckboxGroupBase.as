package com.coremedia.blueprint.assets.studio.forms {

import com.coremedia.blueprint.assets.studio.config.checkboxGroup;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;

import ext.Component;
import ext.Container;
import ext.form.Checkbox;

public class CheckboxGroupBase extends Container {
  private static const CHECK_EVENT:String = 'check';
  private static const CHANGE_EVENT:String = 'change';
  private static const CHECKED_PROPERTY:String = 'checked';

  private var model:Bean = beanFactory.createLocalBean({checked: []});
  private var effectiveReadOnlyExpression:ValueExpression;

  public function CheckboxGroupBase(config:checkboxGroup = null) {
    super(config);

    on('add', itemAdded);
    on('remove', itemRemoved);

    var that:CheckboxGroupBase = this;
    items.each(function (item:Checkbox, index:Number):void {
      itemAdded(that, item, index);
    });

    model.addPropertyChangeListener(CHECKED_PROPERTY, onModelChange);

    effectiveReadOnlyExpression = PropertyEditorUtil.createReadOnlyValueExpression(
            config.bindTo,
            config.forceReadOnlyValueExpression);
    effectiveReadOnlyExpression.addChangeListener(onReadOnlyChange);
  }


  private function itemAdded(container:Container, item:Component, index:Number):void {
    if (model.get(CHECKED_PROPERTY).indexOf(item.getItemId()) !== -1) {
      Checkbox(item).setValue(true);
    }

    var readOnly:Boolean = effectiveReadOnlyExpression && effectiveReadOnlyExpression.getValue();
    if (readOnly) {
      item.setDisabled(readOnly);
    }

    mon(item, CHECK_EVENT, onComponentChange);
  }

  private function itemRemoved(container:Container, item:Component):void {
    mun(item, CHECK_EVENT, onComponentChange);
  }

  private function onComponentChange(checkbox:Checkbox, checked:Boolean):void {
    var checkedItems:Array = [];

    items.each(function (item:Checkbox):void {
      if (item.checked) {
        checkedItems.push(item.getItemId());
      }
    });

    model.set(CHECKED_PROPERTY, checkedItems);
  }

  private function onModelChange():void {
    var checked:Array = model.get(CHECKED_PROPERTY);

    items.each(function (item:Checkbox):void {
      mun(item, CHECK_EVENT, onComponentChange);

      if (checked.indexOf(item.getItemId()) !== -1) {
        item.setValue(true);
      } else {
        item.setValue(false);
      }

      mon(item, CHECK_EVENT, onComponentChange);
    });

    fireEvent(CHANGE_EVENT, getValue());
  }

  private function onReadOnlyChange():void {
    var readOnly:Boolean = effectiveReadOnlyExpression.getValue();

    items.each(function (item:Checkbox):void {
      item.setDisabled(readOnly);
    });
  }

  public function getValue():Array {
    return model.get(CHECKED_PROPERTY);
  }

  public function setValue(checkedItems:Array):void {
    model.set(CHECKED_PROPERTY, checkedItems || []);
  }
}
}
