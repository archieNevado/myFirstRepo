import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import SelectedListRenderer from "./SelectedListRenderer";

/**
 * The renderer used for the taxonomy filter in the library.
 */
class SelectedListWithoutPathRenderer extends SelectedListRenderer {

  constructor(nodes: Array<any>, componentId: string, scrolling: boolean) {
    super(nodes, componentId, scrolling);
  }

  protected override doRenderInternal(nodes: Array<any>, callback: AnyFunction): void {
    let html = "";
    const nodeObject: any = nodes[nodes.length - 1];
    const node = new TaxonomyNode(nodeObject);
    html += this.renderNode(node, false, false, false, false);
    callback.call(null, html);
  }
}

export default SelectedListWithoutPathRenderer;
