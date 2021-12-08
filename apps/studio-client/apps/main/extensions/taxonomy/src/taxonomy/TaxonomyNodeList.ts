import int from "@jangaroo/runtime/int";
import TaxonomyNode from "./TaxonomyNode";
import TaxonomyUtil from "./TaxonomyUtil";

/**
 * JSON representation fora list of nodes.
 * The list can be a search result or a list of nodes that represent the path of a node.
 */
class TaxonomyNodeList {

  #nodes: Array<any> = null;

  #json: Array<any> = null;

  constructor(object: Array<any>) {
    this.#nodes = [];
    for (let i = 0; i < object.length; i++) {
      const node = new TaxonomyNode(object[i]);
      this.#nodes.push(node);
    }
    this.#json = object;
  }

  getPath(): string {
    return this.#json["path"];
  }

  toJson(): Array<any> {
    return this.#json;
  }

  size(): int {
    return this.#nodes.length;
  }

  setNodes(nodesArray: Array<any>): void {
    this.#nodes = nodesArray;
  }

  getNode(ref: string): TaxonomyNode {
    for (let i = 0; i < this.#nodes.length; i++) {
      if (this.#nodes[i].getRef() === ref) {
        return this.#nodes[i];
      }
    }
    return null;
  }

  getNodeForDisplayName(name: string): TaxonomyNode {
    for (let i = 0; i < this.#nodes.length; i++) {
      const hit: TaxonomyNode = this.#nodes[i];
      if (hit.getDisplayName() === TaxonomyUtil.escapeHTML(name)) {
        return hit;
      }
    }
    return null;
  }

  getNodes(): Array<any> {
    return this.#nodes;
  }

  getLeafRef(): string {
    return this.#nodes[this.#nodes.length - 1].getRef();
  }

  getLeafParentRef(): string {
    return this.#nodes[this.#nodes.length - 2].getRef();
  }
}

export default TaxonomyNodeList;
