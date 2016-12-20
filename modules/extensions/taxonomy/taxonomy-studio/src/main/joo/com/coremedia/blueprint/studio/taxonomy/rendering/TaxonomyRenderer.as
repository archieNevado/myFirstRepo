package com.coremedia.blueprint.studio.taxonomy.rendering {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.ui.models.bem.BEMBlock;
import com.coremedia.ui.models.bem.BEMElement;
import com.coremedia.ui.models.bem.BEMModifier;
import com.coremedia.ui.plugins.BEMMixin;
import com.coremedia.ui.util.EventUtil;

import ext.ComponentManager;

import js.Element;
import js.Event;

import mx.resources.ResourceManager;

/**
 * The common renderer implementation. Subclasses will overwrite the doRenderInternal method.
 */
[ResourceBundle('com.coremedia.icons.CoreIcons')]
public class TaxonomyRenderer {

  public static const TAXONOMY_BLOCK:BEMBlock = new BEMBlock("cm-taxonomy-node");
  public static const TAXONOMY_ELEMENT_BOX:BEMElement = TAXONOMY_BLOCK.createElement("box");
  public static const TAXONOMY_ELEMENT_NAME:BEMElement = TAXONOMY_BLOCK.createElement("name");
  public static const TAXONOMY_ELEMENT_CONTROL:BEMElement = TAXONOMY_BLOCK.createElement("control");
  public static const TAXONOMY_ELEMENT_LINK:BEMElement = TAXONOMY_BLOCK.createElement("link");
  public static const TAXONOMY_MODIFIER_ARROW:BEMModifier = TAXONOMY_BLOCK.createModifier("has-children");
  public static const TAXONOMY_MODIFIER_ELLIPSIS:BEMModifier = TAXONOMY_BLOCK.createModifier("ellipsis");
  public static const TAXONOMY_MODIFIER_LEAF:BEMModifier = TAXONOMY_BLOCK.createModifier("leaf");


  private var html:String; //applied if the callback handler is or can not used

  public var componentId:String;
  public var nodes:Array;

  public function TaxonomyRenderer(nodes:Array, componentId:String) {
    this.nodes = nodes;
    this.componentId = componentId;
  }

  public function getHtml():String {
    return html;
  }

  public function setHtml(value:String):void {
    html = value;
  }

  /**
   * Triggers the rendering for the concrete instance of the renderer.
   * @param callback The callback function the generated HTML will be passed to.
   */
  public function doRender(callback:Function = null):void {
    doRenderInternal(nodes, callback);
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Returns the nodes of this renderer.
   * @return
   */
  protected function getNodes():Array {
    return nodes;
  }

  public function isScrollable():Boolean {
    return false;
  }

  protected function getLeafName(node:TaxonomyNode):String {
    return node.getDisplayName();
  }

  protected function renderNodeName(node:TaxonomyNode):String {
    return '<span class="' + TAXONOMY_ELEMENT_NAME + '">' + getLeafName(node) + '</span>';
  }

  /**
   * Renders the text link of a taxonomy node.
   * The rendering component is responsible for implementing the public method "nodeclicked(ref:String)".
   * @param node the node used to render the link for
   */
  protected function renderNodeNameWithLink(node:TaxonomyNode):String {
    var id:String = "taxonomy-" + componentId + "-textlink-" + node.getRef().replace("/", "-");
    var idAttribute:String = ' id="' + id + '"';

    var wrapperElement:Element = window.document.getElementById(id);
    if (wrapperElement) {
      wrapperElement.removeEventListener("click", nodeClicked, false);
    }

    EventUtil.invokeLater(function ():void {
      var wrapperElement:Element = window.document.getElementById(id);
      if(wrapperElement) {
        wrapperElement.addEventListener("click", nodeClicked, false);
      }
    });

    return '<span ' + idAttribute + ' data-ref="' + node.getRef() + '"' +
            ' data-componentId="' + componentId + '" class="'+  TAXONOMY_ELEMENT_NAME + ' ' + TAXONOMY_ELEMENT_LINK + '">' + getLeafName(node) + '</span>';
  }

  /**
   * Handler for the node text link.
   * The method must be static in order to add/remove the listener function.
   */
  private static function nodeClicked(event:Event):void {
    var compId:String = event.target.getAttribute("data-componentId") as String;
    var nodeRef:String = event.target.getAttribute("data-ref") as String;
    ComponentManager.get(compId)['nodeClicked'](nodeRef);
    event.preventDefault();
    event.stopPropagation();
  }

  /**
   * Renders the node including the '+' link into each row, using ids.
   */
  protected function renderPlusMinusControl(node:TaxonomyNode, plus:Boolean):String {
    if (plus !== undefined && plus !== null) {
      var cls:String = TAXONOMY_ELEMENT_CONTROL.getCSSClass();
      var id:String = "taxonomy-" + componentId + "-action-" + node.getRef().replace("/", "-");
      var idAttribute:String = ' id="' + id + '"';

      if (plus) {
        cls += " " + ResourceManager.getInstance().getString('com.coremedia.icons.CoreIcons', 'add_special_size');
      }
      else {
        cls += " " + ResourceManager.getInstance().getString('com.coremedia.icons.CoreIcons', 'remove_small');
      }

      var wrapperElement:Element = window.document.getElementById(id);
      if (wrapperElement) {
        wrapperElement.removeEventListener("click", plusMinusClicked, false);
      }

      EventUtil.invokeLater(function ():void {
        var wrapperElement:Element = window.document.getElementById(id);
        if(wrapperElement) {
          wrapperElement.addEventListener("click", plusMinusClicked, false);
        }
      });

      return '<span ' + idAttribute + ' class=" ' + cls + '" data-componentId="' + componentId + '" data-ref="' + node.getRef() + '"></span>';
    }
    return "";
  }

  /**
   * Handler for the plus/minus icon.
   * The method must be static in order to add/remove the listener function.
   */
  private static function plusMinusClicked(event:Event):void {
    var compId:String = event.target.getAttribute("data-componentId") as String;
    var nodeRef:String = event.target.getAttribute("data-ref") as String;
    ComponentManager.get(compId)['plusMinusClicked'](nodeRef);
    event.preventDefault();
    event.stopPropagation();
  }

  /**
   * Common utility method for rendering a taxonomy node
   *
   * @param node the node to render
   * @param withArrow true to render a right arrow next to the node
   * @param textLink true to render the node name as link
   * @param addButton true to add a 'plus' icon to the node, false for 'remove' and undefined to skip the area
   * @param selected true to render the node as selected, will result in another background color
   * @return the HTML of the node
   */
  protected function renderNode(node:TaxonomyNode, withArrow:Boolean, textLink:Boolean, addButton:Boolean, selected:Boolean):String {
    //<span class="outerwapper">
    //   <span class="innerwrapper">
    //     <span class="name">NAME</span>
    //     <span class="control" />
    //   </span>
    //</span>
    var outerCls:String = TAXONOMY_BLOCK.getCSSClass();
    if (withArrow) {
      outerCls += " " + TAXONOMY_MODIFIER_ARROW;
    }

    if (selected) {
      outerCls += " " + TAXONOMY_MODIFIER_LEAF;
    }

    var borderCls:String = TAXONOMY_ELEMENT_BOX.getCSSClass();

    var html:String = '<span class="' + outerCls + '">';
    html += '<span class="' + borderCls + '">';
    if (textLink) {
      html += renderNodeNameWithLink(node);
    }
    else {
      html += renderNodeName(node);
    }
    html += renderPlusMinusControl(node, addButton);
    html += '</span>';
    html += '</span>';
    return html;
  }

  // ------------------------------ Concrete Rendering ------------------------------------------------------------

  /**
   * The method must be overwritten by subclasses, error is thrown otherwise.
   * @param nodes
   * @param callback The callback method the HTML is passed to.
   */
  protected function doRenderInternal(nodes:Array, callback:Function):void {
    throw new Error("Subclass must overwrite rendering method 'doRenderInternal'");
  }
}
}