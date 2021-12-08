import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import { as } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyBEMEntities from "./TaxonomyBEMEntities";

/**
 * The common renderer implementation. Subclasses will overwrite the doRenderInternal method.
 */
class TaxonomyRenderer {

  #html: string = null; //applied if the callback handler is or can not used

  componentId: string = null;

  nodes: Array<any> = null;

  #renderControl: boolean = true;

  constructor(nodes: Array<any>, componentId: string) {
    this.nodes = nodes;
    this.componentId = componentId;
  }

  getHtml(): string {
    return this.#html;
  }

  setHtml(value: string): void {
    this.#html = value;
  }

  /**
   * Triggers the rendering for the concrete instance of the renderer.
   * @param callback The callback function the generated HTML will be passed to.
   */
  doRender(callback: AnyFunction = null): void {
    this.doRenderInternal(this.nodes, callback);
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Returns the nodes of this renderer.
   * @return
   */
  protected getNodes(): Array<any> {
    return this.nodes;
  }

  isScrollable(): boolean {
    return false;
  }

  setRenderControl(renderCtrl: boolean): void {
    this.#renderControl = renderCtrl;
  }

  protected getLeafName(node: TaxonomyNode): string {
    return node.getDisplayName();
  }

  protected renderNodeName(node: TaxonomyNode): string {
    return "<span class=\"" + TaxonomyBEMEntities.NODE_ELEMENT_NAME + "\">" + this.getLeafName(node) + "</span>";
  }

  /**
   * Renders the text link of a taxonomy node.
   * The rendering component is responsible for implementing the public method "nodeclicked(ref:String)".
   * @param node the node used to render the link for
   */
  protected renderNodeNameWithLink(node: TaxonomyNode): string {
    const id = "taxonomy-" + this.componentId + "-textlink-" + TaxonomyRenderer.getDataRef(node);
    const idAttribute = " id=\"" + id + "\"";

    const wrapperElement = window.document.getElementById(id);
    if (wrapperElement) {
      wrapperElement.removeEventListener("click", TaxonomyRenderer.#nodeClicked, false);
    }

    EventUtil.invokeLater((): void => {
      const wrapperElement = window.document.getElementById(id);
      if (wrapperElement) {
        wrapperElement.addEventListener("click", TaxonomyRenderer.#nodeClicked, false);
      }
    });

    return "<span " + idAttribute + " data-ref=\"" + TaxonomyRenderer.getDataRef(node) + "\"" +
            " data-componentId=\"" + this.componentId + "\" class=\"" + TaxonomyBEMEntities.NODE_ELEMENT_NAME + " " + TaxonomyBEMEntities.NODE_ELEMENT_LINK + "\">" + this.getLeafName(node) + "</span>";
  }

  /**
   * Handler for the node text link.
   * The method must be static in order to add/remove the listener function.
   */
  static #nodeClicked(event: Event): void {
    const element = as(event.target, HTMLElement);
    const compId = as(element.getAttribute("data-componentId"), String);
    const nodeRef = as(element.getAttribute("data-ref"), String);
    ComponentManager.get(compId)["nodeClicked"](nodeRef);
    event.preventDefault();
    event.stopPropagation();
  }

  protected static getDataRef(node: TaxonomyNode): string {
    return node.getRef().replace("/", "-");
  }

  /**
   * Renders the node including the '+' link into each row, using ids.
   */
  protected renderPlusMinusControl(node: TaxonomyNode, plus: boolean): string {
    if (plus !== undefined && plus !== null) {
      let cls = TaxonomyBEMEntities.NODE_ELEMENT_CONTROL.getCSSClass();
      const id = "taxonomy-" + this.componentId + "-action-" + TaxonomyRenderer.getDataRef(node);
      const idAttribute = " id=\"" + id + "\"";

      if (plus) {
        cls += " " + CoreIcons_properties.add_special_size;
      } else {
        cls += " " + CoreIcons_properties.remove_small;
      }

      const wrapperElement = window.document.getElementById(id);
      if (wrapperElement) {
        wrapperElement.removeEventListener("click", TaxonomyRenderer.#plusMinusClicked, false);
      }

      EventUtil.invokeLater((): void => {
        const wrapperElement = window.document.getElementById(id);
        if (wrapperElement) {
          wrapperElement.addEventListener("click", TaxonomyRenderer.#plusMinusClicked, false);
        }
      });

      return "<span " + idAttribute + " class=\" " + cls + "\" data-componentId=\"" + this.componentId + "\" data-ref=\"" + TaxonomyRenderer.getDataRef(node) + "\"></span>";
    }
    return "";
  }

  /**
   * Handler for the plus/minus icon.
   * The method must be static in order to add/remove the listener function.
   */
  static #plusMinusClicked(event: Event): void {
    const element = as(event.target, HTMLElement);
    const compId = as(element.getAttribute("data-componentId"), String);
    let nodeRef = as(element.getAttribute("data-ref"), String);
    nodeRef = nodeRef.replace("-", "/");
    ComponentManager.get(compId)["plusMinusClicked"](nodeRef);
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
  protected renderNode(node: TaxonomyNode, withArrow: boolean, textLink: boolean, addButton: boolean, selected: boolean): string {
    //<span class="outerwapper">
    //   <span class="innerwrapper">
    //     <span class="name">NAME</span>
    //     <span class="control" />
    //   </span>
    //</span>
    let outerCls = TaxonomyBEMEntities.NODE_BLOCK.getCSSClass();
    if (withArrow) {
      outerCls += " " + TaxonomyBEMEntities.NODE_MODIFIER_ARROW;
    }

    if (selected) {
      outerCls += " " + TaxonomyBEMEntities.NODE_MODIFIER_LEAF;
    }

    const borderCls = TaxonomyBEMEntities.NODE_ELEMENT_BOX.getCSSClass();

    let html = "<span class=\"" + outerCls + "\">";
    html += "<span class=\"" + borderCls + "\">";
    if (textLink) {
      html += this.renderNodeNameWithLink(node);
    } else {
      html += this.renderNodeName(node);
    }

    if (this.#renderControl) {
      html += this.renderPlusMinusControl(node, addButton);
    }

    html += "</span>";
    html += "</span>";
    return html;
  }

  protected createAriaLabel(nodes: Array<any>): string {
    return nodes.reduce((result: string, node: any): string => {
      const taxNode = new TaxonomyNode(node);
      return result + taxNode.getRawName() + " ";
    }, "");
  }

  // ------------------------------ Concrete Rendering ------------------------------------------------------------

  /**
   * The method must be overwritten by subclasses, error is thrown otherwise.
   * @param nodes
   * @param callback The callback method the HTML is passed to.
   */
  protected doRenderInternal(nodes: Array<any>, callback: AnyFunction): void {
    throw new Error("Subclass must overwrite rendering method 'doRenderInternal'");
  }
}

export default TaxonomyRenderer;
