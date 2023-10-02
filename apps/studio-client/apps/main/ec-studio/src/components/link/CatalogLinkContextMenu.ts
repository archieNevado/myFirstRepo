import HideObsoleteSeparatorsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HideObsoleteSeparatorsPlugin";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import LinkListRemoveAction from "@coremedia/studio-client.main.editor-components/sdk/actions/LinkListRemoveAction";
import OpenEntitiesInTabsAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import ActionRef from "@jangaroo/ext-ts/ActionRef";
import Item from "@jangaroo/ext-ts/menu/Item";
import Menu from "@jangaroo/ext-ts/menu/Menu";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin from "../../ECommerceStudioPlugin";

interface CatalogLinkContextMenuConfig extends Config<Menu>, Partial<Pick<CatalogLinkContextMenu,
  "linkListWrapper" |
  "hideOpenInTab" |
  "hideRemove"
>> {
}

/**
 * The context menu for the catalog link list
 */
class CatalogLinkContextMenu extends Menu {
  declare Config: CatalogLinkContextMenuConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogLinkContextMenu";

  constructor(config: Config<CatalogLinkContextMenu> = null) {
    super(ConfigUtils.apply(Config(CatalogLinkContextMenu, {
      width: 210,
      plain: true,

      plugins: [
        Config(HideObsoleteSeparatorsPlugin),
      ],

      items: [
        Config(Item, {
          itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID,
          hidden: config.hideOpenInTab,
          baseAction: Config(ActionRef, { actionId: OpenEntitiesInTabsAction.ACTION_ID }),
        }),

        Config(Separator),

        Config(Item, {
          itemId: ECommerceStudioPlugin.REMOVE_LINK_MENU_ITEM_ID,
          hidden: config.hideRemove,
          baseAction: Config(ActionRef, { actionId: LinkListRemoveAction.ACTION_ID }),
        }),
      ],
    }), config));
  }

  linkListWrapper: ILinkListWrapper = null;

  /**
   * Set to true if the open in tab menu item should be hidden. Default is false
   */
  hideOpenInTab: boolean = false;

  /**
   * Set to true if the remove menu item should be hidden. Default is false
   */
  hideRemove: boolean = false;
}

export default CatalogLinkContextMenu;
