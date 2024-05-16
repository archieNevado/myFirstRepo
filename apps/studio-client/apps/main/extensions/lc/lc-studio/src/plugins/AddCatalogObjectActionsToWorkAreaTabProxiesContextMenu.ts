import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import Actions_properties from "@coremedia/studio-client.main.editor-components/sdk/actions/Actions_properties";
import ShowTabEntityInRepositoryAction from "@coremedia/studio-client.main.editor-components/sdk/actions/ShowTabEntityInRepositoryAction";
import WorkAreaTabProxiesContextMenu from "@coremedia/studio-client.main.editor-components/sdk/desktop/reusability/WorkAreaTabProxiesContextMenu";
import Item from "@jangaroo/ext-ts/menu/Item";
import Separator from "@jangaroo/ext-ts/menu/Separator";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin from "../LivecontextStudioPlugin";
import LivecontextStudioPluginBase from "../LivecontextStudioPluginBase";
import AugmentCategoryAction from "../action/AugmentCategoryAction";
import AugmentProductAction from "../action/AugmentProductAction";
import CreateProductTeaserAction from "../action/CreateProductTeaserAction";

interface AddCatalogObjectActionsToWorkAreaTabProxiesContextMenuConfig extends Config<AddItemsPlugin> {
}

class AddCatalogObjectActionsToWorkAreaTabProxiesContextMenu extends AddItemsPlugin {
  declare Config: AddCatalogObjectActionsToWorkAreaTabProxiesContextMenuConfig;

  constructor(config: Config<AddCatalogObjectActionsToWorkAreaTabProxiesContextMenu> = null) {
    const component = cast(WorkAreaTabProxiesContextMenu, config.cmp);
    const componentConfig = cast(WorkAreaTabProxiesContextMenu, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddCatalogObjectActionsToWorkAreaTabProxiesContextMenu, {

      items: [
        Config(Separator),
        Config(Item, {
          itemId: LivecontextStudioPlugin.SHOW_CONTENT_OF_ACTIVE_TAB_IN_LIBRARY_MENU_ITEM_ID,
          baseAction: new ShowTabEntityInRepositoryAction({
            contextClickedTabPanelValueExpression: component.getContextClickedTabPanelValueExpression(),
            contextClickedTabValueExpression: component.getContextClickedTabValueExpression(),
            text: Actions_properties.Action_showInTree_text,
            entityType: CatalogObject,
            handleEntity: LivecontextStudioPluginBase.showInCatalogTree,
          }),
        }),
        Config(Separator),
        Config(Item, { baseAction: new CreateProductTeaserAction({ catalogObjectExpression: componentConfig.tabEntityExpression }) }),
        Config(Item, { baseAction: new AugmentCategoryAction({ catalogObjectExpression: componentConfig.tabEntityExpression }) }),
        Config(Item, { baseAction: new AugmentProductAction({ catalogObjectExpression: componentConfig.tabEntityExpression }) }),
      ],
    }), config));
  }
}

export default AddCatalogObjectActionsToWorkAreaTabProxiesContextMenu;
