package com.coremedia.livecontext.asset.studio.action {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.livecontext.asset.studio.config.inheritReferencesAction;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.ui.actions.DependencyTrackedToggleAction;
import com.coremedia.ui.data.ValueExpression;

import ext.Ext;

public class InheritReferencesAction extends DependencyTrackedToggleAction {

  private static const PROPERTIES:String = 'properties';
  private static const LOCAL_SETTINGS_STRUCT_NAME:String = 'localSettings';
  private static const COMMERCE_STRUCT_NAME:String = 'commerce';
  private static const INHERIT_PROPERTY_NAME:String = 'inherit';

  private var bindTo:ValueExpression;
  private var inheritExpression:ValueExpression;
  private var originReferencesExpression:ValueExpression;
  private var referencesExpression:ValueExpression;
  private var references:Array = [];
  private var forceReadOnlyValueExpression:ValueExpression;


  public function InheritReferencesAction(config:inheritReferencesAction = null) {
    // Copy values before super constructor call for calculateDisable.
    bindTo = config.bindTo;
    inheritExpression = config.inheritExpression || config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, COMMERCE_STRUCT_NAME, INHERIT_PROPERTY_NAME);
    originReferencesExpression = config.originReferencesExpression || config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, COMMERCE_STRUCT_NAME, CatalogHelper.ORIGIN_REFERENCES_LIST_NAME);
    referencesExpression = config.referencesExpression || config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, COMMERCE_STRUCT_NAME, CatalogHelper.REFERENCES_LIST_NAME);

    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;

    super(inheritReferencesAction(Ext.apply({
      iconCls: LivecontextStudioPlugin_properties.INSTANCE.InheritReferencesAction_icon,
      text: LivecontextStudioPlugin_properties.INSTANCE.InheritReferencesAction_text,
      tooltip: LivecontextStudioPlugin_properties.INSTANCE.InheritReferencesAction_tooltip,
      tooltipPressed:LivecontextStudioPlugin_properties.INSTANCE.InheritReferencesAction_tooltipPressed
    }, config)));
  }

  override protected function handleUnpress():void {
    inheritExpression.setValue(false);

    //restore the temporarily stored catalog object list
    //but only if the catalog object list is not empty
    if (references && references.length > 0) {
      referencesExpression.setValue(references);
    }
  }


  override protected function handlePress():void {
    inheritExpression.setValue(true);

    //we are going to override the catalog object list with original value
    //we want to restore the catalog object list when the button is unpressed
    //so store the catalog object list before copying the original catalog object List
    referencesExpression.loadValue(function():void {
      references = referencesExpression.getValue() || [];

    });

    //set the catalog object list to the origin catalog object list directly
    //before the value of the originReferencesExpression is loaded to a non-undefined value
    referencesExpression.loadValue(function():void {
      referencesExpression.setValue(originReferencesExpression.getValue() || []);
    });

    originReferencesExpression.loadValue(function():void {
      //check if we are in inherit mode.
      //when this asynchronous callback is called the inherit could be set to false before.
      if (inheritExpression.getValue()){
        referencesExpression.setValue(originReferencesExpression.getValue());
      }
    });
  }

  override protected function calculateDisabled():Boolean {
    if (forceReadOnlyValueExpression && forceReadOnlyValueExpression.getValue()) {
      return true;
    }
    var formContent:Content = bindTo.getValue();
    if (formContent === undefined) {
      return undefined;
    }
    var readOnly:Boolean = PropertyEditorUtil.isReadOnly(formContent);
    if (readOnly !== false) {
      return readOnly;
    }

    return false;
  }

  override protected function calculatePressed():Boolean {
    return !!inheritExpression.getValue();
  }
}
}
