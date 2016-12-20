package com.coremedia.ecommerce.studio.dragdrop {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.augmentation.augmentationService;
import com.coremedia.ecommerce.studio.components.link.CatalogLink;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkPropertyFieldDropArea;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;

import ext.Component;
import ext.data.Model;
import ext.dd.DragSource;
import ext.dd.DropTarget;
import ext.dd.DropZone;
import ext.event.Event;
import ext.grid.plugin.GridViewDragDropPlugin;

import js.HTMLElement;

/**
 * A drop zone for string properties that
 * support links to a catalog object.
 */
public class CatalogLinkDropZone{

  private var component:Component;
  private var catalogLink:CatalogLink;
  private var dropZone:DropZone;
  private var bindTo:ValueExpression;
  private var valueExpression:ValueExpression;
  private var readOnlyValueExpression:ValueExpression;
  private var catalogObjectTypes:Array;
  private var multiple:Boolean;
  private var duplicate:Boolean;
  private var createStructFunction:Function;
  private var originalNotifyOver:Function;
  private var originalNotifyDrop:Function;

  public function CatalogLinkDropZone(component:Component,
                                      catalogLink:CatalogLink,
                                      bindTo:ValueExpression,
                                      valueExpression:ValueExpression,
                                      catalogObjectTypes:Array,
                                      forceReadOnlyValueExpression:ValueExpression,
                                      multiple:Boolean = false,
                                      duplicate:Boolean = false,
                                      createStructFunction:Function = null) {
    this.component = component;
    this.catalogLink = catalogLink;
    //when single item list or a drop area we lay a drop zone on it
    if (!multiple || component is CatalogLinkPropertyFieldDropArea) {
      dropZone = new DropZone(component.el, {ddGroup: "ContentDD"});
    }

    if ((component is CatalogLink)) {
      var ddPlugin:GridViewDragDropPlugin = catalogLink.getView().getPlugin("dragdrop") as GridViewDragDropPlugin;
      if (multiple) {
        dropZone = ddPlugin.dropZone;
        catalogLink.on("beforedrop", onBeforeDrop);
      } else {
        // disable the drag&drop plugin of the grid as the single item list doesn't need the reordering
        // and the drop zone will care for the drag & drop
        ddPlugin.disable();
      }
    }

    if (!dropZone) {
      throw Error("CatalogLinkDropTarget is applicable only to components of type CatalogLink or CatalogLinkPropertyFieldDropArea");
    }

    originalNotifyOver = dropZone.notifyOver;
    originalNotifyDrop = dropZone.notifyDrop;
    dropZone.notifyOver = notifyOver;
    dropZone.notifyDrop = notifyDrop;
    dropZone.addToGroup("ContentDD");
    dropZone.addToGroup("ContentLinkDD");

    //only for test
    this.catalogLink['CatalogLinkDropTarget'] = this;
    this.bindTo = bindTo;
    this.valueExpression = valueExpression;
    this.catalogObjectTypes = catalogObjectTypes;
    readOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression(bindTo, forceReadOnlyValueExpression);
    this.multiple = multiple;
    this.duplicate = duplicate;
    this.createStructFunction = createStructFunction;
  }

  public function getDropTarget():DropTarget {
    return dropZone;
  }

  /**
   * update the model after drop
   * @param data
   */
  private function setValue(data:Object):void {
    if (multiple) {
      //retrieve the linked beans from the grid
      var links:Array = [];
      if (catalogLink.isHidden()) {
        //when grid is hidden there will be no linked bean
        //catalogLink.getView().getNodes() will cause trouble then
      } else {
        catalogLink.getView().getNodes().forEach(function(node:HTMLElement):void {
          if (component !== catalogLink && //dropped to the drop area
              data.view && data.view.ownerCt === catalogLink && // from the grid above --> we have to reorder
              catalogLink && catalogLink.getView().isSelected(node)) {
            //don't include the selected/dragged beans
          } else {
            var beanOrRecord:* = catalogLink.getView().getRecord(node);
            links.push(getCatalogObject(beanOrRecord).getId());
          }
        });
      }

      //in the grid the view has already all beans. consider only when dropped to the drop area
      if (component !== catalogLink) {
        for each(var record:* in data.records) {
          links.push(getCatalogObject(record).getId());
        }
      }
      if (!duplicate) {
        links = removeDuplicates(links);
      }
      valueExpression.setValue(links);
    } else {
      // Set the string to the id of the dragged catalog object
      var catalogObject:CatalogObject = getCatalogObject(data.records[0]);
      valueExpression.setValue(catalogObject.getId());
    }
  }

  //avoid redundant entries
  private static function removeDuplicates(links:Array):Array {
    var newLinks:Array = [];
    for (var i:int = 0; i < links.length; i++) {
      if (newLinks.indexOf(links[i]) < 0) {
        newLinks = newLinks.concat(links[i]);
      }
    }
    return newLinks;
  }

  private function allowDrop(data:Object):Boolean {
    if (catalogLink.disabled) {
      return false;
    }
    var catalogRecords:Array = data.records;

    if (!catalogRecords) {
      return false;
    }

    if (!isWritable()) {
      return false;
    }

    if (!multiple && catalogRecords.length !== 1) {
      return false;
    }

    for (var i:int = 0; i < catalogRecords.length;i++) {
      var catalogObject:CatalogObject = getCatalogObject(catalogRecords[i]);
      if (!isAllowedType(catalogObject, catalogObjectTypes)) {
        return false;
      }
    }

    //now we have only allowed types here and can check the duplicates
    if (isDuplicate(data)) {
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
    return true;
  }

  private function isDuplicate(data:Object):Boolean {
    if (!multiple || catalogLink.isHidden()) {
      return false;
    }

    if (data.view && data.view.ownerCt === catalogLink) { // dragging from the grid itself --> reordering
      return false;
    }

    var links:Array = valueExpression.getValue();
    var catalogRecords:Array = data.records;
    for (var i:int = 0; i < catalogRecords.length;i++) {
      var catalogObject:CatalogObject = getCatalogObject(catalogRecords[i]);
      if (links.indexOf(catalogObject.getId()) >= 0) {
        return true
      }
    }

    return false;
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

  private static function getCatalogObject(beanOrRecord:*):CatalogObject {
    var bean:Bean;
    if (beanOrRecord is Bean) {
      bean = beanOrRecord;
    } else if (beanOrRecord is BeanRecord) {
      bean = beanOrRecord.getBean();
    } else if (beanOrRecord.getId) {
      //from catalog tree
      bean = beanFactory.getRemoteBean(beanOrRecord.getId());
    }

    if (bean is Content) {
      return augmentationService.getCatalogObject(Content(bean));
    } else if (bean is CatalogObject) {
      return bean as CatalogObject;
    }
    return null;
  }

  private function isWritable():Boolean {
    return !(readOnlyValueExpression.getValue() === true);
  }

  private function notifyOver(source:DragSource, e:Event, data:Object):String {
    originalNotifyOver(source, e, data);
    if (allowDrop(data)) {
      return dropZone.dropAllowed;
    }

    return dropZone.dropNotAllowed;
  }

  private function notifyDrop(source:DragSource, e:Event, data:Object):Boolean {
    if (!allowDrop(data)) {
      return false;
    }

    if (createStructFunction) {
      createStructFunction.apply();
    }

    originalNotifyDrop(source, e, data);

    setValue(data);
    return true;
  }

  //noinspection JSUnusedLocalSymbols
  private function onBeforeDrop(node:HTMLElement, data:Object, overModel:Model, dropPosition:String):Boolean {
    //avoid moving of the drag source when dropping from tree
    if (catalogLink === component) {
      //dropping to the grid
      if (!data.records[0].getBean && !(data.records[0] is CatalogObject)) {
        data.copy = true;
      }
    }

    return allowDrop(data);
  }
}
}
