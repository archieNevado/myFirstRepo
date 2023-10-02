import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import HideObsoleteSeparatorsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HideObsoleteSeparatorsPlugin";
import OpenEntitiesInTabsAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import Item from "@jangaroo/ext-ts/menu/Item";
import Menu from "@jangaroo/ext-ts/menu/Menu";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin from "../../ECommerceStudioPlugin";

interface CatalogSearchContextMenuConfig extends Config<Menu>, Partial<Pick<CatalogSearchContextMenu,
  "selectedSearchItemsValueExpression"
>> {
}

/**
 * The context menu for the list or thumbnail view in the catalog search view.
 */
class CatalogSearchContextMenu extends Menu {
  declare Config: CatalogSearchContextMenuConfig;

  selectedSearchItemsValueExpression: ValueExpression = null;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchContextMenu";

  constructor(config: Config<CatalogSearchContextMenu> = null) {
    super(ConfigUtils.apply(Config(CatalogSearchContextMenu, {
      width: 210,
      plain: true,

      plugins: [
        Config(HideObsoleteSeparatorsPlugin),
      ],
      items: [
        Config(Item, {
          itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID,
          baseAction: new OpenEntitiesInTabsAction({ entitiesValueExpression: config.selectedSearchItemsValueExpression }),
        }),
      ],

    }), config));
  }
}

export default CatalogSearchContextMenu;
