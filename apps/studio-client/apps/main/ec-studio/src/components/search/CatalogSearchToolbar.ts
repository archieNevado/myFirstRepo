import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import HideObsoleteSeparatorsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HideObsoleteSeparatorsPlugin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import OpenEntitiesInTabsAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import OpenSaveSearchWindowAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenSaveSearchWindowAction";
import CopyToClipboardAction from "@coremedia/studio-client.main.editor-components/sdk/clipboard/CopyToClipboardAction";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin from "../../ECommerceStudioPlugin";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";

interface CatalogSearchToolbarConfig extends Config<Toolbar>, Partial<Pick<CatalogSearchToolbar,
  "selectedSearchItemsValueExpression"
>> {
}

class CatalogSearchToolbar extends Toolbar {
  declare Config: CatalogSearchToolbarConfig;

  selectedSearchItemsValueExpression: ValueExpression = null;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchToolbar";

  /**
   * The itemId of the first header spacer.
   */
  static readonly SEARCH_TOOLBAR_SPACER_FIRST_ITEM_ID: string = "searchToolbarSpacerFirst";

  /**
   * The itemId of the second header spacer.
   */
  static readonly OPEN_IN_TAB_TOOLBAR_SPACER_SECOND_ITEM_ID: string = "searchToolbarSpacerSecond";

  constructor(config: Config<CatalogSearchToolbar> = null) {
    super(ConfigUtils.apply(Config(CatalogSearchToolbar, {
      enableOverflow: true,
      itemId: "commerceToolbar",
      ui: ToolbarSkin.LIGHT.getSkin(),
      ariaLabel: ECommerceStudioPlugin_properties.CollectionView_catalogSearchToolbar_label,
      flex: 1,

      plugins: [
        Config(HideObsoleteSeparatorsPlugin),
      ],
      items: [

        Config(IconButton, {
          itemId: "saveSearch",
          baseAction: new OpenSaveSearchWindowAction({}),
        }),

        Config(Separator, { itemId: CatalogSearchToolbar.SEARCH_TOOLBAR_SPACER_FIRST_ITEM_ID }),

        Config(IconButton, {
          itemId: "copyToClipboard",
          baseAction: new CopyToClipboardAction({ contentValueExpression: config.selectedSearchItemsValueExpression }),
        }),

        Config(Separator, { itemId: CatalogSearchToolbar.OPEN_IN_TAB_TOOLBAR_SPACER_SECOND_ITEM_ID }),

        Config(IconButton, {
          itemId: ECommerceStudioPlugin.OPEN_IN_TAB_BUTTON_ITEM_ID,
          baseAction: new OpenEntitiesInTabsAction({ entitiesValueExpression: config.selectedSearchItemsValueExpression }),
        }),
      ],
      layout: Config(HBoxLayout, <Config<HBoxLayout>>{ triggerWidth: 26 }),

    }), config));
  }
}

export default CatalogSearchToolbar;
