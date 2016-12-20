package com.coremedia.blueprint.studio.taxonomy.action {
import com.coremedia.blueprint.studio.taxonomy.chooser.TaxonomySelectionWindow;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Ext;

/**
 * Shows the dialog for choosing taxonomies for a linklist property.
 */
public class OpenTaxonomyChooserActionBase extends Action {

  private var propertyValueExpression:ValueExpression;
  private var taxId:String;
  private var singleSelection:Boolean;

  /**
   * @param config
   */
  public function OpenTaxonomyChooserActionBase(config:OpenTaxonomyChooserAction = null) {
    propertyValueExpression = config.propertyValueExpression;
    singleSelection = config.singleSelection;
    taxId = config.taxonomyId;
    super(Action(Ext.apply({
      handler: showChooser
    }, config)));
  }

  private function showChooser():void {
    var taxChooser:TaxonomySelectionWindow = new TaxonomySelectionWindow(TaxonomySelectionWindow({
      taxonomyId: taxId,
      singleSelection: singleSelection,
      propertyValueExpression: propertyValueExpression
    }));
    taxChooser.show();
  }

}
}
