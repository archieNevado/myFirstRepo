import CatalogRepositoryContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryContextMenu";
import CatalogRepositoryToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryToolbar";
import CatalogSearchContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchContextMenu";
import CatalogSearchToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchToolbar";
import LivecontextStudioPlugin from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import ICollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/ICollectionView";
import TreeViewContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/TreeViewContextMenu";
import Component from "@jangaroo/ext-ts/Component";
import Item from "@jangaroo/ext-ts/menu/Item";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import SearchProductPicturesAction from "../action/SearchProductPicturesAction";

interface LivecontextAssetLibraryPluginConfig extends Config<NestedRulesPlugin> {
}

class LivecontextAssetLibraryPlugin extends NestedRulesPlugin {
  declare Config: LivecontextAssetLibraryPluginConfig;

  /**
   * The itemId of the search product pictures button item.
   */
  static readonly SEARCH_PRODUCT_PICTURES_BUTTON_ITEM_ID: string = "searchProductPictures";

  /**
   * The itemId of the search product pictures menu item.
   */
  static readonly SEARCH_PRODUCT_PICTURES_MENU_ITEM_ID: string = "searchProductPictures";

  #selectionHolder: ICollectionView = null;

  constructor(config: Config<LivecontextAssetLibraryPlugin> = null) {
    super((()=>{
      this.#selectionHolder = as(config.cmp, ICollectionView);
      return ConfigUtils.apply(Config(LivecontextAssetLibraryPlugin, {

        rules: [

          /*Add Search Product Pictures Action */
          Config(CatalogSearchToolbar, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(IconButton, {
                    itemId: LivecontextAssetLibraryPlugin.SEARCH_PRODUCT_PICTURES_BUTTON_ITEM_ID,
                    baseAction: new SearchProductPicturesAction({ catalogObjectExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression() }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_BUTTON_ITEM_ID }),
                ],
              }),
            ],
          }),

          Config(CatalogRepositoryContextMenu, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(Item, {
                    itemId: LivecontextAssetLibraryPlugin.SEARCH_PRODUCT_PICTURES_MENU_ITEM_ID,
                    baseAction: new SearchProductPicturesAction({ catalogObjectExpression: this.#selectionHolder.getSelectedRepositoryItemsValueExpression() }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID }),
                ],
              }),
            ],
          }),

          Config(CatalogSearchContextMenu, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(Item, {
                    itemId: LivecontextAssetLibraryPlugin.SEARCH_PRODUCT_PICTURES_MENU_ITEM_ID,
                    baseAction: new SearchProductPicturesAction({ catalogObjectExpression: this.#selectionHolder.getSelectedSearchItemsValueExpression() }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID }),
                ],
              }),
            ],
          }),

          Config(CatalogRepositoryToolbar, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(IconButton, {
                    itemId: LivecontextAssetLibraryPlugin.SEARCH_PRODUCT_PICTURES_BUTTON_ITEM_ID,
                    baseAction: new SearchProductPicturesAction({ catalogObjectExpression: this.#selectionHolder.getSelectedRepositoryItemsValueExpression() }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_BUTTON_ITEM_ID }),
                ],
              }),
            ],
          }),

          Config(TreeViewContextMenu, {
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(Item, {
                    itemId: LivecontextAssetLibraryPlugin.SEARCH_PRODUCT_PICTURES_MENU_ITEM_ID,
                    baseAction: new SearchProductPicturesAction({ catalogObjectExpression: this.#selectionHolder.getSelectedFolderValueExpression() }),
                  }),
                ],
                after: [
                  Config(Component, { itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID }),
                ],

              }),
            ],
          }),

        ],

      }), config);
    })());
  }
}

export default LivecontextAssetLibraryPlugin;
