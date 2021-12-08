import ckEditorFactory from "@coremedia/studio-client.ckeditor-factory/util/ckEditorFactory";
import CoreMediaRichTextArea
  from "@coremedia/studio-client.main.ckeditor4-components/CoreMediaRichTextArea";
import RichTextArea from "@coremedia/studio-client.main.ckeditor4-components/RichTextArea";
import RichTextDropTargetPlugin
  from "@coremedia/studio-client.main.ckeditor4-components/dragdrop/RichTextDropTargetPlugin";
import RichTextPropertyField
  from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import TeaserOverlayPropertyField
  from "@coremedia/studio-client.main.ckeditor4-components/fields/TeaserOverlayPropertyField";
import RichTextAreaPlugin
  from "@coremedia/studio-client.main.ckeditor4-components/plugins/RichTextAreaPlugin";
import RichTextPropertyFieldContextMenuPlugin
  from "@coremedia/studio-client.main.ckeditor4-components/plugins/RichTextPropertyFieldContextMenuPlugin";
import RichTextPropertyFieldToolbarPlugin
  from "@coremedia/studio-client.main.ckeditor4-components/plugins/RichTextPropertyFieldToolbarPlugin";
import TeaserOverlayPropertyFieldToolbarPlugin
  from "@coremedia/studio-client.main.ckeditor4-components/plugins/TeaserOverlayPropertyFieldToolbarPlugin";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import OnlyIf from "@coremedia/studio-client.main.editor-components/sdk/plugins/OnlyIf";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface CKE4StudioPluginConfig extends Config<StudioPlugin>{
}

class CKE4StudioPlugin extends StudioPlugin {
  declare Config: CKE4StudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.ckeditor4components.config.cke4studioPlugin";

  constructor(config: Config<CKE4StudioPlugin> = null) {
    super(ConfigUtils.apply(Config(CKE4StudioPlugin, {

      rules: [
        Config(CoreMediaRichTextArea, {
          plugins: [
            Config(RichTextDropTargetPlugin, {}),
          ],
        }),
        Config(RichTextPropertyField, {
          plugins: [
            Config(RichTextPropertyFieldToolbarPlugin, {}),
            Config(RichTextPropertyFieldContextMenuPlugin, {}),
          ],
        }),
        Config(TeaserOverlayPropertyField, {
          plugins: [
            Config(TeaserOverlayPropertyFieldToolbarPlugin, {}),
          ],
        }),
        Config(RichTextArea, {
          plugins: [
            Config(OnlyIf, {
              condition: (component: RichTextPropertyField): boolean => ckEditorFactory.isCKEditorMajorVersion(component.editorType, 4),
              then: Config(RichTextAreaPlugin, {}),
            }),
          ],
        }),
      ],
    }), config));
  }
}

export default CKE4StudioPlugin;
