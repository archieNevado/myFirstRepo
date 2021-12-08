import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import CollectionRepositoryContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionRepositoryContextMenu";
import CollectionSearchContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionSearchContextMenu";
import FolderContentSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/FolderContentSwitchingContainer";
import ICollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/ICollectionView";
import RepositoryToolbar from "@coremedia/studio-client.main.editor-components/sdk/collectionview/RepositoryToolbar";
import SearchFiltersSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SearchFiltersSwitchingContainer";
import SearchSwitchingContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SearchSwitchingContainer";
import SearchToolbar from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SearchToolbar";
import Component from "@jangaroo/ext-ts/Component";
import Item from "@jangaroo/ext-ts/menu/Item";
import ext_menu_Separator from "@jangaroo/ext-ts/menu/Separator";
import ext_toolbar_Separator from "@jangaroo/ext-ts/toolbar/Separator";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CreatePictureFromPictureAssetAction from "./actions/CreatePictureFromPictureAssetAction";
import CreateVideoFromVideoAssetAction from "./actions/CreateVideoFromVideoAssetAction";
import AssetRepositoryListContainer from "./repository/AssetRepositoryListContainer";
import AssetSearchListContainer from "./repository/AssetSearchListContainer";
import AssetSearchFilters from "./search/AssetSearchFilters";

interface AMCollectionViewPluginConfig extends Config<NestedRulesPlugin> {
}

class AMCollectionViewPlugin extends NestedRulesPlugin {
  declare Config: AMCollectionViewPluginConfig;

  #selectionHolder: ICollectionView = null;

  constructor(config: Config<AMCollectionViewPlugin> = null) {
    super((()=>{
      this.#selectionHolder = as(config.cmp, ICollectionView);
      return ConfigUtils.apply(Config(AMCollectionViewPlugin, {

        rules: [
          Config(FolderContentSwitchingContainer, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(AssetRepositoryListContainer, {
                    selectionHolder: this.#selectionHolder,
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
                  Config(AssetSearchListContainer, {
                    searchResultHitsValueExpression: this.#selectionHolder.getSearchResultHitsValueExpression(),
                    selectedItemsValueExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression(),
                  }),
                ],
              }),
            ],
          }),

          Config(CollectionRepositoryContextMenu, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(ext_menu_Separator),
                  Config(Item, {
                    itemId: "createPictureFromAsset",
                    baseAction: new CreatePictureFromPictureAssetAction({
                      contentValueExpression: this.#selectionHolder.getSelectedRepositoryItemsValueExpression(),
                      ...{ hideForContentProxy: true },
                    }),
                  }),
                  Config(Item, {
                    itemId: "createVideoFromAsset",
                    baseAction: new CreateVideoFromVideoAssetAction({
                      contentValueExpression: this.#selectionHolder.getSelectedRepositoryItemsValueExpression(),
                      ...{ hideForContentProxy: true },
                    }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: "openInTab" }),
                ],
              }),
            ],
          }),

          Config(CollectionSearchContextMenu, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(ext_menu_Separator),
                  Config(Item, {
                    itemId: "createPictureFromAsset",
                    baseAction: new CreatePictureFromPictureAssetAction({
                      contentValueExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression(),
                      ...{ hideForContentProxy: true },
                    }),
                  }),
                  Config(Item, {
                    itemId: "createVideoFromAsset",
                    baseAction: new CreateVideoFromVideoAssetAction({
                      contentValueExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression(),
                      ...{ hideForContentProxy: true },
                    }),
                  }),
                  Config(ext_menu_Separator),
                ],
                after: [
                  Config(Component, { itemId: "showInFolder" }),
                ],
              }),
            ],
          }),

          Config(RepositoryToolbar, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(IconButton, {
                    itemId: "createPictureFromAsset",
                    baseAction: new CreatePictureFromPictureAssetAction({
                      contentValueExpression: this.#selectionHolder.getSelectedItemsValueExpression(),
                      ...{ hideForContentProxy: true },
                    }),
                  }),
                  Config(IconButton, {
                    itemId: "createVideoFromAsset",
                    baseAction: new CreateVideoFromVideoAssetAction({
                      contentValueExpression: this.#selectionHolder.getSelectedItemsValueExpression(),
                      ...{ hideForContentProxy: true },
                    }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: "createImageMap" }),
                ],
              }),
            ],
          }),

          Config(SearchToolbar, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(ext_toolbar_Separator),
                  Config(IconButton, {
                    itemId: "createPictureFromAsset",
                    baseAction: new CreatePictureFromPictureAssetAction({ contentValueExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression() }),
                  }),
                  Config(IconButton, {
                    itemId: "createVideoFromAsset",
                    baseAction: new CreateVideoFromVideoAssetAction({ contentValueExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression() }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: "createImageMap" }),
                ],
              }),
            ],
          }),

          Config(SearchFiltersSwitchingContainer, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(AssetSearchFilters, { itemId: AssetSearchFilters.ITEM_ID }),
                ],
              }),
            ],
          }),
        ],

      }), config);
    })());
  }
}

export default AMCollectionViewPlugin;
