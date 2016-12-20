package com.coremedia.ecommerce.studio.components {
import com.coremedia.ecommerce.studio.dragdrop.CatalogDragDropVisualFeedback;
import com.coremedia.ui.data.ValueExpression;

import ext.grid.GridPanel;
import ext.grid.plugin.GridViewDragDropPlugin;

public class AbstractCatalogListBase extends GridPanel {

  /**
   * A value expression that specifies where to set the multiply selected items. This is mandatory.
   */
  public var selectedItemsValueExpression:ValueExpression;

  private var ddPlugin:GridViewDragDropPlugin;

  public function AbstractCatalogListBase(config:AbstractCatalogList = null) {
    super(config);
    on('afterrender', configureDragDrop);
  }

  private function configureDragDrop():void {
    ddPlugin = getView().getPlugin("dragdrop") as GridViewDragDropPlugin;
    //TODO: EXT6_API
    ddPlugin.dragZone['getDragText'] = getDragText;
    ddPlugin.dragZone.addToGroup("ContentLinkDD");
  }

  private function getDragText():String {
    return CatalogDragDropVisualFeedback.getHtmlFeedback(ddPlugin.dragZone.dragData.records);
  }

}
}