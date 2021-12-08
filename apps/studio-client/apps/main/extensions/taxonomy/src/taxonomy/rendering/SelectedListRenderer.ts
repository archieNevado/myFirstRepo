import TaxonomyLinkListRenderer from "./TaxonomyLinkListRenderer";

/**
 * The renderer used for the taxonomy link lists in the selection dialog (upper list).
 */
class SelectedListRenderer extends TaxonomyLinkListRenderer {
  #scrolling: boolean = false;

  constructor(nodes: Array<any>, componentId: string, scrolling: boolean) {
    super(nodes, componentId);
    this.#scrolling = scrolling;
  }

  override isScrollable(): boolean {
    return true;
  }
}

export default SelectedListRenderer;
