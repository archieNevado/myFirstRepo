package com.coremedia.ecommerce.studio.components.search.filters {

import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.collectionview.search.*;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ecommerce.studio.model.SearchFacets;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.container.Container;
import ext.panel.Panel;

[ResourceBundle('com.coremedia.ecommerce.studio.ECommerceStudioPlugin')]
public class FacetsFilterPanelBase extends Panel implements SearchFilter {
  private var categoryExpression:ValueExpression;

  private var facetsExpression:ValueExpression;
  private var selectedFacetsExpression:ValueExpression;
  private var activeStateExpression:ValueExpression;
  private var stateBean:Bean;

  /**
   * The filter ID for this filter. It is used as itemId and identifier in saved searches.
   */
  [ExtConfig]
  public var filterId:String;

  public function FacetsFilterPanelBase(config:FacetsFilterPanel = null) {
    super(config);
  }

  override protected function afterRender():void {
    super.afterRender();
    getFacetsExpression().addChangeListener(facetsChanged);
  }

  public function getStateBean():Bean {
    if (!stateBean) {
      stateBean = new FacetFilterStateBean();
    }
    return stateBean;
  }

  private function getFacetFilterStateBean():FacetFilterStateBean {
    return getStateBean() as FacetFilterStateBean;
  }

  public function getFilterId():String {
    return getItemId();
  }

  public function transformState(state:Object):Object {
    var updatedFacets:Array = [];
    getFacetsExpression().loadValue(function(facets:Array):void {
      for (var m:String in state) {
        var facet:Facet = FacetUtil.findFacetForKey(facets, m);
        if(facet) {
          updatedFacets.push(facet);
          getStateBean().set(m, state[m]);
        }
        else {
          trace('[WARN]', 'Could not find search facet ' + m + ' to restore filter state.');
        }
      }
      getSelectedFacetsExpression().setValue(updatedFacets);
    });
  }

  public function getDefaultState():Object {
    var state:Object = {};
    return state;
  }

  private function getCategoryExpression():ValueExpression {
    if (!categoryExpression) {
      categoryExpression = ValueExpressionFactory.create(CollectionViewModel.FOLDER_PROPERTY, CollectionViewModel.lookupCollectionViewModel(this).getMainStateBean());
    }
    return categoryExpression;
  }

  protected function getSelectedFacetsExpression():ValueExpression {
    if (!selectedFacetsExpression) {
      selectedFacetsExpression = ValueExpressionFactory.createFromValue([]);
      selectedFacetsExpression.addChangeListener(selectedFacetsChanged);
    }
    return selectedFacetsExpression;
  }

  /**
   * Remove filters from state that are not selected anymore.
   * @param ve
   */
  private function selectedFacetsChanged(ve:ValueExpression):void {
    var facetSelection:Array = ve.getValue();
    var state:Object = getStateBean().toObject();
    for (var m:String in state) {
      var facet:Facet = FacetUtil.findFacetForKey(facetSelection, m);
      if(!facet) {
        getFacetFilterStateBean().remove(m);
      }
    }
  }

  public function buildQuery():String {
    //not used, we access this filter state directly instead
    return "";
  }

  protected function getActiveStateExpression():ValueExpression {
    if (!activeStateExpression) {
      activeStateExpression = ValueExpressionFactory.createFromFunction(function():String {
        var selection:Array = getSelectedFacetsExpression().getValue();
        var facets:Array = getFacetsExpression().getValue();

        if(selection && selection.length > 0) {
          return FacetsFilterPanel.FILTER_FACETS_ITEM_ID;
        }

        if(facets && facets.length === 0) {
          return FacetsFilterPanel.DISABLED_ITEM_ID;
        }

        return FacetsFilterPanel.EMPTY_ITEM_ID;
      });
    }
    return activeStateExpression;
  }

  protected function getFacetsExpression():ValueExpression {
    if (!facetsExpression) {
      facetsExpression = ValueExpressionFactory.createFromFunction(function ():Array {
        //the value can be a store or other node elements too!
        var category:Category = getCategoryExpression().getValue() as Category;
        if (!category) {
          return [];
        }

        if (!category.isLoaded()) {
          category.load();
          return undefined;
        }

        var searchFacets:SearchFacets = category.getSearchFacets();
        if (searchFacets === null) {
          return [];
        }

        if (!searchFacets.isLoaded()) {
          searchFacets.load();
          return undefined;
        }
        var categoryFacets:Object = category.getSearchFacets().getFacets();
        if (categoryFacets === undefined) {
          return undefined;
        }

        return categoryFacets || [];
      });
    }
    return facetsExpression;
  }

  protected function resetAllFilters():void {
    var container:Container = queryById('facetsContainer') as Container;
    container.itemCollection.each(function (f:FacetFilterFieldWrapper):void {
      f.reset();
    });
  }

  protected function removeFromSelection(removedFacet:Facet):void {
    var selection:Array = getSelectedFacetsExpression().getValue();
    var updated:Array = selection.filter(function (f:Facet):Boolean {
      return removedFacet.getKey() !== f.getKey();
    });
    getSelectedFacetsExpression().setValue(updated);
  }

  protected function emptyTransformer(values:Array):Boolean {
    return !values || values.length === 0;
  }

  private function facetsChanged(ve:ValueExpression):void {
    var updatedFacets:Array = ve.getValue();
    if(updatedFacets === undefined) {
      return;
    }

    var selectedFacets:Array = getSelectedFacetsExpression().getValue();
    var filtered:Array = selectedFacets.filter(function(f:Facet):Boolean {
      for each(var facet:Facet in updatedFacets) {
        if(f.getKey() === facet.getKey()) {
          return true;
        }
      }
      return false;
    });
    getSelectedFacetsExpression().setValue(filtered);
  }

  override protected function onDestroy():void {
    getFacetsExpression().removeChangeListener(facetsChanged);
    getSelectedFacetsExpression().removeChangeListener(selectedFacetsChanged);

    super.onDestroy();
  }
}
}
