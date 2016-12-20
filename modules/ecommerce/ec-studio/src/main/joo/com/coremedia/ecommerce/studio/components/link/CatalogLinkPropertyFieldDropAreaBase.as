package com.coremedia.ecommerce.studio.components.link {
import com.coremedia.ecommerce.studio.dragdrop.CatalogLinkDropZone;
import com.coremedia.ui.components.IconDisplayField;
import com.coremedia.ui.components.StatefulQuickTip;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.util.createComponentSelector;

import ext.dom.Element;
import ext.tip.QuickTipManager;

/**
 * A container that reacts to content drop events by adding the content object to the link list property field.
 */
public class CatalogLinkPropertyFieldDropAreaBase extends IconDisplayField {

  private var catalogLinkDropZone:CatalogLinkDropZone;
  private var multiple:Boolean;
  private var createStructFunction:Function;
  private var openLinkSources:Function;

  /**
   * Create the container.
   * @param config the config object
   */
  public function CatalogLinkPropertyFieldDropAreaBase(config:CatalogLinkPropertyFieldDropArea = null) {
    super(config);
    multiple = config.multiple;
    createStructFunction = config.createStructFunction;
    openLinkSources = config.openLinkSources;
  }

  internal static const COMPUTED_IMG_MARGIN:Number = 14;

  /**
   * A property path expression leading to the Bean whose property is edited. This property editor assumes that this
   * bean has a property 'properties'.
   */
  [Bindable]
  public var bindTo:ValueExpression;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  [Bindable]
  public var forceReadOnlyValueExpression:ValueExpression;

  public native function get propertyName():String;
  public native function get catalogObjectType():String;
  public native function get catalogObjectTypes():Array;
  public native function get maxCardinality():int;
  public native function get duplicate():Boolean;

  override protected function onRender(parentNode:Element, containerIdx:Number):void {
    super.onRender(parentNode, containerIdx);

    // Get reference to related catalog link component.
    // Reference is required to recognize and handle 'internal' drag and drop operations
    var catalogLink:CatalogLink = findParentByType(CatalogLinkPropertyField).down(createComponentSelector()._xtype(CatalogLink.xtype).build()) as CatalogLink;

    catalogLinkDropZone = new CatalogLinkDropZone(this, catalogLink,
            bindTo,
            bindTo.extendBy('properties').extendBy(propertyName),
            catalogObjectType ? [catalogObjectType] : catalogObjectTypes,
            forceReadOnlyValueExpression, multiple, duplicate, createStructFunction);

    getEl().setStyle("cursor", "pointer");
  }

  override protected function afterRender():void {
    super.afterRender();
    mon(getEl(), 'click', openCollectionView);
    mon(getEl(), 'mouseover', showDropTargetTip);
  }

  override protected function onDisable():void {
    super.onDisable();
    getEl().setStyle("cursor", "default");
  }

  override protected function onEnable():void {
    super.onEnable();
    getEl().setStyle("cursor", "pointer");
  }

  /*
   * Create drop target for this component.
   */
  private function openCollectionView():void {
    if(!disabled) {
      openLinkSources();
    }
  }

  /*
   * Create quicktip is necessary.
   */
  private function getDropContainerEl():Element {
    var currentElement:* = this.getEl();
    return currentElement;
  }

  public function showDropTargetTip():void {
    var currentQuickTip:StatefulQuickTip = QuickTipManager.getQuickTip() as StatefulQuickTip;

    //TODO ext6 see regular link list, same problem
//    var currentLabel:Element = getDropContainerEl().child('.icon-label-text');
//    var currentImg:Element = getDropContainerEl().first('img');
//
//    var currentComputedSpace:Number = ((currentImg ? currentImg.getWidth() : 0) + getDropContainerEl().getPadding('lr')) + COMPUTED_IMG_MARGIN;
//    var currentWidth:Number = (getDropContainerEl().getWidth()) - currentComputedSpace;
//
//    var currentLabelWidth:Number = currentLabel.getWidth();
//
//    if (currentLabelWidth > currentWidth){
//      currentQuickTip.register({
//        text: getValue(),
//        target: currentLabel
//      });
//      currentQuickTip.show();
//    } else
//      currentQuickTip.unregister(currentLabel);
//    currentQuickTip.hide();
  }

  override protected function beforeDestroy():void {
    catalogLinkDropZone && catalogLinkDropZone.getDropTarget() && catalogLinkDropZone.getDropTarget().unreg();
    super.beforeDestroy();
  }

}
}
