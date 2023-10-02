import ECommerceStudioPlugin from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin";
import CatalogRepositoryToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryToolbar";
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

interface AddActionsToCatalogRepositoryToolbarPluginConfig extends Config<AddItemsPlugin> {
}

class AddActionsToCatalogRepositoryToolbarPlugin extends AddItemsPlugin {
  declare Config: AddActionsToCatalogRepositoryToolbarPluginConfig;

  #componentConfig: CatalogRepositoryToolbar = null;

  constructor(config: Config<AddActionsToCatalogRepositoryToolbarPlugin> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#componentConfig = cast(CatalogRepositoryToolbar, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddActionsToCatalogRepositoryToolbarPlugin, {
      items: [
        Config(Separator, { itemId: "searchProductVariantsSeparator" }),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_BUTTON_ITEM_ID,
          baseAction: new SearchProductVariantsAction({ catalogObjectExpression: this$.#componentConfig.selectedItemsValueExpression }),
        }),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.AUGMENT_CATEGORY_BUTTON_ITEM_ID,
          baseAction: new AugmentCategoryAction({ catalogObjectExpression: this$.#componentConfig.selectedItemsValueExpression }),
        }),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.AUGMENT_PRODUCT_BUTTON_ITEM_ID,
          baseAction: new AugmentProductAction({ catalogObjectExpression: this$.#componentConfig.selectedItemsValueExpression }),
        }),
        Config(Separator, { itemId: "createProductTeaserSeparator" }),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.CREATE_PRODUCT_TEASER_BUTTON_ITEM_ID,
          baseAction: new CreateProductTeaserAction({ catalogObjectExpression: this$.#componentConfig.selectedItemsValueExpression }),
        }),
        Config(IconButton, {
          itemId: LivecontextStudioPlugin.CREATE_MARKETING_SPOT_BUTTON_ITEM_ID,
          baseAction: new CreateMarketingSpotAction({ catalogObjectExpression: this$.#componentConfig.selectedItemsValueExpression }),
        }),
      ],
      after: [
        Config(Component, { itemId: ECommerceStudioPlugin.OPEN_IN_TAB_MENU_ITEM_ID }),
      ],
    }), config));
  }
}

export default AddActionsToCatalogRepositoryToolbarPlugin;
