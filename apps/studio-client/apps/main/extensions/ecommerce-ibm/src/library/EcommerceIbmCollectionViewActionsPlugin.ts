import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import CatalogRepositoryContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryContextMenu";
import CatalogRepositoryToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryToolbar";
import CatalogSearchContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchContextMenu";
import CatalogSearchToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchToolbar";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import AbstractContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/AbstractContextMenu";
import ICollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/ICollectionView";
import TreeViewContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/TreeViewContextMenu";
import Component from "@jangaroo/ext-ts/Component";
import Item from "@jangaroo/ext-ts/menu/Item";
import ext_menu_Separator from "@jangaroo/ext-ts/menu/Separator";
import ext_toolbar_Separator from "@jangaroo/ext-ts/toolbar/Separator";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EcommerceIbmStudioPlugin from "../EcommerceIbmStudioPlugin";
import OpenInManagementCenterAction from "../action/OpenInManagementCenterAction";

interface EcommerceIbmCollectionViewActionsPluginConfig extends Config<NestedRulesPlugin> {
}

/* Extend the standard Studio library for Live Context

 @deprecated This plugin is part of the legacy Blueprint commerce integration and has been deprecated
 in favour of the Commerce Hub integration.
 */
/**
 * @deprecated
 */
class EcommerceIbmCollectionViewActionsPlugin extends NestedRulesPlugin {
  declare Config: EcommerceIbmCollectionViewActionsPluginConfig;

  #selectionHolder: ICollectionView = null;

  constructor(config: Config<EcommerceIbmCollectionViewActionsPlugin> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#selectionHolder = as(config.cmp, ICollectionView);
    super(ConfigUtils.apply(Config(EcommerceIbmCollectionViewActionsPlugin, {

      rules: [
        Config(CatalogRepositoryToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(IconButton, {
                  itemId: EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_BUTTON_ITEM_ID,
                  baseAction: new OpenInManagementCenterAction({ catalogObjectExpression: this$.#selectionHolder.getSelectedItemsValueExpression() }),
                }),
              ],
              after: [
                Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_BUTTON_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CatalogRepositoryContextMenu, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(ext_menu_Separator),
                Config(Item, {
                  itemId: EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID,
                  baseAction: new OpenInManagementCenterAction({ catalogObjectExpression: this$.#selectionHolder.getSelectedItemsValueExpression() }),
                }),
              ],
              after: [
                Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CatalogSearchToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(ext_toolbar_Separator),
                Config(IconButton, {
                  itemId: EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_BUTTON_ITEM_ID,
                  baseAction: new OpenInManagementCenterAction({ catalogObjectExpression: this$.#selectionHolder.getSelectedSearchItemsValueExpression() }),
                }),
              ],
              after: [
                Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CatalogSearchContextMenu, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Item, {
                  itemId: EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID,
                  baseAction: new OpenInManagementCenterAction({ catalogObjectExpression: this$.#selectionHolder.getSelectedSearchItemsValueExpression() }),
                }),
                Config(ext_menu_Separator),
              ],
              after: [
                Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(TreeViewContextMenu, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Item, {
                  itemId: EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID,
                  baseAction: new OpenInManagementCenterAction({ catalogObjectExpression: this$.#selectionHolder.getSelectedFolderValueExpression() }),
                }),
                Config(ext_menu_Separator),
              ],
              after: [
                Config(Component, { itemId: AbstractContextMenu.OPEN_IN_TAB_MENU_ITEM_ID }),
              ],
            }),
          ],
        }),

      ],

    }), config));
  }
}

export default EcommerceIbmCollectionViewActionsPlugin;
