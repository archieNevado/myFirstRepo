import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import SelectionListRenderer from "./SelectionListRenderer";

/**
 * Renderer is used for the leaf list in the selection dialog when there is only single selection allowed.
 * Since there are no path information shown, we only have to render
 * the leaf itself. We re-use the link list renderer again, since the leaf
 * layout matches the one of the regular taxonomy link lists.
 */
class SingleSelectionListRenderer extends SelectionListRenderer {
  #selected: boolean = false;

  #selectionExists: boolean = false;

  constructor(nodes: Array<any>, componentId: string, selected: boolean, selectionExists: boolean) {
    super(nodes, componentId, selected);
    this.#selected = selected;
    this.#selectionExists = selectionExists;
  }

  protected override doRenderInternal(nodes: Array<any>, callback: AnyFunction): void {
    let html = "";
    const node: TaxonomyNode = nodes[0];
    let addButton: boolean = undefined;
    if (this.#selected) {
      addButton = false;
    } else if (!this.#selectionExists) {
      addButton = true;
    }
    html += this.renderNode(node, false, !node.isLeaf(), addButton, this.#selected);
    this.setHtml(html);
  }

}

export default SingleSelectionListRenderer;
