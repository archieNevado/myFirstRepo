import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import AbstractContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/AbstractContextMenu";
import CollectionRepositoryContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionRepositoryContextMenu";
import FolderContentContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/FolderContentContainer";
import FolderContentSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/FolderContentSwitchingContainer";
import ICollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/ICollectionView";
import RepositoryToolbar from "@coremedia/studio-client.main.editor-components/sdk/collectionview/RepositoryToolbar";
import SearchFiltersSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SearchFiltersSwitchingContainer";
import SearchSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SearchSwitchingContainer";
import Component from "@jangaroo/ext-ts/Component";
import Item from "@jangaroo/ext-ts/menu/Item";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import UnlinkAction from "../actions/UnlinkAction";
import CatalogSearchFilters from "../collectionview/search/CatalogSearchFilters";
import RepositoryCatalogSearchListContainer from "../repository/RepositoryCatalogSearchListContainer";
import CatalogCollectionViewExtension from "./CatalogCollectionViewExtension";

interface CatalogLibraryPluginConfig extends Config<NestedRulesPlugin> {
}

class CatalogLibraryPlugin extends NestedRulesPlugin {
  declare Config: CatalogLibraryPluginConfig;

  /**
   * The itemId of the delete menu item.
   */
  static readonly CREATE_CATEGORY_MENU_ITEM_ID: string = "createCategory";

  /**
   * The itemId of the delete menu item.
   */
  static readonly CREATE_PRODUCT_MENU_ITEM_ID: string = "createProduct";

  #selectionHolder: ICollectionView = null;

  constructor(config: Config<CatalogLibraryPlugin> = null) {
    super((()=>{
      this.#selectionHolder = as(config.cmp, ICollectionView);
      return ConfigUtils.apply(Config(CatalogLibraryPlugin, {

        rules: [

          Config(FolderContentSwitchingContainer, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(FolderContentContainer, {
                    instanceName: "catalog",
                    itemId: CatalogCollectionViewExtension.CATALOG_FOLDER_CONTAINER_ITEM_ID,
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
                  Config(RepositoryCatalogSearchListContainer, {
                    searchResultHitsValueExpression: this.#selectionHolder.getSearchResultHitsValueExpression(),
                    selectedItemsValueExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression(),
                  }),
                ],
              }),
            ],
          }),

          Config(RepositoryToolbar, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(IconButton, {
                    itemId: "unlink",
                    baseAction: new UnlinkAction({
                      folderValueExpression: this.#selectionHolder.getSelectedFolderValueExpression(),
                      contentValueExpression: this.#selectionHolder.getSelectedItemsValueExpression(),
                    }),
                  }),
                ],
                before: [
                  Config(Component, { itemId: AbstractContextMenu.DELETE_MENU_ITEM_ID }),
                ],
              }),
            ],
          }),

          Config(CollectionRepositoryContextMenu, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(Separator),
                  Config(Item, {
                    itemId: "unlink",
                    baseAction: new UnlinkAction({
                      folderValueExpression: this.#selectionHolder.getSelectedFolderValueExpression(),
                      contentValueExpression: this.#selectionHolder.getSelectedRepositoryItemsValueExpression(),
                    }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: AbstractContextMenu.DELETE_MENU_ITEM_ID }),
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

        ],

      }), config);
    })());
  }
}

export default CatalogLibraryPlugin;
