package com.coremedia.ecommerce.studio.dragdrop {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.augmentation.augmentationService;
import com.coremedia.ecommerce.studio.components.link.CatalogLink;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.Element;
import ext.IEventObject;
import ext.config.droptarget;
import ext.dd.DragSource;
import ext.dd.DropTarget;
import ext.grid.RowSelectionModel;

/**
 * A drop zone for string properties that
 * support a single link to a catalog object per property.
 */
public class CatalogLinkDropTarget extends DropTarget {

  public static const SINGLE_LIST_HOVER_CLASS:String = "single-list-hover";
  public static const DROP_TARGET_HOVER_CLASS:String = "drop-target-hover";
  public static const INSERT_GRID_ROW_ABOVE:String = "grid-row-insert-above";

  private var component:Component;
  private var catalogLink:CatalogLink;
  private var bindTo:ValueExpression;
  private var valueExpression:ValueExpression;
  private var readOnlyValueExpression:ValueExpression;
  private var droppingRowValueExpression:ValueExpression;
  private var catalogObjectTypes:Array;
  private var multiple:Boolean;
  private var duplicate:Boolean;
  private var createStructFunction:Function;
  private var currentRowEl:*;

  public function CatalogLinkDropTarget(component:Component,
                                        catalogLink:CatalogLink,
                                        bindTo:ValueExpression,
                                        valueExpression:ValueExpression,
                                        droppingRowValueExpression:ValueExpression,
                                        catalogObjectTypes:Array,
                                        forceReadOnlyValueExpression:ValueExpression,
                                        multiple:Boolean = false,
                                        duplicate:Boolean = true,
                                        createStructFunction:Function = null) {
    var dropTarget:droptarget = new droptarget();
    dropTarget.ddGroup = "ContentDD";
    super(component.getEl(), dropTarget);
    this.addToGroup("ContentDD");
    this.addToGroup("ContentLinkDD");
    this.component = component;
    this.catalogLink = catalogLink;
    //only for test
    this.catalogLink['CatalogLinkDropTarget'] = this;
    this.bindTo = bindTo;
    this.valueExpression = valueExpression;
    this.droppingRowValueExpression = droppingRowValueExpression;
    this.catalogObjectTypes = catalogObjectTypes;
    readOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression(bindTo, forceReadOnlyValueExpression);
    this.multiple = multiple;
    this.duplicate = duplicate;
    this.createStructFunction = createStructFunction;

    if (droppingRowValueExpression) {
      droppingRowValueExpression.addChangeListener(droppingRowChanged);
      //TODO: removeChangeListener
    }

  }

  private function setValue(dragInfo:CatalogDragInfo, data:Object, rowIndex:Number):void {
    if (multiple) {
      if (data.grid === catalogLink) {
        // reordering drag/drop within the property editor
        reorder(dragInfo.getPositions(), rowIndex);
      } else {
        // drag/drop from another grid
        insertFromOtherGrid(dragInfo, rowIndex);
      }
      removeDuplicates();
    } else {
      // Set the string to the id of the dragged catalog object
      var catalogObject:CatalogObject = getCatalogObject(dragInfo.getCatalogObjects()[0]);
      valueExpression.setValue(catalogObject.getId());
    }
  }

  // Needs to be public so it can be accessed from separate drop dropTarget
  public function reorder(positions:Array, rowIndex:Number):void {
    var isSelected:Array = [];
    positions.forEach(function(position:Number):void {
      isSelected[position] = true;
    });

    var originalBeanList:Array = valueExpression.getValue();

    var beansBefore:Array = [];
    var beansToInsert:Array = [];
    var beansAfter:Array = [];
    for (var j:Number = 0; j < originalBeanList.length; j++) {
      var bean:String = originalBeanList[j];
      if (isSelected[j]) {
        beansToInsert.push(bean);
      } else if (rowIndex === -1 || j < rowIndex) { //-1 means the item is dropped at the drop area
        beansBefore.push(bean);
      } else {
        beansAfter.push(bean);
      }
    }

    updateModelsAfterDrag(beansBefore, beansToInsert, beansAfter);
  }

  private function insertFromOtherGrid(dragInfo:CatalogDragInfo, rowIndex:Number):void {
    // insert into old list and update the model.
    var oldBeans:Array = valueExpression.getValue();
    var catalogObjects:Array = dragInfo.getCatalogObjects();
    var catalogObjectIds:Array = catalogObjects.map(function(catalogObject:CatalogObject):String {
      return getCatalogObject(catalogObject).getId();
    });

    if (rowIndex == -1) {
      //the items are dropped to the drop area blow the grid.
      updateModelsAfterDrag(oldBeans, catalogObjectIds, []);
    } else {
      updateModelsAfterDrag(oldBeans.slice(0, rowIndex), catalogObjectIds, oldBeans.slice(rowIndex));
    }
  }

  private function removeDuplicates():void {
    if (!duplicate) {
      //avoid redundant entries
      var oldIds:Array = valueExpression.getValue();

      var newIds:Array = [];
      for (var i:int = 0; i < oldIds.length; i++) {
        if (newIds.indexOf(oldIds[i]) < 0) {
          newIds = newIds.concat(oldIds[i]);
        }
      }
      valueExpression.setValue(newIds);
    }
  }
  private function updateModelsAfterDrag(beansBefore:Array, beansToInsert:Array, beansAfter:Array):void {
    // Clear the selections. The store will be updated eventually
    // and we cannot yet determine the rows that will be present
    // after the update.
    (catalogLink.getSelectionModel() as RowSelectionModel).clearSelections();

    // Concatenate the partial results and update the model.
    valueExpression.setValue(beansBefore.concat(beansToInsert, beansAfter));
  }

  private function allowDrag(dragInfo:CatalogDragInfo):* {
    if (!dragInfo) {
      return false;
    }
    if (!isWritable()) {
      return false;
    }

    if (!multiple) {
      if (dragInfo.getCatalogObjects().length !== 1) {
        return false;
      }
    }

    var catalogObject:CatalogObject = getCatalogObject(dragInfo.getCatalogObjects()[0]);
    if (!isAllowedType(catalogObject, catalogObjectTypes)) {
      return false;
    }

    //prevent dropping catalog objects from stores of another site
    if(catalogLink.bindTo) {
      var componentOwnerContent:Content = catalogLink.bindTo.getValue();
      var siteId:String = editorContext.getSitesService().getSiteIdFor(componentOwnerContent);
      var catalogObjectSiteId:String = catalogObject.getSiteId();
      if(siteId !== catalogObjectSiteId) {
        return false;
      }
    }
    return !catalogLink.disabled;
  }

  private static function isAllowedType(catalogObject:CatalogObject, catalogObjectTypes:Array): Boolean {
    for (var i:int = 0; i < catalogObjectTypes.length; i++) {
      var catalogObjectType:String = catalogObjectTypes[i];
      if (CatalogHelper.getInstance().isSubType(catalogObject, catalogObjectType)) {
        return true;
      }
    }
    return false;
  }

  private static function getCatalogObject(catalogObjectOrContent:*):CatalogObject {
    if (catalogObjectOrContent is Content) {
      return augmentationService.getCatalogObject(catalogObjectOrContent);
    } else if (catalogObjectOrContent is CatalogObject) {
      return catalogObjectOrContent;
    }
    return null;
  }

  private function isWritable():Boolean {
    return !(readOnlyValueExpression.getValue() === true);
  }

  override public function notifyOver(source:DragSource, e:IEventObject, data:Object):String {
    var droppingRow:Number = computeRowIndex(e);

    //when in the lower half of the last row highlight the linklist drop zone
    if (droppingRow === catalogLink.getStore().getCount()){
      droppingRow = -1;
    }

    if (droppingRowValueExpression) {
      droppingRowValueExpression.setValue(droppingRow);
    }
    return handleOverDrag(source, e, data);
  }
  
  override public function notifyEnter(source:DragSource, e:IEventObject, data:Object):String {
   return handleOverDrag(source, e, data);
  }

  private function handleOverDrag(source:DragSource, e:IEventObject, data:Object):String {
    var dragInfo:CatalogDragInfo = CatalogDragInfo.makeDragInfo(source, data, catalogLink);
    if (allowDrag(dragInfo)) {
      if (!multiple) catalogLink.addClass(SINGLE_LIST_HOVER_CLASS);
      return dropAllowed;
    } else {
      return dropNotAllowed;
    }
  }

  override public function notifyOut(source:DragSource, e:IEventObject, data:Object):void {
    if (droppingRowValueExpression) {
      droppingRowValueExpression.setValue(undefined);
    }
    catalogLink.removeClass(SINGLE_LIST_HOVER_CLASS);
  }

  override public function notifyDrop(source:DragSource, e:IEventObject, data:Object):Boolean {
    if (droppingRowValueExpression) {
      droppingRowValueExpression.setValue(undefined);
    }
    var dragInfo:CatalogDragInfo = CatalogDragInfo.makeDragInfo(source, data, catalogLink);
    if (!allowDrag(dragInfo)) {
      return false;
    }

    if (createStructFunction) {
      createStructFunction.apply();
    }

    // determine the dropTarget row
    var rowIndex:Number = computeRowIndex(e);
    //if the dropTarget row is out-of-bound and we are dragging to the grid
    //then skip it
    if (rowIndex < 0 && component === catalogLink) {
      return false;
    }

    if (valueExpression.isLoaded()) {
      setValue(dragInfo, data, rowIndex);
    } else {
      valueExpression.loadValue(function():void {
        setValue(dragInfo, data, rowIndex);
      });
    }
    catalogLink.removeClass(SINGLE_LIST_HOVER_CLASS);
    return true;
  }

  /**
   * Given an events, compute the index of the row before which the cursor
   * is located. If the cursor is at the end of the list, return the length
   * of the list. If the cursor is outside the drop zone, return -1.
   *
   * @param e the event
   * @return the row index
   */
  private function computeRowIndex(e:IEventObject):Number {
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
    var rowIndex:Number = catalogLink.getView().findRowIndex(target);

    // If the destination lies outside the grid, indicate a rejection by
    // returning -1;
    if (rowIndex < 0 || rowIndex === false) {
      return -1;
    } else {
      // Check cursor position relative to the center of the row.
      // Find position of row relative to page (adjusting for grid's scroll position)
      var currentRow:* = catalogLink.getView().getRow(rowIndex);
      var rowTop:Number = new Element(currentRow).getY() - getScrollerDom()['scrollTop'];
      var rowHeight:Number = currentRow.offsetHeight;
      if (e.getPageY() - rowTop - (rowHeight / 2) > 0) {
        // In lower half.
        return rowIndex + 1;
      }
      return rowIndex;
    }
  }

  private function isInDom(el:*):Boolean {
    var isInDom:Boolean = false;
    for (var loopEl:* = el; loopEl; loopEl = loopEl.parentNode) {
      if (loopEl.id === catalogLink.getId()) {
        isInDom = true;
        break;
      }
    }
    return isInDom;
  }

  private function isStoreEmpty():Boolean {
    return catalogLink.getStore().getCount() == 0;
  }

  /**
   * Return the DOM element associated with the scroller of the grid.
   * This method uses undocumented API.
   *
   * @return the DOM element
   */
  private function getScrollerDom():* {
    return catalogLink.getView()['scroller']['dom'];
  }

  private function droppingRowChanged(valueExpression:ValueExpression):void{
    //first for the grid
    clearDragFeedbackOnGrid();
    var row:* = valueExpression.getValue();
    if (row >= 0) {
      currentRowEl = new Element(catalogLink.getView().getRow(row));
      showDragFeedbackOnGrid();
    }
    //now for the drop area
    if (row === -1) {
      showDragFeedbackOnDropArea();
    } else {
      clearDragFeedbackOnDropArea();
    }

  }

  private function showDragFeedbackOnGrid():void {
    if (currentRowEl) {
      currentRowEl.addClass(INSERT_GRID_ROW_ABOVE);
    }
  }

  private function showDragFeedbackOnDropArea():void {
    component.addClass(DROP_TARGET_HOVER_CLASS);
  }

  private function clearDragFeedbackOnGrid():void {
    if (currentRowEl) {
      currentRowEl.removeClass(INSERT_GRID_ROW_ABOVE);
    }
  }

  private function clearDragFeedbackOnDropArea():void {
    component.removeClass(DROP_TARGET_HOVER_CLASS);
  }

}
}
