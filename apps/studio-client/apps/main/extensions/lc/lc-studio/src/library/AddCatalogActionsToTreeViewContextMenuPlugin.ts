import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import AbstractContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/AbstractContextMenu";
import TreeViewContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/TreeViewContextMenu";
import Component from "@jangaroo/ext-ts/Component";
import Item from "@jangaroo/ext-ts/menu/Item";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin from "../LivecontextStudioPlugin";
import AugmentCategoryAction from "../action/AugmentCategoryAction";
import CreateMarketingSpotAction from "../action/CreateMarketingSpotAction";
import CreateProductTeaserAction from "../action/CreateProductTeaserAction";
import SearchProductVariantsAction from "../action/SearchProductVariantsAction";

interface AddCatalogActionsToTreeViewContextMenuPluginConfig extends Config<AddItemsPlugin> {
}

class AddCatalogActionsToTreeViewContextMenuPlugin extends AddItemsPlugin {
  declare Config: AddCatalogActionsToTreeViewContextMenuPluginConfig;

  constructor(config: Config<AddCatalogActionsToTreeViewContextMenuPlugin> = null) {
    const componentConfig = cast(TreeViewContextMenu, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddCatalogActionsToTreeViewContextMenuPlugin, {
      items: [
        Config(Item, {
          itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID,
          baseAction: new SearchProductVariantsAction({ catalogObjectExpression: componentConfig.selectedFolderValueExpression }),
        }),
        Config(Separator),
        Config(Item, {
          itemId: LivecontextStudioPlugin.AUGMENT_CATEGORY_MENU_ITEM_ID,
          baseAction: new AugmentCategoryAction({ catalogObjectExpression: componentConfig.selectedFolderValueExpression }),
        }),
        Config(Item, {
          itemId: LivecontextStudioPlugin.CREATE_PRODUCT_TEASER_MENU_ITEM_ID,
          baseAction: new CreateProductTeaserAction({ catalogObjectExpression: componentConfig.selectedFolderValueExpression }),
        }),
        Config(Item, {
          itemId: LivecontextStudioPlugin.CREATE_MARKETING_SPOT_MENU_ITEM_ID,
          baseAction: new CreateMarketingSpotAction({ catalogObjectExpression: componentConfig.selectedFolderValueExpression }),
        }),
      ],
      after: [
        Config(Component, { itemId: AbstractContextMenu.OPEN_IN_TAB_MENU_ITEM_ID }),
      ],
    }), config));
  }
}

export default AddCatalogActionsToTreeViewContextMenuPlugin;
