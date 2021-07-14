package com.coremedia.livecontext.studio.forms.facets {

import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cms.editor.sdk.components.ChipsField.InputChipsFieldBase;
import com.coremedia.ecommerce.studio.model.Facet;
import com.coremedia.ui.data.ValueExpression;

public class CategoryFacetTagFieldBase extends InputChipsFieldBase {
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

  public function CategoryFacetTagFieldBase(config:CategoryFacetTagField = null) {
    super(config);

    multiFacetsExpression = bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME).extendBy([facet.getKey()]);
    multiFacetsExpression.addChangeListener(multiFacetsStructChanged);

    //This would also work with a BindPlugin, but we don't want to write values when they are not valid anymore
    //This ensures that we validate the persisted data before actually loading it into the editor
    facetTagsExpression = multiFacetsExpression.extendBy(CategoryFacetsPropertyFieldBase.MULTI_FACETS_QUERIES_STRUCT_NAME);

    setTagValues(facetTagsExpression);
    on('change', onInputChange);

    facetTagsExpression.addChangeListener(setTagValues);
  }

  private function onInputChange():void {
    var values:Array = getValue() || [];
    facetTagsExpression.setValue(values);
  }

  /**
   * When the data structure is reverted to another version or reverted, the given data may not exist anymore.
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

  private function setTagValues(ve:ValueExpression):void {
    var values:Array = ve.getValue();
    if (allValuesAreValid(values)) {
      setValue(values);
    }
  }

  private function allValuesAreValid(queryValues:Array):Boolean {
    if (!queryValues) {
      return false;
    }

    for each(var q:String in queryValues) {
      if (!isValidQuery(q)) {
        return false;
      }
    }
    return true;
  }

  private function isValidQuery(q:String):Boolean {
    for each(var value:Object in facet.getValues()) {
      if (value.query === q) {
        return true;
      }
    }
    return false;
  }

  override protected function onDestroy():void {
    super.onDestroy();
    multiFacetsExpression && multiFacetsExpression.addChangeListener(multiFacetsStructChanged);
    facetTagsExpression && facetTagsExpression.removeChangeListener(setTagValues);
  }
}
}
