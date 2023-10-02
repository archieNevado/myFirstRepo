import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ThumbnailResolverFactory from "@coremedia/studio-client.ext.cap-base-components/thumbnails/ThumbnailResolverFactory";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import MetaStyleService from "@coremedia/studio-client.main.editor-components/sdk/util/MetaStyleService";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import P13NStudioPlugin from "./P13NStudioPlugin";

interface P13NStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class P13NStudioPluginBase extends StudioPlugin {
  declare Config: P13NStudioPluginBaseConfig;

  constructor(config: Config<P13NStudioPlugin> = null) {
    super(config);
  }

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    editorContext.registerContentInitializer("CMP13NSearch", bind(this, this.#initP13NSearch));

    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_YELLOW, [
      "CMP13NSearch", "CMP13NSearch",
    ]);

    //TODO: is this line needed?
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMSelectionRules", "defaultContent"));
  }

  #initP13NSearch(content: Content): void {
    content.getProperties().set("documentType", "Document_");
    editorContext._.getContentInitializer("CMLocalized")(content);
  }
}

export default P13NStudioPluginBase;
