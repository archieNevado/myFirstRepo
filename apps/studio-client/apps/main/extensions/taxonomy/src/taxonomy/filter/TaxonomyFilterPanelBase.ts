import IdHelper from "@coremedia/studio-client.cap-rest-client/common/IdHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import UndocContentUtil from "@coremedia/studio-client.cap-rest-client/content/UndocContentUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import FilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/FilterPanel";
import StringUtil from "@jangaroo/ext-ts/String";
import ExtEvent from "@jangaroo/ext-ts/event/Event";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import KeyEvent from "@jangaroo/runtime/KeyEvent";
import int from "@jangaroo/runtime/int";
import uint from "@jangaroo/runtime/uint";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyUtil from "../TaxonomyUtil";
import TaxonomyRenderFactory from "../rendering/TaxonomyRenderFactory";

interface TaxonomyFilterPanelBaseConfig extends Config<FilterPanel>, Partial<Pick<TaxonomyFilterPanelBase,
  "filterId" |
  "taxonomyId" |
  "propertyName" |
  "siteSelectionExpression"
>> {
}

/**
 * The non-UI part of a filter for the collection view that allows to select
 * the taxonomies of documents to be included in the search result.
 */
class TaxonomyFilterPanelBase extends FilterPanel {
  declare Config: TaxonomyFilterPanelBaseConfig;

  static readonly TAXONOMY_NODE_GRID_ITEM_ID: string = "taxonomyFilterSelection";

  /**
   *  The filter ID for this filter. It is used as identifier in saved searches.
   */
  filterId: string = null;

  /**
   * The filter property storing the keyword.
   */
  static readonly TAXONOMIES_PROPERTY: string = "taxonomies";

  /**
   *  The taxonomy id to use for the selection.
   */
  taxonomyId: string = null;

  /**
   * The name of the SOLR field to apply the search for.
   */
  propertyName: string = null;

  /**
   * Contains the active selection.
   */
  #selectionExpression: ValueExpression = null;

  /**
   * Contains the search result.
   */
  #searchResultExpression: ValueExpression = null;

  /**
   * Contains the optional siteId of the taxonomy.
   */
  siteSelectionExpression: ValueExpression<string> = null;

  /**
   * Create a new filter panel.
   *
   * @param config the configuration
   */
  constructor(config: Config<TaxonomyFilterPanelBase> = null) {
    super(config);

    // Update the UI once and after state changes.
    this.getStateBean().addValueChangeListener(bind(this, this.#stateBeanChanged));
    this.#stateBeanChanged();
  }

  protected override afterRender(): void {
    super.afterRender();
    this.addListener("afterlayout", bind(this, this.#addKeyListener));
  }

  #addKeyListener(): void {
    this.removeListener("afterlayout", bind(this, this.#addKeyListener));
    const grid = as(this.query(createComponentSelector().itemId(TaxonomyFilterPanelBase.TAXONOMY_NODE_GRID_ITEM_ID).build())[0], GridPanel);
    grid.getEl().addListener("keyup", (evt: ExtEvent, el: HTMLElement): any => {
      if (evt.getKey() === KeyEvent.DOM_VK_DELETE ||
              evt.getKey() === KeyEvent.DOM_VK_ENTER ||
              evt.getKey() === KeyEvent.DOM_VK_RETURN ||
              evt.getKey() === KeyEvent.DOM_VK_SPACE) {
        const values: Array<any> = this.getSelectionExpression().getValue();
        for (const selection of values as Content[]) {
          const ref = TaxonomyUtil.parseRestId(selection);
          this.plusMinusClicked(ref);
        }
      }
    });
  }

  /**
   * The model has changed. Update the UI.
   */
  #stateBeanChanged(): void {
    const stateBean = this.getStateBean();
    const selection: Array<any> = stateBean.get(TaxonomyFilterPanelBase.TAXONOMIES_PROPERTY) || [];

    const currentTaxonomies = [];
    for (let i: uint = 0; i < selection.length; i++) {
      const content: Content = selection[i];
      currentTaxonomies.push(content);
    }
    this.getSelectionExpression().setValue(currentTaxonomies);
  }

  /**
   * Called when the user has made a selection.
   */
  #selectionChanged(): void {
    const selection: Array<any> = this.getSelectionExpression().getValue();
    this.getStateBean().set(TaxonomyFilterPanelBase.TAXONOMIES_PROPERTY, selection);
  }

  /**
   * Returns the value expression that contains the active selection.
   * @return
   */
  protected getSelectionExpression(): ValueExpression {
    if (!this.#selectionExpression) {
      this.#selectionExpression = ValueExpressionFactory.create("selection", beanFactory._.createLocalBean());
      this.#selectionExpression.addChangeListener(bind(this, this.#selectionChanged));
    }
    return this.#selectionExpression;
  }

  /**
   * Returns the value expression that contains the current search result.
   * @return
   */
  protected getSearchResultExpression(): ValueExpression {
    if (!this.#searchResultExpression) {
      this.#searchResultExpression = ValueExpressionFactory.create("search", beanFactory._.createLocalBean());
      this.#searchResultExpression.addChangeListener((): void => {
        const selection = as(this.#searchResultExpression.getValue(), TaxonomyNodeList);
        if (selection) {
          const leafRef = selection.getLeafRef();
          const keyword = UndocContentUtil.getContent(leafRef);
          keyword.load((): void => {
            let values: Array<any> = this.#selectionExpression.getValue();
            if (values.indexOf(keyword) === -1) {
              values = values.concat(keyword);
            }
            this.#selectionExpression.setValue(values);
          });
        }
      });
    }
    return this.#searchResultExpression;
  }

  /**
   * Removes the given taxonomy. Invoked from the rendered selection.
   */
  plusMinusClicked(nodeRef: string): void {
    TaxonomyUtil.removeNodeFromSelection(this.#selectionExpression, nodeRef);
  }

  /**
   * Displays each path item of a taxonomy
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected taxonomyRenderer(value: any, metaData: any, record: BeanRecord): string {
    TaxonomyUtil.loadTaxonomyPath(record, null, this.taxonomyId, (updatedRecord: BeanRecord): void => {
      const renderer = TaxonomyRenderFactory.createSelectedListWithoutPathRenderer(record.data.nodes, this.getId(), false);
      renderer.doRender((html: string): void => {
        if (record.data.html !== html) {
          record.data.html = html;
          record.commit(false);
        }
      });
    });
    if (!record.data.html) {
      return "<div class='loading'>" + TaxonomyStudioPlugin_properties.TaxonomyLinkList_status_loading_text + "</div>";
    }
    return record.data.html;
  }

  /**
   * @inheritDoc
   */
  override buildQuery(): string {
    const stateBean = this.getStateBean();
    const keywords: Array<any> = stateBean.get(TaxonomyFilterPanelBase.TAXONOMIES_PROPERTY) || [];
    if (keywords.length === 0) {
      // The entire filter can be omitted.
      return null;
    } else {
      const queryTerms = [];
      for (let i: uint = 0; i < keywords.length; i++) {
        const keyword: Content = keywords[i];
        const param: int = IdHelper.parseContentId(keyword);
        queryTerms.push(StringUtil.format(this.propertyName.toLowerCase() + ":{0}", param));
      }
      return queryTerms.join(" OR ");
    }
  }

  /**
   * @inheritDoc
   */
  override getActiveFilterCount(): number {
    const stateBean = this.getStateBean();
    const keywords: Array<any> = stateBean.get(TaxonomyFilterPanelBase.TAXONOMIES_PROPERTY) || [];
    return keywords.length;
  }

  /**
   * @inheritDoc
   */
  override getDefaultState(): any {
    const state: Record<string, any> = {};
    state[TaxonomyFilterPanelBase.TAXONOMIES_PROPERTY] = [];
    return state;
  }

  /**
   * @inheritDoc
   */
  override getFilterId(): string {
    if (!this.filterId) {
      throw new Error("filterId has not been set for taxonomy filter");
    }
    return this.filterId;
  }
}

export default TaxonomyFilterPanelBase;
