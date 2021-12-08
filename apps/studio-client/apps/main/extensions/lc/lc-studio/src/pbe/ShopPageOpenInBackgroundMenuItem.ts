import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import OpenInTabAction from "@coremedia/studio-client.ext.form-services-toolkit/actions/OpenInTabAction";
import Item from "@jangaroo/ext-ts/menu/Item";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import MetadataToEntitiesActionAdapter from "../action/MetadataToEntitiesActionAdapter";

interface ShopPageOpenInBackgroundMenuItemConfig extends Config<Item>, Partial<Pick<ShopPageOpenInBackgroundMenuItem,
  "metadataValueExpression"
>> {
}

class ShopPageOpenInBackgroundMenuItem extends Item {
  declare Config: ShopPageOpenInBackgroundMenuItemConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.shopPageOpenInBackgroundMenuItem";

  /**
   * The itemId of the 'open in background tab' menu item.
   */
  static readonly LC_OPEN_IN_BACKGROUND_TAB_MENU_ITEM_ID: string = "lcOpenInBackgroundTab";

  metadataValueExpression: ValueExpression = null;

  constructor(config: Config<ShopPageOpenInBackgroundMenuItem> = null) {
    super(ConfigUtils.apply(Config(ShopPageOpenInBackgroundMenuItem, {

      baseAction: new MetadataToEntitiesActionAdapter({
        metadataValueExpression: config.metadataValueExpression,
        itemId: ShopPageOpenInBackgroundMenuItem.LC_OPEN_IN_BACKGROUND_TAB_MENU_ITEM_ID,
        hideOnDisable: true,
        useParentNode: true,
        backingAction:
        /*the content will be set via the MetadataToEntitiesActionAdapter. Just configure a empty value expression*/
        new OpenInTabAction({
          contentValueExpression: ValueExpressionFactory.createFromValue(),
          background: true,
        }),
      }),

    }), config));
  }
}

export default ShopPageOpenInBackgroundMenuItem;
