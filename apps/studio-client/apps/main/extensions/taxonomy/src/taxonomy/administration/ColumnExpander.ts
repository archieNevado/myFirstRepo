import Ext from "@jangaroo/ext-ts";
import { as } from "@jangaroo/runtime";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyExplorerPanel from "./TaxonomyExplorerPanel";

/**
 * Delayed expanding of a column on mouse over.
 * The expanding may be cancelled when another node was hovered.
 */
class ColumnExpander {
  #targetNode: TaxonomyNode = null;

  #cancelled: boolean = false;

  constructor(targetNode: TaxonomyNode) {
    this.#targetNode = targetNode;
  }

  cancel(): void {
    this.#cancelled = true;
  }

  expand(): void {
    window.setTimeout((): void => {
      if (!this.#cancelled) {
        const taxonomyExplorer = as(Ext.getCmp("taxonomyExplorerPanel"), TaxonomyExplorerPanel);
        taxonomyExplorer.updateColumns(this.#targetNode);
      }
    }, 500);
  }

  /**
   * Checks if this expander expands the given node.
   * @param targetNode
   * @return
   */
  expands(node: TaxonomyNode): boolean {
    return this.#targetNode.getRef() === node.getRef();
  }
}

export default ColumnExpander;
