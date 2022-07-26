import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ApproveAction from "@coremedia/studio-client.ext.cap-base-components/actions/ApproveAction";
import PublishAction from "@coremedia/studio-client.ext.cap-base-components/actions/PublishAction";
import WithdrawAction from "@coremedia/studio-client.ext.cap-base-components/actions/WithdrawAction";
import DeleteAction from "@coremedia/studio-client.main.editor-components/sdk/actions/DeleteAction";
import OpenEntitiesInTabsAction
  from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import BookmarkMenuItem from "@coremedia/studio-client.main.editor-components/sdk/bookmarks/BookmarkMenuItem";
import CopyToClipboardAction from "@coremedia/studio-client.main.editor-components/sdk/clipboard/CopyToClipboardAction";
import AbstractContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/AbstractContextMenu";
import Item from "@jangaroo/ext-ts/menu/Item";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface CommonCatalogContextMenuConfig extends Config<AbstractContextMenu>, Partial<Pick<CommonCatalogContextMenu,
  "selectedItemsValueExpression"
  >> {
}

/**
 * The context menu for the list or thumbnail view in the catalog repository view.
 */
class CommonCatalogContextMenu extends AbstractContextMenu {
  declare Config: CommonCatalogContextMenuConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.commonCatalogContextMenu";

  selectedItemsValueExpression: ValueExpression = null;

  constructor(config: Config<CommonCatalogContextMenu> = null) {
    super(ConfigUtils.apply(Config(CommonCatalogContextMenu, {
      plain: true,
      items: [
        Config(Item, {
          itemId: AbstractContextMenu.OPEN_IN_TAB_MENU_ITEM_ID,
          baseAction: new OpenEntitiesInTabsAction({ entitiesValueExpression: config.selectedItemsValueExpression }),
        }),
        Config(Separator),
        Config(BookmarkMenuItem, {
          itemId: AbstractContextMenu.BOOKMARK_ITEM_ID,
          contentValueExpression: config.selectedItemsValueExpression,
        }),
        Config(Separator),
        Config(Item, {
          itemId: AbstractContextMenu.COPY_TO_CLIPBOARD_ITEM_ID,
          baseAction: new CopyToClipboardAction({ contentValueExpression: config.selectedItemsValueExpression }),
        }),
        Config(Separator),
        Config(Item, {
          itemId: AbstractContextMenu.APPROVE_MENU_ITEM_ID,
          baseAction: new ApproveAction({ contentValueExpression: config.selectedItemsValueExpression }),
        }),
        Config(Item, {
          itemId: AbstractContextMenu.PUBLISH_MENU_ITEM_ID,
          baseAction: new PublishAction({ contentValueExpression: config.selectedItemsValueExpression }),
        }),
        Config(Separator),
        Config(Item, {
          itemId: AbstractContextMenu.WITHDRAW_MENU_ITEM_ID,
          baseAction: new WithdrawAction({ contentValueExpression: config.selectedItemsValueExpression }),
        }),
        Config(Item, {
          itemId: AbstractContextMenu.DELETE_MENU_ITEM_ID,
          baseAction: new DeleteAction({ contentValueExpression: config.selectedItemsValueExpression }),
        }),
      ],
    }), config));
  }
}

export default CommonCatalogContextMenu;
