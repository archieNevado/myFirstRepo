import ECommerceStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.ec-studio/ECommerceStudioPlugin_properties";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import CommerceCatalogHierarchyForm from "@coremedia-blueprint/studio-client.main.lc-studio/desktop/CommerceCatalogHierarchyForm";
import StudioAppsImpl from "@coremedia/studio-client.app-context-models/apps/StudioAppsImpl";
import studioApps from "@coremedia/studio-client.app-context-models/apps/studioApps";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import AddQuickTipPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddQuickTipPlugin";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import ExtensionsMenuToolbar from "@coremedia/studio-client.main.editor-components/sdk/desktop/ExtensionsMenuToolbar";
import WorkAreaTabProxiesContextMenu from "@coremedia/studio-client.main.editor-components/sdk/desktop/reusability/WorkAreaTabProxiesContextMenu";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import AddCatalogActionsToWorkAreaTabContextMenuPlugin from "./AddCatalogActionsToWorkAreaTabContextMenuPlugin";
import AddCatalogLinkActionsPlugin from "./AddCatalogLinkActionsPlugin";
import EcommerceIbmStudioPlugin_properties from "./EcommerceIbmStudioPlugin_properties";
import OpenManagementCenterAction from "./action/OpenManagementCenterAction";
import OpenManagementCenterButton from "./components/OpenManagementCenterButton";
import EcommerceIbmCollectionViewActionsPlugin from "./library/EcommerceIbmCollectionViewActionsPlugin";

interface EcommerceIbmStudioPluginConfig extends Config<StudioPlugin> {
}

/* Extend the Live Context Studio for IBM WCS

 @deprecated This plugin is part of the legacy Blueprint commerce integration and has been deprecated
 in favour of the Commerce Hub integration.
 */
class EcommerceIbmStudioPlugin extends StudioPlugin {
  declare Config: EcommerceIbmStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.livecontext.ibm.studio.config.ecommerceIbmStudioPlugin";

  /**
   * The itemId of the open in management center menu item.
   */
  static readonly OPEN_IN_MCENTER_MENU_ITEM_ID: string = "openInManagementCenter";

  /**
   * The itemId of the open in management center button.
   */
  static readonly OPEN_IN_MCENTER_BUTTON_ITEM_ID: string = "openInManagementCenter";

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    cast(StudioAppsImpl, studioApps._).getSubAppLauncherRegistry().registerSubAppLauncher("cmWcsManagementCenter", (): void => {
      const openWcsAction = new OpenManagementCenterAction();
      openWcsAction.execute();
    });
  }

  constructor(config: Config<EcommerceIbmStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(EcommerceIbmStudioPlugin, {

      rules: [
        Config(CollectionView, {
          plugins: [
            Config(EcommerceIbmCollectionViewActionsPlugin),
          ],
        }),

        Config(ExtensionsMenuToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(OpenManagementCenterButton, { itemId: "openManagementCenter" }),
              ],
            }),
          ],
        }),

        Config(WorkAreaTabProxiesContextMenu, {
          plugins: [
            Config(AddCatalogActionsToWorkAreaTabContextMenuPlugin),
          ],
        }),

        Config(CommerceCatalogHierarchyForm, {
          plugins: [
            Config(AddQuickTipPlugin, { text: EcommerceIbmStudioPlugin_properties.AugmentedCategory_help_categorymanagement_tooltip }),
          ],
        }),

        Config(CatalogLinkPropertyField, {
          plugins: [
            Config(AddCatalogLinkActionsPlugin),
          ],
        }),

      ],

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties),
          source: resourceManager.getResourceBundle(null, EcommerceIbmStudioPlugin_properties),
        }),
      ],

    }), config));
  }
}

export default EcommerceIbmStudioPlugin;
