import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import MetaStyleService from "@coremedia/studio-client.main.editor-components/sdk/util/MetaStyleService";
import Config from "@jangaroo/runtime/Config";
import ElasticSocialStudioPlugin from "./ElasticSocialStudioPlugin";

interface ElasticSocialStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class ElasticSocialStudioPluginBase extends StudioPlugin {
  declare Config: ElasticSocialStudioPluginBaseConfig;

  constructor(config: Config<ElasticSocialStudioPlugin> = null) {
    super(config);
  }

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    // Colorful Studio styles
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_YELLOW, ["ESDynamicList"]);
  }

}

export default ElasticSocialStudioPluginBase;
