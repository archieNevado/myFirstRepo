import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyLinkListRenderer from "./TaxonomyLinkListRenderer";

/**
 * The renderer used for rendering the suggestions.
 * The rendering is almost the same like for regular link lists, so we
 * extend the link list renderer and modify the leaf rendering.
 */
class SuggestionsRenderer extends TaxonomyLinkListRenderer {
  #weight: string = null;

  #leaf: any = null;

  constructor(nodes: Array<any>, componentId, weight: string) {
    super(nodes, componentId);
    this.#weight = weight;
    this.#leaf = nodes[nodes.length - 1];
  }

  protected override doRenderInternal(nodes: Array<any>, callback: AnyFunction): void {
    super.doRenderInternal(nodes, callback);
  }

  protected override isSelected(node: TaxonomyNode, isLeaf: boolean): boolean {
    return false;
  }

  protected override plusMinus(): boolean {
    return true;
  }

  /**
   * Overwrites the name rendering to add the weight
   * information calculated by the suggestions plugin.
   * @param node The node to render the name for.
   * @return
   */
  protected override getLeafName(node: TaxonomyNode): string {
    let name = super.getLeafName(node);
    if (this.#weight && node.getRef() === this.#leaf.ref) {
      name += " (" + this.#weight + ")";
    }
    return name;
  }
}

export default SuggestionsRenderer;
