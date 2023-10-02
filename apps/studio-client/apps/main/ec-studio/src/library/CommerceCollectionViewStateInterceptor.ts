import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import AsyncObserver from "@coremedia/studio-client.client-core/util/AsyncObserver";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import MessageBoxUtilInternal from "@coremedia/studio-client.ext.ui-components/messagebox/MessageBoxUtilInternal";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import DeleteSavedSearchActionBase from "@coremedia/studio-client.main.editor-components/sdk/actions/DeleteSavedSearchActionBase";
import CollectionViewStateInterceptor from "@coremedia/studio-client.main.editor-components/sdk/desktop/CollectionViewStateInterceptor";
import SavedSearchModel from "@coremedia/studio-client.main.editor-components/sdk/desktop/SavedSearchModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import sitesService from "@coremedia/studio-client.multi-site-models/global/sitesService";
import { as, is, mixin } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import ECommerceStudioPlugin_properties from "../ECommerceStudioPlugin_properties";
import CatalogSearchFilters from "../components/search/CatalogSearchFilters";
import FacetUtil from "../components/search/filters/FacetUtil";

/**
 * Checks if the content folder of the saved search is still valid.
 */
class CommerceCollectionViewStateInterceptor implements CollectionViewStateInterceptor {
  constructor() {
  }

  intercept(state: SavedSearchModel, callback: AnyFunction): void {
    if (!this.#isApplicable(state)) {
      callback(state);
      return;
    }

    const siteId = state.getSiteId();
    const folder: RemoteBean = state.getFolder();

    if (siteId && siteId !== sitesService._.getPreferredSiteId()) {
      const title = Editor_properties.saveSearch_invalidSite_title;
      const msg = Editor_properties.saveSearch_invalidSite_text;

      const buttons: Record<string, any> = {
        yes: Editor_properties.saveSearch_invalidSite_change_site_btn_text,
        cancel: Editor_properties.dialog_defaultCancelButton_text,
      };
      MessageBoxUtilInternal.show(title, msg, null, buttons, this.#getSwitchSiteCallback(state, callback));
    } else {
      this.#checkFilterState(as(folder, Category), state, callback);
    }
  }

  #isApplicable(state: SavedSearchModel): boolean {
    const folder: RemoteBean = state.getFolder();
    const name = state.getName();
    return name && is(folder, Category);
  }

  /**
   * Changes the preferred site and waits for all pending processes to be finished.
   * Afterwards, we check the filter status.
   *
   * @param state the persisted search filter state
   * @param callback the callback which applies the state to the library
   * @return
   */
  #getSwitchSiteCallback(state: SavedSearchModel, callback: AnyFunction): AnyFunction {
    return (btn: string): void => {
      if (btn === "yes") {
        const site = sitesService._.getSite(state.getSiteId());
        editorContext._.getSitesService().getPreferredSiteIdExpression().setValue(site.getId());

        //we need the minimal setup loaded so that the filter is activated afterwards
        EventUtil.invokeLater((): void =>
          //we can't determine when the library mode switch is finished, so we wait for all other stuff to be completed
          AsyncObserver.complete((): void =>
            ValueExpressionFactory.createFromFunction((): RemoteBean => {
              if (sitesService._.getPreferredSiteId() !== site.getId()) {
                return undefined;
              }

              const root = sitesService._.getPreferredSite().getSiteRootFolder();
              if (!root.isLoaded() || !root.getPath()) {
                return undefined;
              }

              return state.getFolder();
            }).loadValue((bean: RemoteBean): void =>
              this.#checkFilterState(as(bean, Category), state, callback),
            ),
          ),
        );
      }
    };
  }

  /**
   * Validates the persisted commerce search facets against the actual values of the selected store.
   *
   * @param category
   * @param state
   * @param callback
   */
  #checkFilterState(category: Category, state: SavedSearchModel, callback: AnyFunction): void {
    //load facet values first
    ValueExpressionFactory.createFromFunction((): Array<any> => {
      const searchFacets = category.getSearchFacets();
      if (searchFacets === null) {
        return null;
      }

      if (!searchFacets.isLoaded()) {
        searchFacets.load();
        return undefined;
      }

      return searchFacets.getFacets();
    }).loadValue((facets: Array<any>): void => {
      //then validate all values
      if (!this.#validateFilterState(facets, state)) {
        const title = ECommerceStudioPlugin_properties.saveSearch_invalidFacets_title;
        const msg = ECommerceStudioPlugin_properties.saveSearch_invalidFacets_text;

        const buttons: Record<string, any> = {
          yes: ECommerceStudioPlugin_properties.saveSearch_invalidFacets_delete_btn_text,
          no: ECommerceStudioPlugin_properties.saveSearch_invalidFacets_clear_btn_text,
          cancel: Editor_properties.dialog_defaultCancelButton_text,
        };
        MessageBoxUtilInternal.show(title, msg, null, buttons, this.#getInvalidFacetCallback(facets, state, callback));
      } else {
        callback(state);
      }
    });
  }

  /**
   * Validates if the stored facet and its query values are still available.
   *
   * @param facets the list of facets to validate against
   * @param state the search filter to validate
   * @return
   */
  #validateFilterState(facets: Array<any>, state: SavedSearchModel): any {
    const commerceFilterState: any = state.getData()[CatalogSearchFilters.FACET_FILTER_ID];
    for (const m in commerceFilterState) {
      const selectedValues: Array<any> = commerceFilterState[m];
      const facet = FacetUtil.findFacetForKey(facets, m);
      if (!facet) {
        return false;
      }

      for (const v of selectedValues as string[]) {
        if (!FacetUtil.validateFacetValue(facet, v)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Removes the invalid values from the commerce saved search.
   *
   * @param facets the list of facets to validate against
   * @param state the search filter to validate
   *
   * @return the updated search state
   */
  #clearInvalidValues(facets: Array<any>, state: SavedSearchModel): any {
    const commerceFilterState: any = state.getData()[CatalogSearchFilters.FACET_FILTER_ID];
    for (const m in commerceFilterState) {
      const selectedValues: Array<any> = commerceFilterState[m];
      const facet = FacetUtil.findFacetForKey(facets, m);
      if (!facet) {
        delete commerceFilterState[m];
        continue;
      }

      const updated = [];
      for (const v of selectedValues as string[]) {
        if (FacetUtil.validateFacetValue(facet, v)) {
          updated.push(v);
        }
      }

      commerceFilterState[m] = updated;
    }
    return state;
  }

  /**
   * The action handler for the invalid facet dialog.
   *
   * @param facets the list of facets to validate against
   * @param state the search filter to validate
   * @param callback the callback to invoke with the updated search state
   * @return
   */
  #getInvalidFacetCallback(facets: Array<any>, state: SavedSearchModel, callback: AnyFunction): AnyFunction {
    return (btn: string): void => {
      if (btn === "yes") {
        const name = state.getName();
        DeleteSavedSearchActionBase.deleteSearch(name);
      } else if (btn === "no") {
        const updated = this.#clearInvalidValues(facets, state);
        callback(updated);
      }
    };
  }
}
mixin(CommerceCollectionViewStateInterceptor, CollectionViewStateInterceptor);

export default CommerceCollectionViewStateInterceptor;
