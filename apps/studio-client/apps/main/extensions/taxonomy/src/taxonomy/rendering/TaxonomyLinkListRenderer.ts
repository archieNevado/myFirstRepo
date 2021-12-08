import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import NodePathEllipsis from "./NodePathEllipsis";
import TaxonomyBEMEntities from "./TaxonomyBEMEntities";
import TaxonomyRenderer from "./TaxonomyRenderer";

/**
 * The renderer used for the regular taxonomy link lists (property editor).
 */
class TaxonomyLinkListRenderer extends TaxonomyRenderer {
  #wrapperId: string = null;

  constructor(nodes: Array<any>, componentId: string) {
    super(nodes, componentId);
    this.#wrapperId = (componentId + "-wrapper-" + nodes[nodes.length - 1].ref).replace(/\//g, "-");
  }

  protected override doRenderInternal(nodes: Array<any>, callback: AnyFunction): void {
    let html = "<div class=\"" + TaxonomyBEMEntities.NODE_WRAP + "\" style=\"text-align:left;\" id=\"" + this.#wrapperId + "\" aria-label=\"" + this.createAriaLabel(nodes) + "\">";

    for (let i = 1; i < nodes.length; i++) {
      const node = new TaxonomyNode(nodes[i]);
      const isLeaf: boolean = (i === nodes.length - 1);
      let addButton: boolean = undefined;
      if (isLeaf) {
        addButton = this.plusMinus();
      }
      const selected = this.isSelected(node, isLeaf);

      html += this.renderNode(node, !isLeaf, false, addButton, selected);
    }

    html += "</div>";
    callback.call(null, html);

    const that = this;
    //well...don't ask
    EventUtil.invokeLater((): void =>
      EventUtil.invokeLater((): void =>
        new NodePathEllipsis(this.#wrapperId, that).autoEllipsis(),
      ),
    );
  }

  protected isSelected(node: TaxonomyNode, isLeaf: boolean): boolean {
    return isLeaf;
  }

  protected plusMinus(): boolean {
    return false;
  }
}

export default TaxonomyLinkListRenderer;
