import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import { as } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyNodeFactory from "../TaxonomyNodeFactory";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyUtil from "../TaxonomyUtil";

class TaxonomyCache {
  #taxonomyIdExpression: ValueExpression = null;

  #activeContentVE: ValueExpression = null;

  #cachedSuggestions: TaxonomyNodeList = null;

  #pvExpression: ValueExpression = null;

  constructor(contentVE: ValueExpression, propertyValueExpression: ValueExpression, taxIdExpression: ValueExpression) {
    this.#pvExpression = propertyValueExpression;
    this.#activeContentVE = contentVE;
    this.#taxonomyIdExpression = taxIdExpression;
  }

  /**
   * Invalidates the cached result and re-requests the suggestion
   * list for the given content.
   * @param callback The callback handler that processes the suggestions list.
   */
  invalidate(callback: AnyFunction): void {
    const content = as(this.#activeContentVE.getValue(), Content);
    if (!content) {
      return;
    }

    this.#taxonomyIdExpression.loadValue((taxId: string): void =>
      TaxonomyNodeFactory.loadSuggestions(taxId, content, (nodeList: TaxonomyNodeList): void => {
        this.#cachedSuggestions = nodeList;
        callback(this.#getActiveSuggestions());
      }),
    );
  }

  /**
   * Returns a subset of the suggestions.
   * @return
   */
  #getActiveSuggestions(): TaxonomyNodeList {
    if (this.#cachedSuggestions) {
      const json = this.#cachedSuggestions.toJson();
      const nodes = [];
      for (let i = 0; i < json.length; i++) {
        const node = new TaxonomyNode(json[i]);
        if (!this.isInTaxonomyList(node)) {
          nodes.push(node);
        }
      }
      const taxList = new TaxonomyNodeList(json);
      taxList.setNodes(nodes);
      return taxList;
    } else {
      return null;
    }
  }

  /**
   * Returns true if the given node is already added a keyword
   * for the active content.
   * @param node
   * @return
   */
  isInTaxonomyList(node: TaxonomyNode): boolean {
    const items: Array<any> = this.#pvExpression.getValue();
    if (items) {
      for (let i = 0; i < items.length; i++) {
        const child: Content = items[i];
        const childId = TaxonomyUtil.parseRestId(child);
        if (childId === node.getRef()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns the active suggestions list.
   * @param callback
   */
  loadSuggestions(callback: AnyFunction): void {
    callback(this.#getActiveSuggestions());
  }

  /**
   * Returns the weight of the
   * @param id
   * @return
   */
  getWeight(id: string): string {
    if (this.#cachedSuggestions) {
      id = TaxonomyUtil.getRestIdFromCapId(id);
      const node = this.#cachedSuggestions.getNode(id);
      if (node) {
        return node.getWeight();
      }
    }
    return null;
  }
}

export default TaxonomyCache;
