import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";

import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import AbstractContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/AbstractContextMenu";
import OpenInTreeAction from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/OpenInTreeAction";
import Component from "@jangaroo/ext-ts/Component";
import Item from "@jangaroo/ext-ts/menu/Item";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommonCatalogContextMenu from "../CommonCatalogContextMenu";

interface CatalogRepositoryContextMenuConfig extends Config<CommonCatalogContextMenu>, Partial<Pick<CatalogRepositoryContextMenu,
  "selectedFolderValueExpression"
  >> {
}

/**
 * The context menu for the list or thumbnail view in the catalog repository view.
 */
class CatalogRepositoryContextMenu extends CommonCatalogContextMenu {
  declare Config: CatalogRepositoryContextMenuConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryContextMenu";

  selectedFolderValueExpression: ValueExpression = null;

  constructor(config: Config<CatalogRepositoryContextMenu> = null) {
    super(ConfigUtils.apply(Config(CatalogRepositoryContextMenu, {
      ...ConfigUtils.append({
        plugins: [
          Config(AddItemsPlugin, {
            items: [
              Config(Item, {
                itemId: AbstractContextMenu.OPEN_MENU_ITEM_ID,
                baseAction: new OpenInTreeAction({
                  selectedItemsValueExpression: config.selectedItemsValueExpression,
                  selectedFolderValueExpression: config.selectedFolderValueExpression,
                }),
              }),
            ],
            before: [
              Config(Component, { itemId: AbstractContextMenu.OPEN_IN_TAB_MENU_ITEM_ID }),
            ],
          }),
        ],
      }),
    }), config));
  }
}

export default CatalogRepositoryContextMenu;
