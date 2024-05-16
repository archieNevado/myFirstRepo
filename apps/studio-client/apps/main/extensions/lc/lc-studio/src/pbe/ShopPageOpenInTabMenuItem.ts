import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import OpenInTabAction from "@coremedia/studio-client.ext.form-services-toolkit/actions/OpenInTabAction";
import Item from "@jangaroo/ext-ts/menu/Item";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import MetadataToEntitiesActionAdapter from "../action/MetadataToEntitiesActionAdapter";

interface ShopPageOpenInTabMenuItemConfig extends Config<Item>, Partial<Pick<ShopPageOpenInTabMenuItem,
  "metadataValueExpression"
>> {
}

class ShopPageOpenInTabMenuItem extends Item {
  declare Config: ShopPageOpenInTabMenuItemConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.shopPageOpenInTabMenuItem";

  /**
   * The itemId of the 'open in tab' menu item.
   */
  static readonly LC_OPEN_IN_TAB_MENU_ITEM_ID: string = "lcOpenInTab";

  metadataValueExpression: ValueExpression = null;

  constructor(config: Config<ShopPageOpenInTabMenuItem> = null) {
    super(ConfigUtils.apply(Config(ShopPageOpenInTabMenuItem, {

      baseAction: new MetadataToEntitiesActionAdapter({
        metadataValueExpression: config.metadataValueExpression,
        itemId: ShopPageOpenInTabMenuItem.LC_OPEN_IN_TAB_MENU_ITEM_ID,
        hideOnDisable: true,
        useParentNode: true,
        backingAction: new OpenInTabAction({ contentValueExpression: ValueExpressionFactory.createFromValue() }),
      }),

    }), config));
  }
}

export default ShopPageOpenInTabMenuItem;
