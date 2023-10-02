import StudioAppsImpl from "@coremedia/studio-client.app-context-models/apps/StudioAppsImpl";
import studioApps from "@coremedia/studio-client.app-context-models/apps/studioApps";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import ExtensionsMenuToolbar from "@coremedia/studio-client.main.editor-components/sdk/desktop/ExtensionsMenuToolbar";
import Button from "@jangaroo/ext-ts/button/Button";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenOptimizelyServiceUrlAction from "./OpenOptimizelyServiceUrlAction";
import OptimizelyStudioPlugin_properties from "./OptimizelyStudioPlugin_properties";

interface OptimizelyStudioPluginConfig extends Config<StudioPlugin> {
}

class OptimizelyStudioPlugin extends StudioPlugin {
  declare Config: OptimizelyStudioPluginConfig;

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    cast(StudioAppsImpl, studioApps._).getSubAppLauncherRegistry().registerSubAppLauncher("cmOptimizelyAnalytics", (): void => {
      const openOptimizelyServiceUrlAction = new OpenOptimizelyServiceUrlAction();
      openOptimizelyServiceUrlAction.execute();
    });
  }

  constructor(config: Config<OptimizelyStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(OptimizelyStudioPlugin, {

      rules: [
        Config(ExtensionsMenuToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Button, {
                  itemId: "btn-optimizely",
                  tooltip: OptimizelyStudioPlugin_properties.optimizely_fav_btn_tooltip,
                  baseAction: new OpenOptimizelyServiceUrlAction({
                    text: OptimizelyStudioPlugin_properties.optimizely_fav_btn_text,
                    iconCls: CoreIcons_properties.ab_testing_tool,
                  }),
                }),
              ],
              /* insert before spacer, not at the end of the container */
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default OptimizelyStudioPlugin;
