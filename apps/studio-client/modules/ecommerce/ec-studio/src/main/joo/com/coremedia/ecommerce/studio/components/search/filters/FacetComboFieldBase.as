package com.coremedia.ecommerce.studio.components.search.filters {
import com.coremedia.ui.components.LocalComboBox;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class FacetComboFieldBase extends LocalComboBox {
  public static const LABEL:String = "label";
  public static const QUERY:String = "query";

  [ExtConfig]
  public var facetValueExpression:ValueExpression;

  [ExtConfig]
  public var selectedFacetValuesExpression:ValueExpression;

  public var comboValuesExpression:ValueExpression;

  public function FacetComboFieldBase(config:FacetComboFieldBase = null) {
    super(config);
    on('change', onInputChange);
    selectedFacetValuesExpression.addChangeListener(selectionChanged);
  }

  override protected function afterRender():void {
    super.afterRender();
    selectionChanged(selectedFacetValuesExpression);
  }

  protected function getComboValuesExpression(config:FacetComboField):ValueExpression {
    if (!comboValuesExpression) {
      comboValuesExpression = ValueExpressionFactory.createFromFunction(function ():Array {
        var result:Array = [].concat(config.facetValueExpression.getValue().getValues());
        result.push({'query': '', 'label': '---'});
        return result;
      });
      return comboValuesExpression;
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

  /**
   * The value is a single string.
   * We convert it to an array to store a StringList anyway, same format as for multi facets.
   */
  private function onInputChange():void {
    var value:String = getValue() || '';
    if (value === '') {
      selectedFacetValuesExpression.setValue([]);
    } else {
      selectedFacetValuesExpression.setValue([value]);
    }
  }


  override protected function onDestroy():void {
    super.onDestroy();
    selectedFacetValuesExpression && selectedFacetValuesExpression.removeChangeListener(selectionChanged);
  }
}
}
