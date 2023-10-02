import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import CatalogLinkContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkContextMenu";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import CatalogLinkToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkToolbar";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import Component from "@jangaroo/ext-ts/Component";
import Item from "@jangaroo/ext-ts/menu/Item";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EcommerceIbmStudioPlugin from "./EcommerceIbmStudioPlugin";
import OpenInManagementCenterAction from "./action/OpenInManagementCenterAction";

interface AddCatalogLinkActionsPluginConfig extends Config<NestedRulesPlugin> {
}

/**
 * @deprecated This plugin is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 * @deprecated
 */
class AddCatalogLinkActionsPlugin extends NestedRulesPlugin {
  declare Config: AddCatalogLinkActionsPluginConfig;

  #catalogLinkPropertyField: CatalogLinkPropertyField = null;

  constructor(config: Config<AddCatalogLinkActionsPlugin> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#catalogLinkPropertyField = cast(CatalogLinkPropertyField, config.cmp);
    super(ConfigUtils.apply(Config(AddCatalogLinkActionsPlugin, {

      rules: [
        Config(CatalogLinkToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(IconButton, {
                  itemId: EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_BUTTON_ITEM_ID,
                  baseAction: new OpenInManagementCenterAction({ catalogObjectExpression: this$.#catalogLinkPropertyField.selectedValuesExpression }),
                }),
              ],
              after: [
                Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_BUTTON_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CatalogLinkContextMenu, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Item, {
                  itemId: EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID,
                  baseAction: new OpenInManagementCenterAction({ catalogObjectExpression: this$.#catalogLinkPropertyField.selectedValuesExpression }),
                }),
              ],
              after: [
                Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID }),
              ],
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default AddCatalogLinkActionsPlugin;
