import StudioAppsImpl from "@coremedia/studio-client.app-context-models/apps/StudioAppsImpl";
import studioApps from "@coremedia/studio-client.app-context-models/apps/studioApps";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
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
    super(config);
  }
}

export default TopicPagesStudioPlugin;
