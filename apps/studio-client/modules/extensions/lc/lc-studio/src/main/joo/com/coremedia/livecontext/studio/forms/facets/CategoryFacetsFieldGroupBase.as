package com.coremedia.livecontext.studio.forms.facets {
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.panel.Panel;

public class CategoryFacetsFieldGroupBase extends Panel {

  [ExtConfig]
  public var bindTo:ValueExpression;

  [ExtConfig]
  public var forceReadOnlyValueExpression:ValueExpression;

  [ExtConfig]
  public var hideIssues:Boolean;

  [ExtConfig]
  public var externalIdPropertyName:String;

  [ExtConfig]
  public var structPropertyName:String;

  [ExtConfig]
  public var facetsExpression:ValueExpression;

  [ExtConfig]
  public var facetValuePropertyName:String;

  public function CategoryFacetsFieldGroupBase(config:CategoryFacetsFieldGroup = null) {
    super(config);
  }

  /**
   * A message shown in case we have selected a category that has no facets.
   * @param config
   * @return
   */
  protected function getHideNoFacetsMsgExpression(config:CategoryFacetsFieldGroup):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var searchFacets:Array = facetsExpression.getValue();
      return searchFacets && searchFacets.length > 0;
    });
  }
}
}
