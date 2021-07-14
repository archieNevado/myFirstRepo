package com.coremedia.ecommerce.studio.components.search.filters {

import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ui.components.LocalComboBox;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class FacetsChooserBase extends LocalComboBox {
  [ExtConfig]
  public var facetsExpression:ValueExpression;

  [ExtConfig]
  public var selectedFacetsExpression:ValueExpression;

  private var facetListExpression:ValueExpression;
  private var disabledExpression:ValueExpression;

  public function FacetsChooserBase(config:FacetsChooser = null) {
    super(config);
    on('change', onInputChange);
    selectedFacetsExpression.addChangeListener(selectionChanged);
  }

  private function onInputChange():void {
    var value:String = getValue() || null;
    if (value) {
      var facet:Facet = FacetUtil.findFacetForKey(facetsExpression.getValue(), value);
      var selection:Array = selectedFacetsExpression.getValue();
      var updated:Array = [facet].concat(selection);
      selectedFacetsExpression.setValue(updated);
    }
  }

  private function selectionChanged(ve:ValueExpression):void {
    var value:* = ve.getValue();
    if ((!value || value.length === 0) && rendered) {
      un('change', onInputChange);
      this.clearValue();
      on('change', onInputChange);
    }
    else if(value && value.length > 0) {
      this.setValue(value);
    }
  }

  protected function getEmptyTextExpression(config:FacetsChooser):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():String {
      var selection:Array = config.selectedFacetsExpression.getValue();
      var facets:Array = config.facetsExpression.getValue();

      if(facets === undefined || selection === undefined) {
        return undefined;
      }

      if(facets.length === 0) {
        return resourceManager.getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'CollectionView_search_no_filter_allAdded_text');
      }

      if (selection && facets && selection.length === facets.length) {
        return resourceManager.getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'CollectionView_search_filter_allAdded_text');
      }

      return resourceManager.getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'CollectionView_search_filter_combo_emptyText');
    });
  }


  protected function getDisabledExpression(config:FacetsChooser):ValueExpression {
    if (!disabledExpression) {
      disabledExpression = ValueExpressionFactory.createFromFunction(function ():Boolean {
        var facets:Array = config.facetsExpression.getValue();
        return !facets || facets.length === 0;
      });
    }
    return disabledExpression;
  }

  protected function getFacetListExpression(config:FacetsChooser):ValueExpression {
    if (!facetListExpression) {
      facetListExpression = ValueExpressionFactory.createFromFunction(function ():Array {
        var categoryFacets:Array = config.facetsExpression.getValue();
        if (categoryFacets === undefined) {
          return undefined;
        }

        var updatedValues:Array = categoryFacets.filter(function (f:Facet):Boolean {
          var selection:Array = config.selectedFacetsExpression.getValue();
          return FacetUtil.findFacetForKey(selection, f.getKey()) === null;
        });

        updatedValues.sort(function (f1:Facet, f2:Facet):int {
          var l1:String = FacetUtil.localizeFacetLabel(f1.getLabel());
          var l2:String = FacetUtil.localizeFacetLabel(f2.getLabel());
          return l1.localeCompare(l2);
        });

        return updatedValues;
      });
    }
    return facetListExpression;
  }


  override protected function onDestroy():void {
    super.onDestroy();
    selectedFacetsExpression && selectedFacetsExpression.removeChangeListener(selectionChanged);
  }
}
}
