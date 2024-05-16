import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Config from "@jangaroo/runtime/Config";
import LivecontextAssetStudioPlugin from "./LivecontextAssetStudioPlugin";

interface LivecontextAssetStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class LivecontextAssetStudioPluginBase extends StudioPlugin {
  declare Config: LivecontextAssetStudioPluginBaseConfig;

  constructor(config: Config<LivecontextAssetStudioPlugin> = null) {
    super(config);
  }
}

export default LivecontextAssetStudioPluginBase;
