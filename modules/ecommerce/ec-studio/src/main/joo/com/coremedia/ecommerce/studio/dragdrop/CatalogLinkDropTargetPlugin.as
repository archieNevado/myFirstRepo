package com.coremedia.ecommerce.studio.dragdrop {
import com.coremedia.ecommerce.studio.components.link.CatalogLink;
import com.coremedia.ecommerce.studio.config.catalogLinkDropTargetPlugin;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.Plugin;

/**
 * A plugin to create a drop target that receives a single catalog link
 * and writes its id to the given value expression.
 */
public class CatalogLinkDropTargetPlugin implements Plugin {
  private var bindTo:ValueExpression;
  private var valueExpression:ValueExpression;
  private var droppingRowValueExpression:ValueExpression;
  private var catalogObjectTypes:Array;
  private var forceReadOnlyValueExpression:ValueExpression;
  private var multiple:Boolean;
  private var duplicate:Boolean;
  private var createStructFunction:Function;

  private var catalogLink:CatalogLink;
  private var linkDropTarget:CatalogLinkDropTarget;

  /**
   * A plugin to create a drop target that receives a single catalog link.
   *
   * @param config the config object
   *
   * @see com.coremedia.ecommerce.studio.config.catalogLinkDropTargetPlugin
   */
  public function CatalogLinkDropTargetPlugin(config:catalogLinkDropTargetPlugin = null) {
    bindTo = config.bindTo;
    valueExpression = config.valueExpression;
    droppingRowValueExpression = config.droppingRowValueExpression;
    catalogObjectTypes = config.catalogObjectType ? [config.catalogObjectType] : config.catalogObjectTypes;
    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
    multiple = config.multiple;
    duplicate = config.duplicate;
    createStructFunction = config.createStructFunction;
  }

  public function init(component:Component):void {
    catalogLink = component as CatalogLink;
    if (!catalogLink) {
      throw Error("plugin is applicable only to CatalogLink");
    }
    catalogLink.mon(catalogLink, 'render', onRender);
    catalogLink.mon(catalogLink, 'beforedestroy', beforeCmpDestroy);
  }

  private function beforeCmpDestroy():void {
    linkDropTarget && linkDropTarget.unreg();
  }

  private function onRender():void {
    linkDropTarget = new CatalogLinkDropTarget(catalogLink, catalogLink, bindTo, valueExpression, droppingRowValueExpression, catalogObjectTypes,
            forceReadOnlyValueExpression, multiple, duplicate, createStructFunction);
  }
}
}
