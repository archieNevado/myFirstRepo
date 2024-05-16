import CKEditorFeatureFlags from "@coremedia/studio-client.ckeditor-common/CKEditorFeatureFlags";
import CKEditor5RichTextArea from "@coremedia/studio-client.ext.ckeditor5-components/CKEditor5RichTextArea";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import OnlyIf from "@coremedia/studio-client.main.editor-components/sdk/plugins/OnlyIf";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CKEditor5FeatureFlagPlugin from "./CKEditor5FeatureFlagPlugin";

interface CKEditor5StudioPluginConfig extends Config<StudioPlugin> {
}

class CKEditor5StudioPlugin extends StudioPlugin {
  declare Config: CKEditor5StudioPluginConfig;

  static readonly xtype: string = "com.coremedia.cms.studio.ckeditor5plugin.config.cke5studioPlugin";

  constructor(config: Config<CKEditor5StudioPlugin> = null) {
    super(ConfigUtils.apply(Config(CKEditor5StudioPlugin, {

      rules: [
        Config(CKEditor5RichTextArea, {
          plugins: [
            Config(OnlyIf, {
              isAdministrator: true,
              then: Config(CKEditor5FeatureFlagPlugin, { featureFlags: [CKEditorFeatureFlags.ADMINISTRATIVE] }),
            }),
          ],
        }),
      ],
    }), config));
  }
}

export default CKEditor5StudioPlugin;
