package com.coremedia.ecommerce.studio.components.search.filters {
import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ui.components.panel.CollapsiblePanel;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.models.bem.BEMBlock;
import com.coremedia.ui.models.bem.BEMElement;
import com.coremedia.ui.models.bem.BEMModifier;

import ext.ComponentManager;

public class FacetFilterFieldWrapperBase extends CollapsiblePanel {
  public static const BLOCK:BEMBlock = new BEMBlock("cm-filter-panel");
  public static const ELEMENT_BODY:BEMElement = BLOCK.createElement("body");
  public static const ELEMENT_HEADER:BEMElement = BLOCK.createElement("header");
  public static const ELEMENT_REMOVE:BEMElement = BLOCK.createElement("remove");
  public static const MODIFIER_CUSTOMIZED:BEMModifier = BLOCK.createModifier("customized");

  [ExtConfig]
  public var facet:Facet;

  [ExtConfig]
  public var stateBean:Bean;

  [ExtConfig]
  public var removeHandler:Function;

  private var facetExpression:ValueExpression;
  private var selectedFacetValuesExpression:ValueExpression;
  private var modifierVE:ValueExpression;

  public function FacetFilterFieldWrapperBase(config:FacetFilterFieldWrapper = null) {
    super(config);

    var xType:String = FacetComboField.xtype;
    if(config.facet.isMultiSelect()) {
      xType = FacetTagField.xtype;
    }

    var editorCfg:Object = {
      facetValueExpression: getFacetExpression(config),
      selectedFacetValuesExpression: getSelectedFacetValuesExpression(config),
      xtype: xType
    };

    var editor:* = ComponentManager.create(editorCfg);
    this.add(editor);
  }

  public function reset():void {
    getSelectedFacetValuesExpression().setValue([]);
  }

  protected function getFacetExpression(config:FacetFilterFieldWrapper):ValueExpression {
    if(!facetExpression) {
      facetExpression = ValueExpressionFactory.createFromValue(config.facet);
    }
    return facetExpression;
  }

  public function getSelectedFacetValuesExpression(config:FacetFilterFieldWrapper = null):ValueExpression {
    if(!selectedFacetValuesExpression) {
      selectedFacetValuesExpression = ValueExpressionFactory.create(config.facet.getKey(), config.stateBean);
      if(!selectedFacetValuesExpression.getValue()) {
        selectedFacetValuesExpression.setValue([]);
      }
    }
    return selectedFacetValuesExpression;
  }

  protected function getModifierVE():ValueExpression {
    if (!modifierVE) {
      modifierVE = ValueExpressionFactory.createFromFunction(function ():Array {
        var facets:Array = getSelectedFacetValuesExpression().getValue();
        if(facets === undefined) {
          return [];
        }

        if (facets  .length === 0) {
          return [];
        } else {
          return [MODIFIER_CUSTOMIZED.getIdentifier()];
        }
      });
    }
    return modifierVE;
  }

  protected function removeFilter():void {
    removeHandler(this.facet);
  }

  protected function formatItemId(facet:Facet):String {
    var id:String = facet.getKey();
    return id.replace(/\\/g, "-").replace(/\./g,'-');
  }
}
}
