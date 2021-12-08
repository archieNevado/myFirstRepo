import Ext from "@jangaroo/ext-ts";
import { as, bind, cast, is } from "@jangaroo/runtime";
import uint from "@jangaroo/runtime/uint";
import TaxonomyBEMEntities from "./TaxonomyBEMEntities";
import TaxonomyRenderer from "./TaxonomyRenderer";

class NodePathEllipsis {
  #wrapperId: string = null;

  #componentId: string = null;

  #nodes: Array<any> = null;

  #averageNodeWith: number = NaN;

  #scrollable: boolean = false;

  #fixWidth: number = NaN;

  constructor(wrapperId: string, renderer: TaxonomyRenderer, fixWidth?: number) {
    this.#wrapperId = wrapperId;
    this.#componentId = renderer.componentId;
    this.#nodes = renderer.nodes;
    this.#fixWidth = fixWidth;
    this.#scrollable = renderer.isScrollable();
  }

  /**
   * This is executed once the rendering is done.
   * We can calculate now all the actual width's, so no more guessing
   * what the width of a node might be.
   */
  autoEllipsis(): void {

    // Try to add the resize listener
    this.#addResizeListener();

    let actualTotalWidth: number = 0;
    const spanElement: any = window.document.getElementById(this.#wrapperId);
    //might not be there if not match was found
    if (!spanElement) {
      return;
    }

    const nodeElements: Array<any> = spanElement.childNodes;

    const nodeWidthMap: Record<string, any> = {};

    // Cache the node withs and calculate the totalWidth of all nodes
    for (let i = 0; i < nodeElements.length; i++) {
      const c: HTMLElement = nodeElements[i];
      NodePathEllipsis.#disableEllipsis(c);
      nodeWidthMap[i] = this.#getNodeWidth(c);
      actualTotalWidth += nodeWidthMap[i];
    }

    let componentWidth = this.#getComponentWidth();
    //check if a scrollbar may overlap
    if (this.#scrollable) {
      componentWidth = componentWidth - 32;
    }

    // Should nodes be ellipsified?
    if (actualTotalWidth <= componentWidth) {
      //the path rendering fits into the container, so everything is fine
      for (let j = 1; j < nodeElements.length - 1; j++) {
        var node = nodeElements[j];
        this.#removeNodeMouseHandlers(node);
      }
    } else {
      //Apply stripping: calculate the average size, but let the leaf AND root as it is
      const availableParentWidth: number = componentWidth - nodeWidthMap[nodeElements.length - 1] - nodeWidthMap[0];
      if (availableParentWidth > 0) {
        this.#averageNodeWith = Math.floor(availableParentWidth / (this.#nodes.length - 2));

        const nodePadding = this.#getNodePadding(nodeElements[nodeElements.length - 1]);

        //...and apply the value
        for (let k = 1; k < nodeElements.length - 1; k++) {
          node = nodeElements[k];
          const nodeWidth: number = nodeWidthMap[k];
          const surroundingWidth: number = nodeWidth - this.#getNodeBoxWidth(node) + nodePadding;
          if (nodeWidth > this.#averageNodeWith && nodeWidth > surroundingWidth) {
            //store the width as data attribute since we have to access it in a static context
            NodePathEllipsis.#getNameNode(node).setAttribute("data-width", this.#averageNodeWith - surroundingWidth);
            this.#addNodeMouseEventHandlers(node);
          } else {
            //not stripped, given node is short enough
          }
        }
      }
    }
  }

  /**
   * The first node after a break caused by the drop down width has the full length: ~680px
   * As a workaround we calculate the width of this node.
   */
  #getNodeWidth(node: HTMLElement): number {
    const element = Ext.fly(node);
    if (element) {
      return element.getWidth();
    }
    return 0;
  }

  /**
   * Calculate the width of cm-taxonomy-node__box
   *
   * @param node
   * @return
   */
  #getNodeBoxWidth(node: HTMLElement): number {
    const elements = as(Ext.fly(node).query(TaxonomyBEMEntities.NODE_ELEMENT_BOX.getCSSSelector(), false), Array);
    return elements && elements.length > 0 ? elements[0]["getWidth"]() : 0;
  }

  /**
   * Get the padding of cm-taxonomy-node
   *
   * @param node
   * @return
   */
  #getNodePadding(node: HTMLElement): number {
    return this.#getNodeWidth(node) - this.#getNodeBoxWidth(node);
  }

  /**
   * Adds the hover listeners for those nodes that are ellipsed.
   * Note that this method must be static so that the listeners
   * and be registered and de-registered on refresh.
   * @param node the node element to apply the ellipsis for
   */
  #addNodeMouseEventHandlers(node: HTMLElement): void {
    node.removeEventListener("mouseover", NodePathEllipsis.#handleNodeMouseOver, false);
    node.addEventListener("mouseover", NodePathEllipsis.#handleNodeMouseOver, false);
    NodePathEllipsis.#enableEllipsis(node);

    node.removeEventListener("mouseout", NodePathEllipsis.#handleNodeMouseOut, false);
    node.addEventListener("mouseout", NodePathEllipsis.#handleNodeMouseOut, false);
  }

  /**
   * Removes the hover listeners for those nodes that are ellipsed.
   * @param node the node element to remove the ellipsis from
   */
  #removeNodeMouseHandlers(node: HTMLElement): void {
    node.removeEventListener("mouseover", NodePathEllipsis.#handleNodeMouseOver, false);
    NodePathEllipsis.#disableEllipsis(node);

    node.removeEventListener("mouseout", NodePathEllipsis.#handleNodeMouseOut, false);
  }

  /**
   * The mouse over listener that is executed for ellipsed nodes.
   */
  static #handleNodeMouseOver(event: Event): void {
    NodePathEllipsis.#disableEllipsis(as(event.target, HTMLElement));
  }

  /**
   * The mouse out listener that is executed for ellipsed nodes.
   */
  static #handleNodeMouseOut(event: Event): void {
    NodePathEllipsis.#enableEllipsis(as(event.target, HTMLElement));
  }

  /**
   * Enable ellisifying of nodes
   *
   * @param node
   */
  static #enableEllipsis(node: HTMLElement): void {
    const nodeSpan: HTMLElement = NodePathEllipsis.#getTaxonomyNode(node);
    const textSpan: HTMLElement = NodePathEllipsis.#getNameNode(node);
    if (nodeSpan && textSpan) {
      const width = Ext.fly(textSpan).getAttribute("data-width");
      Ext.fly(nodeSpan).addCls(TaxonomyBEMEntities.NODE_MODIFIER_ELLIPSIS.getCSSClass());
      Ext.fly(textSpan).setStyle("width", width + "px");
    }
  }

  /**
   * Disable ellisifying of nodes
   *
   * @param node
   */
  static #disableEllipsis(node: HTMLElement): void {
    const nodeSpan: HTMLElement = NodePathEllipsis.#getTaxonomyNode(node);
    const textSpan: HTMLElement = NodePathEllipsis.#getNameNode(node);
    if (nodeSpan && textSpan) {
      Ext.fly(textSpan).setStyle("width");
      Ext.fly(nodeSpan).removeCls(TaxonomyBEMEntities.NODE_MODIFIER_ELLIPSIS.getCSSClass());
    }
  }

  /**
   * Get the cm-taxonomy-node__name span
   *
   * @param selection
   * @return
   */
  static #getNameNode(selection: any): any {
    if (is(selection, HTMLElement)) {
      let textSpan: any = selection.getElementsByClassName(TaxonomyBEMEntities.NODE_ELEMENT_NAME.toString())[0];
      if (!textSpan) {
        textSpan = selection;
      }
      return textSpan;
    }
    return null;
  }

  /**
   * Get the cm-taxonomy-node span
   *
   * @param selection
   * @return
   */
  static #getTaxonomyNode(selection: any): any {
    const element = Ext.fly(selection);
    if (element) {
      return element.up(TaxonomyBEMEntities.NODE_BLOCK.getCSSSelector()) || selection;
    }
    return selection;
  }

  /**
   * Called when the taxonomy leaf is not fully visible. Previous elements are hidden then.
   */
  leafMouseOver(): void {
    const componentWidth = this.#getComponentWidth();
    const spanElement = cast(HTMLElement, window.document.getElementById(this.#wrapperId));
    const nodeElements = spanElement.childNodes;
    let actualTotalWidth: number = 0;
    //calculate current with...
    for (let i: uint = 0; i < nodeElements.length; i++) {
      actualTotalWidth += as(nodeElements[i], HTMLElement).offsetWidth;
    }
    actualTotalWidth = actualTotalWidth + 10; //add margin left and right
    if (actualTotalWidth > componentWidth) {
      const margin: number = actualTotalWidth - componentWidth + 25;
      if (spanElement.parentNode) {
        as(spanElement.parentNode, HTMLElement).style.marginLeft = "-" + margin + "px";
      }
    }
  }

  /**
   * Called when the leaf of a link list taxonomy has been moved
   * to the left, so that it is fully visible. On the mouse out event, the previous
   * elements are shown again.
   */
  leafMouseOut(): void {
    const spanElement = cast(HTMLElement, window.document.getElementById(this.#wrapperId));
    if (spanElement && spanElement.parentNode) {
      as(spanElement.parentNode, HTMLElement).style.marginLeft = "0px";
    }
  }

  /**
   * Calculates the available with for component the path should be rendered into.
   * @return The available pixels.
   */
  #getComponentWidth(): number {
    if (this.#fixWidth) {
      return this.#fixWidth;
    }
    return Ext.get(this.#componentId).getWidth();
  }

  /**
   * Add a resize listener to the component, if the component has no fix width.
   */
  #addResizeListener(): void {
    if (!this.#fixWidth) {
      const component = Ext.getCmp(this.#componentId);
      component.removeListener("resize", bind(this, this.autoEllipsis));
      component.addListener("resize", bind(this, this.autoEllipsis));
    }
  }
}

export default NodePathEllipsis;
