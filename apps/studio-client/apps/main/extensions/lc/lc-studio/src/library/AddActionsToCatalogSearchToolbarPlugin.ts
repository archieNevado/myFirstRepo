import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import CatalogSearchToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchToolbar";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import Component from "@jangaroo/ext-ts/Component";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin from "../LivecontextStudioPlugin";
import AugmentCategoryAction from "../action/AugmentCategoryAction";
import AugmentProductAction from "../action/AugmentProductAction";
import CreateMarketingSpotAction from "../action/CreateMarketingSpotAction";
import CreateProductTeaserAction from "../action/CreateProductTeaserAction";
import SearchProductVariantsAction from "../action/SearchProductVariantsAction";

interface AddActionsToCatalogSearchToolbarPluginConfig extends Config<AddItemsPlugin> {
}

class AddActionsToCatalogSearchToolbarPlugin extends AddItemsPlugin {
  declare Config: AddActionsToCatalogSearchToolbarPluginConfig;

  constructor(config: Config<AddActionsToCatalogSearchToolbarPlugin> = null) {
    const componentConfig = cast(CatalogSearchToolbar, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddActionsToCatalogSearchToolbarPlugin, {
      items: [
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_BUTTON_ITEM_ID,
          baseAction: new SearchProductVariantsAction({ catalogObjectExpression: componentConfig.selectedSearchItemsValueExpression }),
        }),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.AUGMENT_PRODUCT_BUTTON_ITEM_ID,
          baseAction: new AugmentProductAction({ catalogObjectExpression: componentConfig.selectedSearchItemsValueExpression }),
        }),
        Config(Separator),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.CREATE_PRODUCT_TEASER_BUTTON_ITEM_ID,
          baseAction: new CreateProductTeaserAction({ catalogObjectExpression: componentConfig.selectedSearchItemsValueExpression }),
        }),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.AUGMENT_CATEGORY_BUTTON_ITEM_ID,
          baseAction: new AugmentCategoryAction({ catalogObjectExpression: componentConfig.selectedSearchItemsValueExpression }),
        }),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.CREATE_MARKETING_SPOT_BUTTON_ITEM_ID,
          baseAction: new CreateMarketingSpotAction({ catalogObjectExpression: componentConfig.selectedSearchItemsValueExpression }),
        }),
      ],
      after: [
        Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID }),
      ],

    }), config));
  }
}

export default AddActionsToCatalogSearchToolbarPlugin;
