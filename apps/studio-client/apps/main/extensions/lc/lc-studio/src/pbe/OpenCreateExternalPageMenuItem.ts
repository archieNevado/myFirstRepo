import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ProcessingData from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/processing/ProcessingData";
import Item from "@jangaroo/ext-ts/menu/Item";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import MetadataToEntitiesActionAdapter from "../action/MetadataToEntitiesActionAdapter";
import OpenCreateExternalPageDialogAction from "../action/OpenCreateExternalPageDialogAction";

interface OpenCreateExternalPageMenuItemConfig extends Config<Item>, Partial<Pick<OpenCreateExternalPageMenuItem,
  "metadataValueExpression"
>> {
}

class OpenCreateExternalPageMenuItem extends Item {
  declare Config: OpenCreateExternalPageMenuItemConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.openCreateExternalPageMenuItem";

  /**
   * The itemId of the 'augment shop page' menu item.
   */
  static readonly AUGMENT_SHOP_PAGE_MENU_ITEM_ID: string = "augmentShopPage";

  metadataValueExpression: ValueExpression = null;

  constructor(config: Config<OpenCreateExternalPageMenuItem> = null) {
    super(ConfigUtils.apply(Config(OpenCreateExternalPageMenuItem, {

      baseAction: new MetadataToEntitiesActionAdapter({
        metadataValueExpression: config.metadataValueExpression,
        itemId: OpenCreateExternalPageMenuItem.AUGMENT_SHOP_PAGE_MENU_ITEM_ID,
        hideOnDisable: true,
        useParentNode: true,
        setEntities: "setData",
        text: LivecontextStudioPlugin_properties.Action_augmentShopPage_text,
        backingAction: new OpenCreateExternalPageDialogAction({
          contentType: "CMExternalPage",
          inheritEditors: false,
          defaultProperties: ProcessingData.NAME_PROPERTY + ","
                            + ProcessingData.FOLDER_PROPERTY,
        }),
      }),

    }), config));
  }
}

export default OpenCreateExternalPageMenuItem;
