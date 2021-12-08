import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import FolderContentSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/FolderContentSwitchingContainer";
import ICollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/ICollectionView";
import RepositoryToolbarSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/RepositoryToolbarSwitchingContainer";
import SearchFiltersSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SearchFiltersSwitchingContainer";
import SearchSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SearchSwitchingContainer";
import SearchToolbarSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SearchToolbarSwitchingContainer";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogRepositoryListContainer from "../components/repository/CatalogRepositoryListContainer";
import CatalogRepositoryToolbarContainer from "../components/repository/CatalogRepositoryToolbarContainer";
import CatalogSearchFilters from "../components/search/CatalogSearchFilters";
import CatalogSearchListContainer from "../components/search/CatalogSearchListContainer";
import CatalogSearchToolbarContainer from "../components/search/CatalogSearchToolbarContainer";

interface ECommerceLibraryPluginConfig extends Config<NestedRulesPlugin> {
}

class ECommerceLibraryPlugin extends NestedRulesPlugin {
  declare Config: ECommerceLibraryPluginConfig;

  #selectionHolder: ICollectionView = null;

  constructor(config: Config<ECommerceLibraryPlugin> = null) {
    super((()=>{
      this.#selectionHolder = as(config.cmp, ICollectionView);
      return ConfigUtils.apply(Config(ECommerceLibraryPlugin, {

        rules: [

          Config(FolderContentSwitchingContainer, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(CatalogRepositoryListContainer, {
                    selectedItemsValueExpression: this.#selectionHolder.getSelectedItemsValueExpression(),
                    selectedFolderValueExpression: this.#selectionHolder.getSelectedFolderValueExpression(),
                    selectedRepositoryItemsValueExpression: this.#selectionHolder.getSelectedRepositoryItemsValueExpression(),
                    newContentDisabledValueExpression: this.#selectionHolder.getNewContentActionDisabledExpression(),
                    createdContentValueExpression: this.#selectionHolder.getCreatedContentValueExpression(),
                  }),
                ],
              }),
            ],
          }),

          Config(SearchSwitchingContainer, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(CatalogSearchListContainer, {
                    searchResultHitsValueExpression: this.#selectionHolder.getSearchResultHitsValueExpression(),
                    selectedItemsValueExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression(),
                  }),
                ],
              }),
            ],
          }),

          Config(SearchFiltersSwitchingContainer, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(CatalogSearchFilters, { itemId: CatalogSearchFilters.ITEM_ID }),
                ],
              }),
            ],
          }),

          Config(RepositoryToolbarSwitchingContainer, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(CatalogRepositoryToolbarContainer, {
                    selectedFolderValueExpression: this.#selectionHolder.getSelectedFolderValueExpression(),
                    createdContentValueExpression: this.#selectionHolder.getCreatedContentValueExpression(),
                    selectedItemsValueExpression: this.#selectionHolder.getSelectedItemsValueExpression(),
                  }),
                ],
              }),
            ],
          }),

          Config(SearchToolbarSwitchingContainer, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(CatalogSearchToolbarContainer, { selectedSearchItemsValueExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression() }),
                ],
              }),
            ],
          }),

        ],

      }), config);
    })());
  }
}

export default ECommerceLibraryPlugin;
