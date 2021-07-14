package com.coremedia.blueprint.studio.taxonomy.chooser {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeFactory;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.components.StudioDialog;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

/**
 * The base class of the taxonomy selection window.
 */
[ResourceBundle('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin')]
public class TaxonomySelectionWindowBase extends StudioDialog {

  private var selectionExpression:ValueExpression;
  private var propertyValueExpression:ValueExpression;
  private var searchResultExpression:ValueExpression;
  private var loadingExpression:ValueExpression;
  private var nodePathExpression:ValueExpression;
  private var singleSelection:Boolean;

  [ExtConfig]
  public var taxonomyIdExpression:ValueExpression;

  public function TaxonomySelectionWindowBase(config:TaxonomySelectionWindow = null) {
    super(config);
    singleSelection = config.singleSelection;
    propertyValueExpression = config.propertyValueExpression;

    var selection:Array = [];
    var value:* = propertyValueExpression.getValue();
    if (value) {
      if (singleSelection && value as Content) {
        selection.push(value as Content);
      }
      else {
        selection = value as Array;
      }
    }

    getSelectionExpression().setValue(selection);
  }

  protected function resolveTitle(config:TaxonomySelectionWindow):String {
    var title:String = resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'taxonomy_selection_dialog_title');
    var taxonomyId:String = config.taxonomyIdExpression.getValue();
    return resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomyType_' + taxonomyId + '_text') || title;
  }

  protected function getLoadingExpression():ValueExpression {
    if(!loadingExpression) {
      loadingExpression = ValueExpressionFactory.createFromValue(false);
    }
    return loadingExpression;
  }

  protected function getSearchResultExpression():ValueExpression {
    if(!searchResultExpression) {
      searchResultExpression = ValueExpressionFactory.createFromValue(null);
      searchResultExpression.addChangeListener(searchChanged);
    }
    return searchResultExpression;
  }

  protected function getNodePathExpression():ValueExpression {
    if (!nodePathExpression) {
      nodePathExpression = ValueExpressionFactory.create("nodeRef", beanFactory.createLocalBean());
    }
    return nodePathExpression;
  }

  /**
   * This callback is invoked after the "-" button has been clicked of a node inside the taxonomy link list.
   * We need this callback to check if the corresponding node is rendered in the letter list panel as well and therefore
   * needs a refresh.
   * @param nodeRef the reference of the node that has been removed from the taxonomy link list
   */
  protected function removedFromLinkListCallback(nodeRef:String):void {
    var content:Content = WorkArea.ACTIVE_CONTENT_VALUE_EXPRESSION.getValue();
    var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
    TaxonomyNodeFactory.loadPath(taxonomyIdExpression.getValue(), nodeRef, siteId, function(list:TaxonomyNodeList):void {
      if(isInActiveNodeList(list)) {
        getLetterListPanel().updateUI();
      }
    });
  }

  /**
   * The callback once a selection is made via the search field.
   * An additional computation is made here to check if the selected node
   * is on the same level currently selected in the letter list panel.
   * We only refresh the panel to update the +/- button of the node then.
   *
   * @param ve the value expression that contains the selected node list
   */
  private function searchChanged(ve:ValueExpression):void {
    var nodeList:TaxonomyNodeList = ve.getValue();
    var doRefresh:Boolean = isInActiveNodeList(nodeList);

    //reset previous selection if the dialog is in single selection mode
    if(singleSelection) {
      getSelectionExpression().setValue([]);
    }

    var selectedNodeRef:String = ve.getValue().getLeafRef();
    updateSelection(selectedNodeRef, false, doRefresh);
  }

  /**
   * Do only refresh if the removed or added node is part of the current path selected in the letter list panel.
   * If the dialog is operating in the single selection mode, we always refresh the nodes since the +/- buttons visibility does toggle.
   *
   * @param nodeList the node list of the node that has been added or removed
   * @return true if the give node list matches the path selection of the letter list panel
   */
  private function isInActiveNodeList(nodeList:TaxonomyNodeList):Boolean {
    var parentRef:String = nodeList.getLeafParentRef();
    var currentLevelRef:String = nodePathExpression.getValue();

    //refresh when a node from the selected level is selected (the default root level ref if equals the taxonomy id!)
    return singleSelection || currentLevelRef === parentRef || (nodeList.getNodes().length === 2 && currentLevelRef === taxonomyIdExpression.getValue());
  }

  public function updateSelection(nodeRef:String, doRemove:Boolean, doRefresh:Boolean):void {
    //invoke after the operation has been executed
    var callback:Function = function():void {
      if(doRefresh) {
        getLetterListPanel().updateUI();
      }
    };

    if(!TaxonomyUtil.isInSelection(getSelectionExpression().getValue(), nodeRef)) {
      TaxonomyUtil.addNodeToSelection(selectionExpression, nodeRef, callback);
    }
    else if(doRemove) {
      TaxonomyUtil.removeNodeFromSelection(selectionExpression, nodeRef, callback);
    }
  }

  private function getLetterListPanel():LetterListPanel {
    return queryById(LetterListPanel.ITEM_ID) as LetterListPanel;
  }

  /**
   * Ok button handler.
   */
  protected function okPressed():void {
    var selection:Array = selectionExpression.getValue();
    if (!singleSelection) {
      propertyValueExpression.setValue(selection);
    }
    else {
      if (selection && selection.length > 0) {
        propertyValueExpression.setValue(selection[0]);
      }
      else {
        propertyValueExpression.setValue(null);
      }
    }
    close();
  }

  /**
   * Cancel button handler.
   */
  protected function cancelPressed():void {
    close();
  }

  //noinspection JSMethodCanBeStatic
  /**
   * Depending on single selection mode, a different link list title is displayed
   * for the active selection.
   * @param singleSelection
   * @return
   */
  protected function resolveSelectionTitle(singleSelection:Boolean):String {
    if (singleSelection) {
      return resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomyLinkList_singleSelection_title');
    }
    return resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomyLinkList_title');
  }

  /**
   * Contains the entries selected by the user.
   * @return
   */
  protected function getSelectionExpression():ValueExpression {
    if (!selectionExpression) {
      selectionExpression = ValueExpressionFactory.createFromValue([]);
    }
    return selectionExpression;
  }
}
}
