package com.coremedia.livecontext.studio.forms.facets {

import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ui.components.LocalComboBox;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class CategoryFacetComboFieldBase extends LocalComboBox {
  public static const LABEL:String = "label";
  public static const QUERY:String = "query";

  [ExtConfig]
  public var facet:Facet;

  [ExtConfig]
  public var bindTo:ValueExpression;

  [ExtConfig]
  public var forceReadOnlyValueExpression:ValueExpression;

  [ExtConfig]
  public var structPropertyName:String;

  private var facetTagsExpression:ValueExpression;
  private var multiFacetsExpression:ValueExpression;
  private var comboValuesExpression:ValueExpression;

  public function CategoryFacetComboFieldBase(config:CategoryFacetComboField = null) {
    super(config);

    multiFacetsExpression = bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME).extendBy([facet.getKey()]);
    multiFacetsExpression.addChangeListener(multiFacetsStructChanged);

    //This would also work with a BindPlugin, but we don't want to write values when they are not valid anymore
    //This ensures that we validate the persisted data before actually loading it into the editor
    facetTagsExpression = multiFacetsExpression.extendBy(CategoryFacetsPropertyFieldBase.MULTI_FACETS_QUERIES_STRUCT_NAME);

    setComboValue(facetTagsExpression);
    on('change', onInputChange);

    facetTagsExpression.addChangeListener(setComboValue);
  }

  /**
   * The value is a single string.
   * We convert it to an array to store a StringList anyway, same format as for multi facets.
   */
  private function onInputChange():void {
    var value:String = getValue() || '';
    if (value === '') {
      facetTagsExpression.setValue([]);
    } else {
      facetTagsExpression.setValue([value]);
    }
  }

  /**
   * When the data structure is reverted, the given data may not exist anymore.
   * The editor remains without being re-rendered, so we have to reset it manually.
   * @param ve the facet struct of the editor
   */
  private function multiFacetsStructChanged(ve:ValueExpression):void {
    var value:* = ve.getValue();
    if (!value && rendered) {
      un('change', onInputChange);
      this.clearValue();
      on('change', onInputChange);
    }
  }

  private function setComboValue(ve:ValueExpression):void {
    var values:Array = ve.getValue() || [];
    if(values.length > 0) {
      var value:String = values[0];
      if (isValidQuery(value)) {
        setValue(value);
      }
    } else {
      this.clearValue();
    }
  }

  private function isValidQuery(q:String):Boolean {
    for each(var value:Object in facet.getValues()) {
      if (value.query === q) {
        return true;
      }
    }
    return false;
  }

  protected function getComboValuesExpression(config:CategoryFacetComboField):ValueExpression {
    if (!comboValuesExpression) {
      comboValuesExpression = ValueExpressionFactory.createFromFunction(function ():Array {
        var result:Array = [].concat(config.facet.getValues());
        result.push({'query': '', 'label': '---'});
        return result;
      });
      return comboValuesExpression;
    }
  }

  override protected function onDestroy():void {
    super.onDestroy();
    multiFacetsExpression && multiFacetsExpression.addChangeListener(multiFacetsStructChanged);
    facetTagsExpression && facetTagsExpression.removeChangeListener(setComboValue);
  }
}
}
