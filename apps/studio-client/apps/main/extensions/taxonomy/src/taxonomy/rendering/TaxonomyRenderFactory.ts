import TaxonomyNode from "../TaxonomyNode";
import SelectedListRenderer from "./SelectedListRenderer";
import SelectedListWithoutPathRenderer from "./SelectedListWithoutPathRenderer";
import SelectionListRenderer from "./SelectionListRenderer";
import SingleSelectionListRenderer from "./SingleSelectionListRenderer";
import SuggestionsRenderer from "./SuggestionsRenderer";
import TaxonomyLinkListRenderer from "./TaxonomyLinkListRenderer";
import TaxonomyRenderer from "./TaxonomyRenderer";
import TaxonomySearchComboRenderer from "./TaxonomySearchComboRenderer";

/**
 * Factory class for creating new renderers for the different taxonomy renderings.
 */
class TaxonomyRenderFactory {

  static createSearchComboRenderer(nodes: Array<any>, componentId: string): TaxonomyRenderer {
    return new TaxonomySearchComboRenderer(nodes, componentId);
  }

  static createLinkListRenderer(nodes: Array<any>, componentId: string): TaxonomyRenderer {
    return new TaxonomyLinkListRenderer(nodes, componentId);
  }

  static createSelectedListRenderer(nodes: Array<any>, componentId: string, scrolling: boolean): TaxonomyRenderer {
    return new SelectedListRenderer(nodes, componentId, scrolling);
  }

  static createSelectedListWithoutPathRenderer(nodes: Array<any>, componentId: string, scrolling: boolean): TaxonomyRenderer {
    return new SelectedListWithoutPathRenderer(nodes, componentId, scrolling);
  }

  static createSuggestionsRenderer(nodes: Array<any>, componentId: string, weight: string): TaxonomyRenderer {
    return new SuggestionsRenderer(nodes, componentId, weight);
  }

  static createSelectionListRenderer(node: TaxonomyNode, componentId: string, selected: boolean): TaxonomyRenderer {
    return new SelectionListRenderer([node], componentId, selected);
  }

  static createSingleSelectionListRenderer(node: TaxonomyNode, componentId: string, selected: boolean, selectionExists: boolean): TaxonomyRenderer {
    return new SingleSelectionListRenderer([node], componentId, selected, selectionExists);
  }
}

export default TaxonomyRenderFactory;
