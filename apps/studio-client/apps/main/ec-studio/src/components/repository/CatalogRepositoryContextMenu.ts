import RemoveItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/RemoveItemsPlugin";
import AbstractContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/AbstractContextMenu";
import CollectionRepositoryContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionRepositoryContextMenu";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface CatalogRepositoryContextMenuConfig extends Config<CollectionRepositoryContextMenu> {
}

/**
 * The context menu for the list or thumbnail view in the catalog repository view.
 */
class CatalogRepositoryContextMenu extends CollectionRepositoryContextMenu {
  declare Config: CatalogRepositoryContextMenuConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryContextMenu";

  constructor(config: Config<CatalogRepositoryContextMenu> = null) {
    super(ConfigUtils.apply(Config(CatalogRepositoryContextMenu, {

      ...ConfigUtils.append({
        plugins: [
          Config(RemoveItemsPlugin, {
            items: [
              Config(Component, { itemId: AbstractContextMenu.NEW_FOLDER_ITEM_ID }),
              Config(Component, { itemId: AbstractContextMenu.NEW_CONTENT_ITEM_ID }),
              Config(Component, { itemId: AbstractContextMenu.CUT_TO_CLIPBOARD_ITEM_ID }),
              Config(Component, { itemId: AbstractContextMenu.PASTE_FROM_CLIPBOARD_ITEM_ID }),
              Config(Component, { itemId: AbstractContextMenu.RENAME_MENU_ITEM_ID }),
              Config(Component, { itemId: "createProduct" }),
              Config(Component, { itemId: "createCategory" }),
            ],
          }),
        ],
      }),
    }), config));
  }
}

export default CatalogRepositoryContextMenu;
