package com.coremedia.livecontext.studio.forms.facets {
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.panel.Panel;

public class CategoryFacetsFieldGroupBase extends Panel {

  [Bindable]
  public var bindTo:ValueExpression;

  [Bindable]
  public var forceReadOnlyValueExpression:ValueExpression;

  [Bindable]
  public var hideIssues:Boolean;

  [Bindable]
  public var externalIdPropertyName:String;

  [Bindable]
  public var structPropertyName:String;

  [Bindable]
  public var facetsExpression:ValueExpression;

  [Bindable]
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
