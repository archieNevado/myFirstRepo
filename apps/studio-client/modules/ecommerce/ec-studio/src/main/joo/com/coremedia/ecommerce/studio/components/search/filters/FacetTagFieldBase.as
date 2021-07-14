package com.coremedia.ecommerce.studio.components.search.filters {
import com.coremedia.cms.editor.sdk.components.ChipsField.InputChipsFieldBase;
import com.coremedia.ui.data.ValueExpression;

public class FacetTagFieldBase extends InputChipsFieldBase {
  public static const LABEL:String = "label";
  public static const QUERY:String = "query";

  [ExtConfig]
  public var facetValueExpression:ValueExpression;

  [ExtConfig]
  public var selectedFacetValuesExpression:ValueExpression;

  public function FacetTagFieldBase(config:FacetTagFieldBase = null) {
    super(config);
  }
}
}
