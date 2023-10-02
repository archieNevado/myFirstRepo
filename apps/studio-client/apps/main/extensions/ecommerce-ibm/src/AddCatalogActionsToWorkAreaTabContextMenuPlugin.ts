import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import WorkAreaTabProxiesContextMenu from "@coremedia/studio-client.main.editor-components/sdk/desktop/reusability/WorkAreaTabProxiesContextMenu";
import Item from "@jangaroo/ext-ts/menu/Item";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EcommerceIbmStudioPlugin from "./EcommerceIbmStudioPlugin";
import OpenInManagementCenterAction from "./action/OpenInManagementCenterAction";

interface AddCatalogActionsToWorkAreaTabContextMenuPluginConfig extends Config<AddItemsPlugin> {
}

/**
 * @deprecated This plugin is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 * @deprecated
 */
class AddCatalogActionsToWorkAreaTabContextMenuPlugin extends AddItemsPlugin {
  declare Config: AddCatalogActionsToWorkAreaTabContextMenuPluginConfig;

  #componentConfig: WorkAreaTabProxiesContextMenu = null;

  constructor(config: Config<AddCatalogActionsToWorkAreaTabContextMenuPlugin> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#componentConfig = cast(WorkAreaTabProxiesContextMenu, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddCatalogActionsToWorkAreaTabContextMenuPlugin, {
      items: [
        Config(Separator),
        Config(Item, {
          itemId: EcommerceIbmStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID,
          baseAction: new OpenInManagementCenterAction({ catalogObjectExpression: this$.#componentConfig.tabEntityExpression }),
        }),
      ],
    }), config));
  }
}

export default AddCatalogActionsToWorkAreaTabContextMenuPlugin;
