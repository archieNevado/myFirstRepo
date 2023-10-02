import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyRenderer from "./TaxonomyRenderer";

/**
 * Renderer is used for the leaf list in the selection dialog.
 * Since there are no path information shown, we only have to render
 * the leaf itself.
 */
class SelectionListRenderer extends TaxonomyRenderer {
  #selected: boolean = false;

  constructor(nodes: Array<any>, componentId: string, selected: boolean) {
    super(nodes, componentId);
    this.#selected = selected;
  }

  protected override doRenderInternal(nodes: Array<any>, callback: AnyFunction): void {
    let html = "";
    const node: TaxonomyNode = nodes[0];
    const addButton: boolean = !this.#selected;
    html += this.renderNode(node, false, !node.isLeaf(), addButton, this.#selected);
    this.setHtml(html);
  }
}

export default SelectionListRenderer;
