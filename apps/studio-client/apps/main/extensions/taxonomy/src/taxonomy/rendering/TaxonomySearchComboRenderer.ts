import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import NodePathEllipsis from "./NodePathEllipsis";
import TaxonomyBEMEntities from "./TaxonomyBEMEntities";
import TaxonomyRenderer from "./TaxonomyRenderer";

/**
 * Renders the search result displayed in the drop down of full text search of link list
 * and in the taxonomy administration.
 */
class TaxonomySearchComboRenderer extends TaxonomyRenderer {
  #wrapperId: string = null;

  static readonly LIST_WIDTH: number = 580;

  constructor(nodes: Array<any>, componentId: string) {
    super(nodes, null);
    this.#wrapperId = (componentId + "-wrapper-" + nodes[nodes.length - 1].ref).replace(/\//g, "-");
  }

  protected override doRenderInternal(nodes: Array<any>, callback: AnyFunction): void {
    let html = "<div class=\"" + TaxonomyBEMEntities.NODE_WRAP + "\" id=\"" + this.#wrapperId + "\" aria-label=\"" + this.createAriaLabel(nodes) + "\">";

    for (let i = 1; i < nodes.length; i++) {
      const node = new TaxonomyNode(nodes[i]);
      const isLeaf: boolean = i === (nodes.length - 1);
      html += this.renderNode(node, !isLeaf, false, undefined, false);
    }

    html += "</div>";
    this.setHtml(html);

    const that = this;
    EventUtil.invokeLater((): void =>
      new NodePathEllipsis(this.#wrapperId, that, 580).autoEllipsis(),
    );
  }
}

export default TaxonomySearchComboRenderer;
