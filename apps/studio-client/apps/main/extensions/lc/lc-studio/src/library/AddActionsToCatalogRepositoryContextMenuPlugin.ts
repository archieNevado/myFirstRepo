import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import CatalogRepositoryContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryContextMenu";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import Component from "@jangaroo/ext-ts/Component";
import Item from "@jangaroo/ext-ts/menu/Item";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin from "../LivecontextStudioPlugin";
import AugmentCategoryAction from "../action/AugmentCategoryAction";
import AugmentProductAction from "../action/AugmentProductAction";
import CreateMarketingSpotAction from "../action/CreateMarketingSpotAction";
import CreateProductTeaserAction from "../action/CreateProductTeaserAction";
import SearchProductVariantsAction from "../action/SearchProductVariantsAction";

interface AddActionsToCatalogRepositoryContextMenuPluginConfig extends Config<AddItemsPlugin> {
}

class AddActionsToCatalogRepositoryContextMenuPlugin extends AddItemsPlugin {
  declare Config: AddActionsToCatalogRepositoryContextMenuPluginConfig;

  constructor(config: Config<AddActionsToCatalogRepositoryContextMenuPlugin> = null) {
    const componentConfig = cast(CatalogRepositoryContextMenu, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddActionsToCatalogRepositoryContextMenuPlugin, {
      items: [
        Config(Item, {
          itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID,
          baseAction: new SearchProductVariantsAction({ catalogObjectExpression: componentConfig.selectedItemsValueExpression }),
        }),
        Config(Separator),
        Config(Item, {
          itemId: LivecontextStudioPlugin.AUGMENT_CATEGORY_MENU_ITEM_ID,
          baseAction: new AugmentCategoryAction({ catalogObjectExpression: componentConfig.selectedItemsValueExpression }),
        }),
        Config(Item, {
          itemId: LivecontextStudioPlugin.AUGMENT_PRODUCT_MENU_ITEM_ID,
          baseAction: new AugmentProductAction({ catalogObjectExpression: componentConfig.selectedItemsValueExpression }),
        }),
        Config(Item, {
          itemId: LivecontextStudioPlugin.CREATE_PRODUCT_TEASER_MENU_ITEM_ID,
          baseAction: new CreateProductTeaserAction({ catalogObjectExpression: componentConfig.selectedItemsValueExpression }),
        }),
        Config(Item, {
          itemId: LivecontextStudioPlugin.CREATE_MARKETING_SPOT_MENU_ITEM_ID,
          baseAction: new CreateMarketingSpotAction({ catalogObjectExpression: componentConfig.selectedItemsValueExpression }),
        }),
      ],
      after: [
        Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID }),
      ],
    }), config));
  }
}

export default AddActionsToCatalogRepositoryContextMenuPlugin;
