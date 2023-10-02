import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import SearchFilter from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchFilter";
import Container from "@jangaroo/ext-ts/container/Container";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, bind, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import trace from "@jangaroo/runtime/trace";
import FacetFilterFieldWrapper from "./FacetFilterFieldWrapper";
import FacetFilterStateBean from "./FacetFilterStateBean";
import FacetUtil from "./FacetUtil";
import FacetsFilterPanel from "./FacetsFilterPanel";

interface FacetsFilterPanelBaseConfig extends Config<Panel>, Partial<Pick<FacetsFilterPanelBase,
  "filterId"
>> {
}

class FacetsFilterPanelBase extends Panel implements SearchFilter {
  declare Config: FacetsFilterPanelBaseConfig;

  #categoryExpression: ValueExpression = null;

  #facetsExpression: ValueExpression = null;

  #selectedFacetsExpression: ValueExpression = null;

  #activeStateExpression: ValueExpression = null;

  #stateBean: Bean = null;

  /**
   * The filter ID for this filter. It is used as itemId and identifier in saved searches.
   */
  filterId: string = null;

  constructor(config: Config<FacetsFilterPanel> = null) {
    super(config);
  }

  protected override afterRender(): void {
    super.afterRender();
    this.getFacetsExpression().addChangeListener(bind(this, this.#facetsChanged));
  }

  getStateBean(): Bean {
    if (!this.#stateBean) {
      this.#stateBean = new FacetFilterStateBean();
    }
    return this.#stateBean;
  }

  #getFacetFilterStateBean(): FacetFilterStateBean {
    return as(this.getStateBean(), FacetFilterStateBean);
  }

  getFilterId(): string {
    return this.getItemId();
  }

  transformState(state: any): any {
    const updatedFacets = [];
    this.getFacetsExpression().loadValue((facets: Array<any>): void => {
      for (const m in state) {
        const facet = FacetUtil.findFacetForKey(facets, m);
        if (facet) {
          updatedFacets.push(facet);
          this.getStateBean().set(m, state[m]);
        } else {
          trace("[WARN]", "Could not find search facet " + m + " to restore filter state.");
        }
      }
      this.getSelectedFacetsExpression().setValue(updatedFacets);
    });
  }

  getDefaultState(): any {
    const state: Record<string, any> = {};
    return state;
  }

  #getCategoryExpression(): ValueExpression {
    if (!this.#categoryExpression) {
      this.#categoryExpression = ValueExpressionFactory.create(CollectionViewModel.FOLDER_PROPERTY, CollectionViewModel.lookupCollectionViewModel(this).getMainStateBean());
    }
    return this.#categoryExpression;
  }

  protected getSelectedFacetsExpression(): ValueExpression {
    if (!this.#selectedFacetsExpression) {
      this.#selectedFacetsExpression = ValueExpressionFactory.createFromValue([]);
      this.#selectedFacetsExpression.addChangeListener(bind(this, this.#selectedFacetsChanged));
    }
    return this.#selectedFacetsExpression;
  }

  /**
   * Remove filters from state that are not selected anymore.
   * @param ve
   */
  #selectedFacetsChanged(ve: ValueExpression): void {
    const facetSelection: Array<any> = ve.getValue();
    const state = this.getStateBean().toObject();
    for (const m in state) {
      const facet = FacetUtil.findFacetForKey(facetSelection, m);
      if (!facet) {
        this.#getFacetFilterStateBean().remove(m);
      }
    }
  }

  buildQuery(): string {
    //not used, we access this filter state directly instead
    return "";
  }

  protected getActiveStateExpression(): ValueExpression {
    if (!this.#activeStateExpression) {
      this.#activeStateExpression = ValueExpressionFactory.createFromFunction((): string => {
        const selection: Array<any> = this.getSelectedFacetsExpression().getValue();
        const facets: Array<any> = this.getFacetsExpression().getValue();

        if (selection && selection.length > 0) {
          return FacetsFilterPanel.FILTER_FACETS_ITEM_ID;
        }

        if (facets && facets.length === 0) {
          return FacetsFilterPanel.DISABLED_ITEM_ID;
        }

        return FacetsFilterPanel.EMPTY_ITEM_ID;
      });
    }
    return this.#activeStateExpression;
  }

  protected getFacetsExpression(): ValueExpression {
    if (!this.#facetsExpression) {
      this.#facetsExpression = ValueExpressionFactory.createFromFunction((): Array<any> => {
        //the value can be a store or other node elements too!
        const category = as(this.#getCategoryExpression().getValue(), Category);
        if (!category) {
          return [];
        }

        if (!category.isLoaded()) {
          category.load();
          return undefined;
        }

        const searchFacets = category.getSearchFacets();
        if (searchFacets === null) {
          return [];
        }

        if (!searchFacets.isLoaded()) {
          searchFacets.load();
          return undefined;
        }
        const categoryFacets: any = category.getSearchFacets().getFacets();
        if (categoryFacets === undefined) {
          return undefined;
        }

        return categoryFacets || [];
      });
    }
    return this.#facetsExpression;
  }

  protected resetAllFilters(): void {
    const container = as(this.queryById("facetsContainer"), Container);
    container.items.each((f: FacetFilterFieldWrapper): void =>
      f.reset(),
    );
  }

  protected removeFromSelection(removedFacet: Facet): void {
    const selection: Array<any> = this.getSelectedFacetsExpression().getValue();
    const updated = selection.filter((f: Facet): boolean =>
      removedFacet.getKey() !== f.getKey(),
    );
    this.getSelectedFacetsExpression().setValue(updated);
  }

  protected emptyTransformer(values: Array<any>): boolean {
    return !values || values.length === 0;
  }

  #facetsChanged(ve: ValueExpression): void {
    const updatedFacets: Array<any> = ve.getValue();
    if (updatedFacets === undefined) {
      return;
    }

    const selectedFacets: Array<any> = this.getSelectedFacetsExpression().getValue();
    const filtered = selectedFacets.filter((f: Facet): boolean => {
      for (const facet of updatedFacets as Facet[]) {
        if (f.getKey() === facet.getKey()) {
          return true;
        }
      }
      return false;
    });
    this.getSelectedFacetsExpression().setValue(filtered);
  }

  protected override onDestroy(): void {
    this.getFacetsExpression().removeChangeListener(bind(this, this.#facetsChanged));
    this.getSelectedFacetsExpression().removeChangeListener(bind(this, this.#selectedFacetsChanged));

    super.onDestroy();
  }
}
mixin(FacetsFilterPanelBase, SearchFilter);

export default FacetsFilterPanelBase;
