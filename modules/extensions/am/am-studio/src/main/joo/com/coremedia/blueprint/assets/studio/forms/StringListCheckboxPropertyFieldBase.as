package com.coremedia.blueprint.assets.studio.forms {

import com.coremedia.blueprint.assets.studio.AMStudioPlugin_properties;
import com.coremedia.blueprint.assets.studio.config.stringListCheckboxPropertyField;
import com.coremedia.cms.editor.sdk.config.propertyFieldPlugin;
import com.coremedia.cms.editor.sdk.config.showIssuesPlugin;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;

import ext.Container;
import ext.config.checkbox;

public class StringListCheckboxPropertyFieldBase extends Container {
  private var propertyValueExpression:ValueExpression;
  private var structValueExpression:ValueExpression;

  private var checkboxGroup:CheckboxGroup;

  public function StringListCheckboxPropertyFieldBase(config:stringListCheckboxPropertyField = null) {
    super(config);

    checkboxGroup = getComponent('checkboxGroup') as CheckboxGroup;
    checkboxGroup.on('change', onComponentChange);
    getPropertyValueExpression().addChangeListener(onStructChange);
  }

  internal native function get bindTo():ValueExpression;
  internal native function get structName():String;
  internal native function get propertyName():String;
  internal native function get availableValuesValueExpression():ValueExpression;

  internal native function get hideIssues():Boolean;

  private function getPropertyValueExpression():ValueExpression {
    if (!propertyValueExpression) {
      propertyValueExpression = bindTo.extendBy('properties', structName, propertyName);
    }
    return propertyValueExpression;
  }

  private function getStructValueExpression():ValueExpression {
    if (!structValueExpression) {
      structValueExpression = bindTo.extendBy('properties', structName);
    }
    return structValueExpression;
  }

  internal function computeCheckboxConfigs():Object {
    var availableValues:Array = availableValuesValueExpression.getValue();
    var struct:RemoteBean = getStructValueExpression().getValue() as RemoteBean;

    // make sure everything is loaded
    if (availableValues === undefined || struct === undefined) {
      return undefined;
    }
    if (struct && !struct.isLoaded()) {
      struct.load();
      return undefined;
    }

    // extend available values with values stored in the struct (if any)
    var valuesStoredInContent:Array = getPropertyValueExpression().getValue();
    if (valuesStoredInContent) {
      var valuesOnlyStoredInContent:Array = valuesStoredInContent.filter(function (value:String):Boolean {
        return availableValues.indexOf(value) === -1;
      });
      availableValues = availableValues.concat(valuesOnlyStoredInContent);
    }

    var checkboxConfigs:Array = availableValues.map(function (availableValue:String):checkbox {
      var propertyKey:String = 'Asset_' + structName + '_' + propertyName + '_' + availableValue + '_text';
      var checked:Boolean = valuesStoredInContent && valuesStoredInContent.indexOf(availableValue) !== -1;

      var checkboxConfig:checkbox = new checkbox();
      checkboxConfig.boxLabel = AMStudioPlugin_properties.INSTANCE[propertyKey] || availableValue;
      checkboxConfig.name = availableValue;
      checkboxConfig.itemId = availableValue;
      checkboxConfig.checked = checked;
      var propertyFieldPluginConfig:propertyFieldPlugin = new propertyFieldPlugin();
      propertyFieldPluginConfig.propertyName = structName + '.' + propertyName + '.' + availableValue;
      var showIssuesPluginConfig:showIssuesPlugin = new showIssuesPlugin();
      showIssuesPluginConfig.bindTo = bindTo;
      showIssuesPluginConfig.hideIssues = hideIssues;
      showIssuesPluginConfig.propertyName = structName + '.' + propertyName + '.' + availableValue;
      checkboxConfig.plugins = [
        propertyFieldPluginConfig,
        showIssuesPluginConfig
      ];
      return checkboxConfig;
    });

    return checkboxConfigs;
  }

  private function onComponentChange():void {
    getPropertyValueExpression().removeChangeListener(onStructChange);
    getPropertyValueExpression().setValue(checkboxGroup.getValue());
    getPropertyValueExpression().addChangeListener(onStructChange);
  }

  private function onStructChange():void {
    checkboxGroup.un('change', onComponentChange);
    checkboxGroup.setValue(getPropertyValueExpression().getValue());
    checkboxGroup.on('change', onComponentChange);
  }

}
}
