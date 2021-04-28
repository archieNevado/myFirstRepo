package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.mixins.IHidableMixin;

import ext.form.FieldContainer;

/**
 * Base class for the taxonomy property editor.
 * The class is used to disable the suggestion panel if they are not required.
 */
public class TaxonomyPropertyFieldBase extends FieldContainer implements IHidableMixin {

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

  /** @private */
  [Bindable]
  public function set hideText(newHideText:String):void {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  [Bindable]
  public function get hideText():String {
    return getFieldLabel();
  }

  /** @private */
  /** @inheritDoc */
  [Bindable]
  public native function set hideId(newHideId:String):void;

  /** @inheritDoc */
  [Bindable]
  public native function get hideId():String;
}
}
