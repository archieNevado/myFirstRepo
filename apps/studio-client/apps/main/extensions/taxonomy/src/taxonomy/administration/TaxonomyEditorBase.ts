import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Config from "@jangaroo/runtime/Config";
import TaxonomyNodeFactory from "../TaxonomyNodeFactory";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyUtil from "../TaxonomyUtil";
import TaxonomyEditor from "./TaxonomyEditor";

interface TaxonomyEditorBaseConfig extends Config<Panel> {
}

/**
 * Base class of the taxonomy administration tab.
 */
class TaxonomyEditorBase extends Panel {
  declare Config: TaxonomyEditorBaseConfig;

  #siteSelectionExpression: ValueExpression = null;

  #searchResultExpression: ValueExpression = null;

  constructor(config: Config<TaxonomyEditor> = null) {
    super(config);
  }

  /**
   * The value expression contains the active selected site.
   * @return
   */
  protected getSiteSelectionExpression(): ValueExpression {
    if (!this.#siteSelectionExpression) {
      this.#siteSelectionExpression = ValueExpressionFactory.create("site", beanFactory._.createLocalBean());
    }
    return this.#siteSelectionExpression;
  }

  /**
   * The value expression contains the active search result.
   * @return
   */
  protected getSearchResultExpression(): ValueExpression {
    if (!this.#searchResultExpression) {
      this.#searchResultExpression = ValueExpressionFactory.create("search", beanFactory._.createLocalBean());
    }
    return this.#searchResultExpression;
  }

  /**
   * Displays the path of the given node.
   * @param node
   */
  showNodeSelectedNode(): void {
    const node = TaxonomyUtil.getLatestSelection();
    //show last selected node.
    if (node && !node.isRoot()) { //do not select root node, path retrieving will fail.
      TaxonomyNodeFactory.loadPath(node.getTaxonomyId(), node.getRef(), node.getSite(),
        (nodeList: TaxonomyNodeList): void =>
          this.selectNode(nodeList),
      );
    }
  }

  /**
   * Selects the given node in the tree.
   * @param nodeList
   */
  selectNode(nodeList: TaxonomyNodeList): void {
    this.#searchResultExpression.setValue(nodeList);
  }
}

export default TaxonomyEditorBase;
