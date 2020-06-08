package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.form.FieldContainer;

/**
 * Base class for the taxonomy property editor.
 * The class is used to disable the suggestion panel if they are not required.
 */
public class TaxonomyPropertyFieldBase extends FieldContainer {

  private var disableSuggestions:Boolean;

  public function TaxonomyPropertyFieldBase(config:TaxonomyPropertyField = null) {
    disableSuggestions = config.disableSuggestions;
    super(config);
  }


  override protected function initComponent():void {
    super.initComponent();
    if(disableSuggestions) {
      queryById('suggestionsPanel').hide();
    }
  }

  protected function getTaxonomyIdExpr(config:TaxonomyPropertyField):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():String {
      if(config.taxonomyId) {
        return config.taxonomyId;
      }

      return config.taxonomyIdExpression.getValue();
    });
  }
}
}
