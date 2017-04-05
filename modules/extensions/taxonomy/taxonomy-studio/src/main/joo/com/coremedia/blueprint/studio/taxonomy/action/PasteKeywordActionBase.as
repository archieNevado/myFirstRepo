package com.coremedia.blueprint.studio.taxonomy.action {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.administration.TaxonomyExplorerPanel;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Component;
import ext.Ext;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin')]
public class PasteKeywordActionBase extends Action {

  private var selectionExpression:ValueExpression;
  private var clipboardValueExpression:ValueExpression;
  internal native function get items():Array;

  public function PasteKeywordActionBase(config:PasteKeywordAction = null) {
    config.handler = pasteNode;
    config.text = ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomyExplorerPanel_paste_button_label');
    config.disabled = true;
    super(config);
    clipboardValueExpression = config.clipboardValueExpression;
    selectionExpression = config.selectionExpression;
    selectionExpression.addChangeListener(updateDisabled);
    clipboardValueExpression.addChangeListener(updateDisabled);
  }

  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    updateDisabled();
  }

  private function updateDisabled():void {
    var selection:TaxonomyNode = selectionExpression.getValue();
    setDisabled(!selection ||
      !clipboardValueExpression.getValue() ||
      (selection.getTaxonomyId() !== clipboardValueExpression.getValue().getTaxonomyId()) ||
      clipboardValueExpression.getValue().getRef() === selection.getRef());
  }


  /**
   * Copies the cutted node as a child of the selected node.
   */
  protected function pasteNode():void {
    var targetNode:TaxonomyNode = selectionExpression.getValue();
    var sourceNode:TaxonomyNode = clipboardValueExpression.getValue();
    if (sourceNode && targetNode) {
      var taxonomyExplorer:TaxonomyExplorerPanel = Ext.getCmp('taxonomyExplorerPanel') as TaxonomyExplorerPanel;
      taxonomyExplorer.moveNode(sourceNode, targetNode);
    }
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      selectionExpression && selectionExpression.removeChangeListener(updateDisabled);
      clipboardValueExpression && clipboardValueExpression.removeChangeListener(updateDisabled);
    }
  }
}
}