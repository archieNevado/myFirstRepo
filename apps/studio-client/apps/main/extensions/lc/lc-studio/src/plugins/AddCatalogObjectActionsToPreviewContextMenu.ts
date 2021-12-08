import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import PreviewContextMenu from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewContextMenu";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenCreateExternalPageMenuItem from "../pbe/OpenCreateExternalPageMenuItem";
import ShopPageOpenInBackgroundMenuItem from "../pbe/ShopPageOpenInBackgroundMenuItem";
import ShopPageOpenInTabMenuItem from "../pbe/ShopPageOpenInTabMenuItem";
import ShopPageShowInLibraryMenuItem from "../pbe/ShopPageShowInLibraryMenuItem";

interface AddCatalogObjectActionsToPreviewContextMenuConfig extends Config<AddItemsPlugin> {
}

class AddCatalogObjectActionsToPreviewContextMenu extends AddItemsPlugin {
  declare Config: AddCatalogObjectActionsToPreviewContextMenuConfig;

  constructor(config: Config<AddCatalogObjectActionsToPreviewContextMenu> = null) {
    const componentConfig = cast(PreviewContextMenu, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddCatalogObjectActionsToPreviewContextMenu, {

      items: [
        Config(OpenCreateExternalPageMenuItem, { metadataValueExpression: componentConfig.selectedNodeValueExpression }),
        Config(ShopPageOpenInTabMenuItem, { metadataValueExpression: componentConfig.selectedNodeValueExpression }),
        Config(ShopPageOpenInBackgroundMenuItem, { metadataValueExpression: componentConfig.selectedNodeValueExpression }),
        Config(ShopPageShowInLibraryMenuItem, { metadataValueExpression: componentConfig.selectedNodeValueExpression }),
      ],
    }), config));
  }
}

export default AddCatalogObjectActionsToPreviewContextMenu;
