package com.coremedia.livecontext.studio.forms.facets {

import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ui.data.ValueExpression;

import ext.ComponentManager;
import ext.container.Container;

public class CategoryFacetFieldBase extends Container {

  [Bindable]
  public var facet:Facet;

  [Bindable]
  public var bindTo:ValueExpression;

  [Bindable]
  public var forceReadOnlyValueExpression:ValueExpression;

  [Bindable]
  public var structPropertyName:String;

  public function CategoryFacetFieldBase(config:CategoryFacetField = null) {
    super(config);

    var xType:String = CategoryFacetComboField.xtype;
    if(config.facet.isMultiSelect()) {
      xType = CategoryFacetTagField.xtype;
    }

    var editorCfg:Object = {
      bindTo: config.bindTo,
      facet: config.facet,
      forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
      structPropertyName: config.structPropertyName,
      xtype: xType
    };

    var editor:* = ComponentManager.create(editorCfg);
    this.add(editor);
  }
}
}
