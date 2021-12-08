import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import SwitchViewButtonsContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SwitchViewButtonsContainer";
import Container from "@jangaroo/ext-ts/container/Container";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogSearchToolbar from "./CatalogSearchToolbar";

interface CatalogSearchToolbarContainerConfig extends Config<Container>, Partial<Pick<CatalogSearchToolbarContainer,
  "selectedSearchItemsValueExpression"
>> {
}

class CatalogSearchToolbarContainer extends Container {
  declare Config: CatalogSearchToolbarContainerConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchToolbarContainer";

  /**
   * Used for extensions
   */
  static readonly CATALOG_SEARCH_TOOLBAR_ITEM_ID: string = "catalogSearchToolbar";

  selectedSearchItemsValueExpression: ValueExpression = null;

  constructor(config: Config<CatalogSearchToolbarContainer> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogSearchToolbarContainer, {
      itemId: CatalogSearchToolbarContainer.CATALOG_SEARCH_TOOLBAR_ITEM_ID,

      items: [
        Config(CatalogSearchToolbar, { selectedSearchItemsValueExpression: config.selectedSearchItemsValueExpression }),
        Config(Toolbar, {
          itemId: "switchViewButtonsToolbar",
          ariaLabel: Editor_properties.CollectionView_switchView_toolbar_label,
          ui: ToolbarSkin.LIGHT.getSkin(),
          items: [
            Config(SwitchViewButtonsContainer, { scope: this }),
          ],
        }),
      ],
      layout: Config(HBoxLayout),

    }), config))());
  }
}

export default CatalogSearchToolbarContainer;
