package com.coremedia.blueprint.studio.taxonomy.action {
import com.coremedia.blueprint.studio.taxonomy.chooser.TaxonomySelectionWindow;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.AccessControlUtil;
import com.coremedia.ui.actions.DependencyTrackedAction;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.util.EventUtil;

import ext.Ext;

/**
 * Shows the dialog for choosing taxonomies for a linklist property.
 */
public class OpenTaxonomyChooserActionBase extends DependencyTrackedAction {

  private var propertyValueExpression:ValueExpression;
  private var singleSelection:Boolean;

  public var bindTo:ValueExpression;
  public var forceReadOnlyValueExpression:ValueExpression;

  [ExtConfig]
  public var siteSelectionExpression:ValueExpression;

  [ExtConfig]
  public var taxonomyIdExpression:ValueExpression;

  /**
   * @param config
   */
  public function OpenTaxonomyChooserActionBase(config:OpenTaxonomyChooserAction = null) {
    propertyValueExpression = config.propertyValueExpression;
    singleSelection = config.singleSelection;
    bindTo = config.bindTo;
    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
    siteSelectionExpression = config.siteSelectionExpression;
    taxonomyIdExpression = config.taxonomyIdExpression;
    config.handler = showChooser;
    super(config);
  }


  override protected function calculateDisabled():Boolean {
    if (bindTo && bindTo.getValue() is Content && forceReadOnlyValueExpression) {
      return (bindTo.getValue() as Content).isCheckedOutByOther() || AccessControlUtil.isReadOnly(bindTo.getValue()) || forceReadOnlyValueExpression.getValue();
    }
    return false;
  }

  private function showChooser():void {
    var dialog:TaxonomySelectionWindow = Ext.getCmp(TaxonomySelectionWindow.ID) as TaxonomySelectionWindow;
    if (dialog && dialog.rendered) {
      dialog.focus();
    }
    else {
      var taxChooser:TaxonomySelectionWindow = new TaxonomySelectionWindow(TaxonomySelectionWindow({
        taxonomyIdExpression: taxonomyIdExpression,
        siteSelectionExpression: siteSelectionExpression,
        singleSelection: singleSelection,
        bindTo: bindTo,
        propertyValueExpression: propertyValueExpression
      }));
      taxChooser.show();
    }
  }

}
}
