package com.coremedia.blueprint.assets.studio.search {

import com.coremedia.blueprint.assets.studio.AMStudioPlugin_properties;
import com.coremedia.blueprint.assets.studio.AssetConstants;
import com.coremedia.blueprint.assets.studio.config.stringListCheckboxFilterFieldset;
import com.coremedia.cms.editor.sdk.collectionview.search.*;
import com.coremedia.cms.editor.sdk.util.FormatUtil;
import com.coremedia.ui.data.ValueExpression;

import ext.Container;
import ext.config.checkbox;
import ext.config.checkboxgroup;
import ext.form.Checkbox;

public class StringListCheckboxFilterFieldsetBase extends FilterFieldset {
  private var checkboxContainer:Container;

  public function StringListCheckboxFilterFieldsetBase(config:stringListCheckboxFilterFieldset = null) {
    super(config);

    checkboxContainer = getComponent('checkboxContainer') as Container;
    availableValuesValueExpression.addChangeListener(createCheckboxes);
    createCheckboxes();
  }

  internal native function get availableValuesValueExpression():ValueExpression;
  internal native function get solrField():String;
  internal native function get propertyName():String;

  override public function buildQuery():String {
    var selectedValues:Array = getStateBean().get(getFilterId());
    if (!selectedValues || selectedValues.length === 0) {
      return '';
    }

    return FormatUtil.format('{0}:({1})', solrField || getFilterId(), selectedValues.join(" AND "));
  }

  override public function getDefaultState():Object {
    var state:Object = {};
    state[getFilterId()] = [];
    return state;
  }

  private function createCheckboxes():void {
    var availableValues:Array = availableValuesValueExpression.getValue();
    if (!availableValues || availableValues.length === 0) {
      return;
    }

    var checkboxConfigs:Array = availableValues.map(function (availableValue:String):checkbox {
      var propertyKey:String = 'Asset_' + AssetConstants.PROPERTY_ASSET_METADATA  + '_'+ (propertyName || getFilterId()) + '_' + availableValue + '_text';
      var checkboxConfig:checkbox = new checkbox();
      checkboxConfig.boxLabel = AMStudioPlugin_properties.INSTANCE[propertyKey] || availableValue;
      checkboxConfig.name = availableValue;
      checkboxConfig.itemId = availableValue;
      checkboxConfig.hideLabel = true;
      return checkboxConfig;
    });

    var checkboxGroupConfig:checkboxgroup = new checkboxgroup();
    checkboxGroupConfig.items = checkboxConfigs;

    checkboxContainer.removeAll();
    checkboxContainer.add(checkboxGroupConfig);

    doLayout();
  }

  internal function transformer(strings:Array):Object {
    var valueObject:Object = {};
    var i:int;

    for (i = 0; i < strings.length; i++) {
      valueObject[strings[i]] = true;
    }

    var toBeUnchecked:Array = availableValuesValueExpression.getValue().filter(function (availableValue:String):Boolean {
      return strings.indexOf(availableValue) === -1;
    });
    for (i = 0; i < toBeUnchecked.length; i++) {
      valueObject[toBeUnchecked[i]] = false;
    }

    return valueObject;
  }

  internal function reverseTransformer(selectedCheckboxes:Array):Array {
    return selectedCheckboxes.map(function (component:Checkbox):String {
      return component.getName();
    });
  }
}
}
