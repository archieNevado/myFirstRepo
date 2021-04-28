package com.coremedia.livecontext.studio.forms.facets {
import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ecommerce.studio.model.FacetsImpl;
import com.coremedia.ecommerce.studio.model.SearchFacetsImpl;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.EventUtil;

import ext.data.Model;
import ext.form.field.ComboBox;
import ext.panel.Panel;

public class SingleCategoryFacetsFieldGroupBase extends Panel {

  [Bindable]
  public var bindTo:ValueExpression;

  [Bindable]
  public var forceReadOnlyValueExpression:ValueExpression;

  [Bindable]
  public var facetNamePropertyName:String;

  [Bindable]
  public var facetValuePropertyName:String;

  [Bindable]
  public var facetsExpression:ValueExpression;

  private var facetNameExpression:ValueExpression;
  private var facetValueExpression:ValueExpression;

  private var facetCombo:ComboBox;
  private var facetValueCombo:ComboBox;

  public function SingleCategoryFacetsFieldGroupBase(config:SingleCategoryFacetsFieldGroup = null) {
    facetNameExpression = config.bindTo.extendBy("properties." + config.facetNamePropertyName);
    facetValueExpression = config.bindTo.extendBy("properties." + config.facetValuePropertyName);

    super(config);
    facetCombo = itemCollection.get("facetCombo") as ComboBox;
    facetValueCombo = itemCollection.get("facetValueCombo") as ComboBox;

    //when the list of possible facets changed select the facet anew
    facetCombo.getStore().on('datachanged', function():void {
      facetNameExpression.loadValue(function():void {
        //unfortunately the combobox gets the list of possible values to late.
        //we have to give it more time so that when reverting the changes are correctly selected in the combo.
        EventUtil.invokeLater(setFacet);
      });
    });

    facetNameExpression.addChangeListener(setFacet);

    facetCombo.on('change', function():void {
      var value:String = facetCombo.getValue() || '';
      var model:Model = facetCombo.findRecord("id", value);
      if (model && value !== facetNameExpression.getValue()) {
        facetNameExpression.setValue(value);
        facetValueExpression.setValue('');
      }
    });

    //when the list of possible facet values changed select the value anew
    facetValueCombo.getStore().on('datachanged', function():void {
      facetValueExpression.loadValue(function():void {
        //unfortunately the combobox gets the list of possible values to late.
        //we have to give it more time so that when reverting the changes are correctly selected in the combo.
        EventUtil.invokeLater(function():void {
          var value:* = facetValueExpression.getValue();
          var model:Model = facetValueCombo.findRecord("id", value);
          if (model) {
            facetValueCombo.select(model);
          } else {
            facetValueCombo.clearValue();
          }
        });
      });
    });

    facetValueCombo.on('change', function():void {
      var value:String = facetValueCombo.getValue() || '';
      var model:Model = facetValueCombo.findRecord("id", value);
      if (model) {
        facetValueExpression.setValue(value);
      }
    });

    facetValueExpression.addChangeListener(setFacetValue);

    on("destroy", function():void {
      facetNameExpression.removeChangeListener(setFacet);
      facetValueExpression.removeChangeListener(setFacetValue);
    });
  }

  private function setFacet():void {
    var value:* = facetNameExpression.getValue();
    var model:Model = facetCombo.findRecord("id", value);
    if (model) {
      facetCombo.select(model);
    } else {
      facetCombo.clearValue();
    }
  }

  private function setFacetValue():void {
    var value:* = facetValueExpression.getValue();
    var model:Model = facetValueCombo.findRecord("id", value);
    if (model) {
      facetValueCombo.select(model);
    }
  }

  protected function getFacetNamesExpression(config:SingleCategoryFacetsFieldGroup):ValueExpression {
    return ValueExpressionFactory.createFromFunction(
            function ():* {
              var facets:* = config.facetsExpression.getValue();
              if (!facets) {
                return facets;
              }

              var valuesToChoose:Array = [];
              if(facets is FacetsImpl) {
                var facetsFacets:Object = facets.getFacets();
                if (!facetsFacets) {
                  return facetsFacets;
                }

                for (var key:String in facetsFacets) {
                  valuesToChoose.push({id: key, value: key});
                }
              }
              else if(facets is SearchFacetsImpl) {
                var facetList:Array = facets.getFacets();
                for each (var f:Facet in facetList) {
                  valuesToChoose.push({id: f.getLabel(), value: f.getLabel()});
                }
              }
              return valuesToChoose;
            }
    );
  }

  protected function getFacetValuesExpression(config:SingleCategoryFacetsFieldGroup):ValueExpression {
    return ValueExpressionFactory.createFromFunction(
            function ():* {
              var facets:* = config.facetsExpression.getValue();
              if (!facets) {
                return facets;
              }
              var possibleFacetSelector:Array = [];
              var selectedFacet:String = facetNameExpression.getValue();
              if (!selectedFacet) {
                return selectedFacet;
              }

              if(facets is FacetsImpl) {
                var facets2:* = facets.getFacets();
                if (facets2 === null) {
                  // facets2 loaded but empty.
                  return [];
                }
                var facetValues:Array = facets2[selectedFacet];
                if (facetValues && facetValues.length > 0) {
                  for (var i:Number = 0; i < facetValues.length; i++) {
                    possibleFacetSelector.push({id: facetValues[i].id, value: facetValues[i].value});
                  }
                }
              }
              else if(facets is SearchFacetsImpl) {
                var facetList:Array = facets.getFacets();
                for each (var f:Facet in facetList) {
                  if(f.getKey() === selectedFacet || f.getLabel() === selectedFacet) {
                    for each(var value:Object in f.getValues()) {
                      possibleFacetSelector.push({id: value.query, value: value.label});
                    }
                  }
                }
              }

              return possibleFacetSelector;
            }
    );
  }
}
}
