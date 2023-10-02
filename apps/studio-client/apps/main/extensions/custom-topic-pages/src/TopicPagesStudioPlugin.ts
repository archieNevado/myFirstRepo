import StudioAppsImpl from "@coremedia/studio-client.app-context-models/apps/StudioAppsImpl";
import studioApps from "@coremedia/studio-client.app-context-models/apps/studioApps";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import ExtensionsMenuToolbar from "@coremedia/studio-client.main.editor-components/sdk/desktop/ExtensionsMenuToolbar";
import Button from "@jangaroo/ext-ts/button/Button";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TopicPages_properties from "./TopicPages_properties";
import OpenTopicPagesEditorAction from "./administration/OpenTopicPagesEditorAction";

interface TopicPagesStudioPluginConfig extends Config<StudioPlugin> {
}

class TopicPagesStudioPlugin extends StudioPlugin {
  declare Config: TopicPagesStudioPluginConfig;

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    cast(StudioAppsImpl, studioApps._).getSubAppLauncherRegistry().registerSubAppLauncher("cmTopicPages", (): void => {
      const openTopicPagesEditorAction = new OpenTopicPagesEditorAction();
      openTopicPagesEditorAction.execute();
    });
  }

  constructor(config: Config<TopicPagesStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(TopicPagesStudioPlugin, {

      rules: [

        Config(ExtensionsMenuToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Button, {
                  text: TopicPages_properties.TopicPages_administration_title,
                  iconAlign: "top",
                  itemId: "btn-topicpages-editor",
                  id: "btn-topicpages-editor",
                  iconCls: TopicPages_properties.TopicPages_administration_icon,
                  baseAction: new OpenTopicPagesEditorAction({}),
                }),
              ],
            }),
          ],
        }),

      ],

    }), config));
  }
}

export default TopicPagesStudioPlugin;
