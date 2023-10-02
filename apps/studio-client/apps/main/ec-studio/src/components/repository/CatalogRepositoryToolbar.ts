import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import RemoveItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/RemoveItemsPlugin";
import OpenEntitiesInTabsAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import RepositoryToolbar from "@coremedia/studio-client.main.editor-components/sdk/collectionview/RepositoryToolbar";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin from "../../ECommerceStudioPlugin";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";

interface CatalogRepositoryToolbarConfig extends Config<RepositoryToolbar> {
}

class CatalogRepositoryToolbar extends RepositoryToolbar {
  declare Config: CatalogRepositoryToolbarConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryToolbar";

  constructor(config: Config<CatalogRepositoryToolbar> = null) {
    super(ConfigUtils.apply(Config(CatalogRepositoryToolbar, {
      ariaLabel: ECommerceStudioPlugin_properties.CollectionView_catalogRepositoryToolbar_label,
      itemId: "commerceToolbar",

      ...ConfigUtils.append({
        plugins: [
          Config(AddItemsPlugin, {
            items: [
              Config(IconButton, {
                itemId: ECommerceStudioPlugin.OPEN_IN_TAB_BUTTON_ITEM_ID,
                baseAction: new OpenEntitiesInTabsAction({ entitiesValueExpression: config.selectedItemsValueExpression }),
              }),
            ],
            before: [
              Config(Component, { itemId: RepositoryToolbar.REPOSITORY_TOOLBAR_SPACER_THIRD_ITEM_ID }),
            ],
          }),
          Config(RemoveItemsPlugin, {
            items: [
              Config(Component, { itemId: RepositoryToolbar.OPEN_BUTTON_ITEM_ID }),
              Config(Component, { itemId: RepositoryToolbar.CUT_BUTTON_ITEM_ID }),
              Config(Component, { itemId: RepositoryToolbar.PASTE_BUTTON_ITEM_ID }),
            ],
          }),
        ],
      }),

    }), config));
  }
}

export default CatalogRepositoryToolbar;
