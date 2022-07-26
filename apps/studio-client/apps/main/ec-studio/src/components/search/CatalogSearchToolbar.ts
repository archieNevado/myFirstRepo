import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import OpenSaveSearchWindowAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenSaveSearchWindowAction";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";
import CatalogRepositoryToolbar from "../repository/CatalogRepositoryToolbar";

interface CatalogSearchToolbarConfig extends Config<CatalogRepositoryToolbar> {
}

class CatalogSearchToolbar extends CatalogRepositoryToolbar {
  declare Config: CatalogSearchToolbarConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchToolbar";

  static readonly SEARCH_TOOLBAR_SAVE_SEPARATOR_ITEM_ID: string = "searchCatalogToolbarSave";

  static readonly SEARCH_TOOLBAR_SAVE_BUTTON_ITEM_ID: string = "saveSearch";

  constructor(config: Config<CatalogSearchToolbar> = null) {
    super(ConfigUtils.apply(Config(CatalogSearchToolbar, {
      itemId: "commerceToolbar",
      ariaLabel: ECommerceStudioPlugin_properties.CollectionView_catalogSearchToolbar_label,
      ...ConfigUtils.append({
        plugins: [
          Config(AddItemsPlugin, {
            index: 0,
            items: [
              Config(IconButton, {
                itemId: CatalogSearchToolbar.SEARCH_TOOLBAR_SAVE_BUTTON_ITEM_ID,
                baseAction: new OpenSaveSearchWindowAction({}),
              }),
              Config(Separator, { itemId: CatalogSearchToolbar.SEARCH_TOOLBAR_SAVE_SEPARATOR_ITEM_ID }),
            ],
          }),
        ],
      }),
    }), config));
  }
}

export default CatalogSearchToolbar;
