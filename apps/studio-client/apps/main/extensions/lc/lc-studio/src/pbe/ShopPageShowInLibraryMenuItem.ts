import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ShowInRepositoryAction from "@coremedia/studio-client.ext.library-services-toolkit/actions/ShowInRepositoryAction";
import Item from "@jangaroo/ext-ts/menu/Item";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import MetadataToEntitiesActionAdapter from "../action/MetadataToEntitiesActionAdapter";

interface ShopPageShowInLibraryMenuItemConfig extends Config<Item>, Partial<Pick<ShopPageShowInLibraryMenuItem,
  "metadataValueExpression"
>> {
}

class ShopPageShowInLibraryMenuItem extends Item {
  declare Config: ShopPageShowInLibraryMenuItemConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.shopPageShowInLibraryMenuItem";

  /**
   * The itemId of the 'open in library' menu item.
   */
  static readonly LC_OPEN_IN_LIBRARY_MENU_ITEM_ID: string = "lcOpenInLibrary";

  metadataValueExpression: ValueExpression = null;

  constructor(config: Config<ShopPageShowInLibraryMenuItem> = null) {
    super(ConfigUtils.apply(Config(ShopPageShowInLibraryMenuItem, {

      baseAction: new MetadataToEntitiesActionAdapter({
        metadataValueExpression: config.metadataValueExpression,
        itemId: ShopPageShowInLibraryMenuItem.LC_OPEN_IN_LIBRARY_MENU_ITEM_ID,
        hideOnDisable: true,
        useParentNode: true,
        backingAction: new ShowInRepositoryAction({ contentValueExpression: ValueExpressionFactory.createFromValue() }),
      }),

    }), config));
  }
}

export default ShopPageShowInLibraryMenuItem;
