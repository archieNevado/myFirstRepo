import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import SwitchViewButtonsContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/SwitchViewButtonsContainer";
import Container from "@jangaroo/ext-ts/container/Container";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogRepositoryToolbar from "./CatalogRepositoryToolbar";

interface CatalogRepositoryToolbarContainerConfig extends Config<Container>, Partial<Pick<CatalogRepositoryToolbarContainer,
  "selectedFolderValueExpression" |
  "createdContentValueExpression" |
  "selectedItemsValueExpression"
>> {
}

class CatalogRepositoryToolbarContainer extends Container {
  declare Config: CatalogRepositoryToolbarContainerConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryToolbarContainer";

  /**
   * Used for the extension
   */
  static readonly CATALOG_REPOSITORY_TOOLBAR_ITEM_ID: string = "catalogRepositoryToolbar";

  constructor(config: Config<CatalogRepositoryToolbarContainer> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogRepositoryToolbarContainer, {
      itemId: CatalogRepositoryToolbarContainer.CATALOG_REPOSITORY_TOOLBAR_ITEM_ID,

      items: [
        Config(CatalogRepositoryToolbar, {
          createdContentValueExpression: config.createdContentValueExpression,
          selectedFolderValueExpression: config.selectedFolderValueExpression,
          selectedItemsValueExpression: config.selectedItemsValueExpression,
        }),
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

  /**
   * value expression for the selected folder in the library tree
   */
  selectedFolderValueExpression: ValueExpression = null;

  /**
   * value expression that acts as a model for informing a view of a newly created content object.
   */
  createdContentValueExpression: ValueExpression = null;

  /**
   * value expression for the selected items, either in the list view, or - if the selection there is empty - the
   * selected folder in the tree view.
   */
  selectedItemsValueExpression: ValueExpression = null;
}

export default CatalogRepositoryToolbarContainer;
