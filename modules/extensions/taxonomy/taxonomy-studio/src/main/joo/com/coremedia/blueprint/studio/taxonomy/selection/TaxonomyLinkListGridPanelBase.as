package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.dragdrop.ContentDragProvider;
import com.coremedia.cms.editor.sdk.dragdrop.DragInfo;
import com.coremedia.cms.editor.sdk.util.ILinkListWrapper;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EventUtil;

import ext.Ext;
import ext.StringUtil;
import ext.dd.DragSource;
import ext.dd.DropTarget;
import ext.dd.ScrollManager;
import ext.dom.Element;
import ext.event.Event;
import ext.grid.GridPanel;
import ext.selection.RowSelectionModel;

import js.HTMLElement;

/**
 * @private
 *
 * The application logic for a property field editor that edits
 * link lists. Links can be limited to documents of a given type.
 *
 * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
 * @see com.coremedia.cms.editor.sdk.premular.fields.LinkListPropertyField
 */
[ResourceBundle('com.coremedia.cms.editor.Editor')]
[ResourceBundle('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin')]
public class TaxonomyLinkListGridPanelBase extends GridPanel implements ContentDragProvider {

  [Bindable]
  public var linkListWrapper:ILinkListWrapper;

  [Bindable]
  public var readOnlyValueExpression:ValueExpression;

  /**
   * The taxonomy identifier configured on the server side.
   */
  [Bindable]
  public var taxonomyId:String;

  /**
   * (optional) The siteId
   */
  [Bindable]
  public var siteId:String = null;

  private var dropTarget:DropTarget;

  /**
   * @param config The configuration options. See the config class for details.
   *
   * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
   */
  public function TaxonomyLinkListGridPanelBase(config:TaxonomyLinkListGridPanelBase = null) {
    super(config);

    this.addListener("afterlayout", refreshLinkList);
  }


  override protected function afterRender():void {
    super.afterRender();

    //noinspection JSUnusedGlobalSymbols
    var dropTargetCfg:DropTarget = DropTarget({});
    dropTargetCfg.ddGroup = 'ContentLinkDD';
    dropTargetCfg['gridDropTarget'] = this;
    dropTargetCfg['notifyDrop'] = notifyDrop;
    dropTargetCfg['notifyOver'] = notifyOver;
    dropTargetCfg['notifyEnter'] = notifyOver;
    dropTargetCfg['notifyOut'] = notifyOut;
    dropTarget = new DropTarget(this.getEl(), dropTargetCfg);
    dropTarget.addToGroup("ContentDD");

    if (this.scrollable) {
      ScrollManager.register(getScrollerDom());

      this.addListener("beforedestroy", onBeforeDestroy, this, {single:true});
    }
  }

  private function updateModelsAfterDrag(beansBefore:Array, beansToInsert:Array, beansAfter:Array):void {
    // Clear the selections. The store will be updated eventually
    // and we cannot yet determine the rows that will be present
    // after the update.
    (getSelectionModel() as RowSelectionModel).deselectAll();

    // Concatenate the partial results and update the model.
    linkListWrapper.setLinks(beansBefore.concat(beansToInsert, beansAfter));
  }

  // Needs to be public so it can be accessed from separate drop dropTarget
  public function reorder(positions:Array, rowIndex:Number):void {
    var isSelected:Array = [];
    positions.forEach(function (position:Number):void {
      isSelected[position] = true;
    });

    var originalBeanList:Array = linkListWrapper.getLinks();

    var beansBefore:Array = [];
    var beansToInsert:Array = [];
    var beansAfter:Array = [];
    for (var j:Number = 0; j < originalBeanList.length; j++) {
      var bean:RemoteBean = originalBeanList[j] as RemoteBean;
      if (isSelected[j]) {
        beansToInsert.push(bean);
      } else if (j < rowIndex) {
        beansBefore.push(bean);
      } else {
        beansAfter.push(bean);
      }
    }

    updateModelsAfterDrag(beansBefore, beansToInsert, beansAfter);
  }

  private function notifyDrop(d:DragSource, e:Event, data:Object):Boolean {
    var dragInfo:DragInfo = DragInfo.makeDragInfo(d, data, this);
    if (!dragInfo) {
      return false;
    }

    // Is the current content modifiable at all?
    if (!isWritable()) {
      return false;
    }

    // determine the dropTarget row
    var rowIndex:Number = computeRowIndex(e);
    if (rowIndex < 0) {
      return false;
    }

    if (data.view.grid === this) {
      // reordering drag/drop within the property editor
      reorder(dragInfo.getPositions(), rowIndex);
    } else {
      // drag/drop from another grid
      // not used at the moment!
    }
    return true;
  }

  private function isWritable():Boolean {
    return !readOnlyValueExpression || !readOnlyValueExpression.getValue();
  }

  /**
   * Given an events, compute the index of the row before which the cursor
   * is located. If the cursor is at the end of the list, return the length
   * of the list. If the cursor is outside the drop zone, return -1.
   *
   * @param e the event
   * @return the row index
   */
  private function computeRowIndex(e:Event):Number {
    // Loose the type of getTarget(). Somehow the typing of getTarget()
    // and findRowIndex() is broken. One may definitely pass the dropTarget as
    // shown here.
    var target:* = e.getTarget();

    // Is the dropTarget part of the DOM owned by this component?
    // At times, ExtJS dispatches move and drop events to the
    // wrong drop dropTarget.
    if (!isInDom(target)) {
      // The event does not belong here.
      return -1;
    }

    if (isStoreEmpty()) {
      // Yes. Be generous with respect to the drop position. There cannot
      // be much of a discussion.
      return 0;
    }

    // Drop before this row.
    var rowIndex:Number = this.getView().indexOf(target);

    // If the destination lies outside the grid, indicate a rejection by
    // returning -1;
    if (rowIndex < 0 || rowIndex === false) {
      return -1;
    } else {
      // Check cursor position relative to the center of the row.
      // Find position of row relative to page (adjusting for grid's scroll position)
      var currentRow:* = this.getView().getRow(rowIndex);

      var rowTop:Number = new Element(currentRow).getY() - HTMLElement(getScrollerDom()).scrollTop;
      var rowHeight:Number = currentRow.offsetHeight;
      if (e.getY() - rowTop - (rowHeight / 2) > 0) {
        // In lower half.
        return rowIndex + 1;
      }
      return rowIndex;
    }
  }

  private function isInDom(el:*):Boolean {
    var isInDom:Boolean = false;
    for (var loopEl:* = el; loopEl; loopEl = loopEl.parentNode) {
      if (loopEl.id === getId()) {
        isInDom = true;
        break;
      }
    }
    return isInDom;
  }

  private function isStoreEmpty():Boolean {
    return getStore().getCount() === 0;
  }

  private function notifyOver(d:DragSource, e:Event, data:Object):String {
    var dragInfo:DragInfo = DragInfo.makeDragInfo(d, data, this);
    if (!dragInfo) {
      return dropTarget.dropNotAllowed;
    }

    // Is the edited content able to receive a new value?
    if (!isWritable()) {
      return dropTarget.dropNotAllowed;
    }

    // Foreign data is about to enter this list; is the data ok?
    if (!dragInfo.isLocalDrag() && !linkListWrapper.acceptsLinks(dragInfo.getContents())) {
      return dropTarget.dropNotAllowed;
    }

    var rowIndex:Number = computeRowIndex(e);

    // Do not allow a drop outside the grid.
    if (rowIndex < 0) {
      return dropTarget.dropNotAllowed;
    }

    if (dragInfo.isLocalDrag()) {
      // One might want to disallow a drop if no reordering would happen.
    }

    return (rowIndex === false) ? dropTarget.dropNotAllowed : dropTarget.dropAllowed;
  }

  //noinspection JSUnusedLocalSymbols
  private function notifyOut(d:*, e:Event, data:Object):void {
  }

  private function onBeforeDestroy():void {
    // if we previously registered with the scroll manager, unregister
    // it (if we don't, it will lead to problems in IE)
    ScrollManager.unregister(getScrollerDom());
  }

  /**
   * Return the DOM element associated with the scroller of the grid.
   * This method uses undocumented API.
   *
   * @return the DOM element
   */
  private function getScrollerDom():* {
    return getEl().dom;
  }

  public function isLinking():Boolean {
    return true;
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Displays each path item of a taxonomy
   * @param value
   * @param metaData
   * @param record
   *
   * @see http://docs.sencha.com/extjs/6.0.1-classic/Ext.grid.column.Column.html#cfg-renderer
   *
   * @return String
   */
  protected function taxonomyRenderer(value:*, metaData:*, record:BeanRecord):String {
    TaxonomyUtil.isEditable(taxonomyId, function (editable:Boolean):void {
      if (editable) {
        TaxonomyUtil.loadTaxonomyPath(record, siteId, taxonomyId, function (updatedRecord:BeanRecord):void {
          var renderer:TaxonomyRenderer = TaxonomyRenderFactory.createSelectedListRenderer(record.data.nodes, getId(), linkListWrapper.getLinks().length > 3);
          renderer.doRender(function (html:String):void {
            if (record.data.html !== html) {
              record.data.html = html;
              record.commit(false);
            }
          });
        });
      }
      else {
        var msg:String = StringUtil.format(resourceManager.getString('com.coremedia.cms.editor.Editor', 'Content_notReadable_text'), IdHelper.parseContentId(record.getBean()));
        var html:String = '<img width="16" height="16" src="' + Ext.BLANK_IMAGE_URL + '" data-qtip="" />'
                + '<span>' + msg + '</span>';
        if (record.data.html !== html) {
          record.data.html = html;
          EventUtil.invokeLater(function ():void {
            record.commit(false);
          });
        }
      }
    }, record.getBean() as Content);

    if (!record.data.html) {
      return "<div class='loading'>" + resourceManager.getString('com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin', 'TaxonomyLinkList_status_loading_text') + "</div>";
    }
    return record.data.html;
  }

  /**
   * Executes after layout, we have to refresh the HTML too.
   */
  private function refreshLinkList():void {
    for(var i:int = 0; i<getStore().getCount(); i++) {
      getStore().getAt(i).data.html = null;
    }
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Removes the given taxonomy<br>
   * Used in TaxonomyRenderer#plusMinusClicked
   */
  public function plusMinusClicked(nodeRef:String):void {
    if (isWritable()) {
      TaxonomyUtil.removeNodeFromSelection(linkListWrapper.getVE(), nodeRef);
    }
  }

  override protected function onRemoved(destroying:Boolean):void {
    dropTarget && dropTarget.unreg();
    super.onRemoved(destroying);
  }
}
}
