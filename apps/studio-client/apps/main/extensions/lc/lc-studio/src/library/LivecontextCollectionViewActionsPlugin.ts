import CatalogRepositoryContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryContextMenu";
import CatalogRepositoryToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/repository/CatalogRepositoryToolbar";
import CatalogSearchContextMenu from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchContextMenu";
import CatalogSearchToolbar from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/CatalogSearchToolbar";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import TreeViewContextMenu from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/TreeViewContextMenu";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AddActionsToCatalogToolbarPlugin from "./AddActionsToCatalogToolbarPlugin";
import AddActionsToCommonCatalogContextMenuPlugin from "./AddActionsToCommonCatalogContextMenuPlugin";
import AddCatalogActionsToTreeViewContextMenuPlugin from "./AddCatalogActionsToTreeViewContextMenuPlugin";

interface LivecontextCollectionViewActionsPluginConfig extends Config<StudioPlugin> {
}

/* Extend the standard Studio and Blueprint components for Live Context */
class LivecontextCollectionViewActionsPlugin extends StudioPlugin {
  declare Config: LivecontextCollectionViewActionsPluginConfig;

  constructor(config: Config<LivecontextCollectionViewActionsPlugin> = null) {
    super(ConfigUtils.apply(Config(LivecontextCollectionViewActionsPlugin, {

      rules: [
        Config(CatalogRepositoryToolbar, {
          plugins: [
            Config(AddActionsToCatalogToolbarPlugin),
          ],
        }),
        Config(CatalogRepositoryContextMenu, {
          plugins: [
            Config(AddActionsToCommonCatalogContextMenuPlugin),
          ],
        }),
        Config(CatalogSearchToolbar, {
          plugins: [
            Config(AddActionsToCatalogToolbarPlugin),
          ],
        }),
        Config(CatalogSearchContextMenu, {
          plugins: [
            Config(AddActionsToCommonCatalogContextMenuPlugin),
          ],
        }),
        Config(TreeViewContextMenu, {
          plugins: [
            Config(AddCatalogActionsToTreeViewContextMenuPlugin),
          ],
        }),
      ],
    }), config));
  }
}

export default LivecontextCollectionViewActionsPlugin;
