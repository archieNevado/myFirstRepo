import ContentHub_properties from "@coremedia/studio-client.main.content-hub-editor-components/ContentHub_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ContentHubRss_properties from "./ContentHubRss_properties";

interface ContentHubStudioRssPluginConfig extends Config<StudioPlugin> {
}

class ContentHubStudioRssPlugin extends StudioPlugin {
  declare Config: ContentHubStudioRssPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.contenthub.rss.ContentHubStudioRssPlugin";

  constructor(config: Config<ContentHubStudioRssPlugin> = null) {
    super(ConfigUtils.apply(Config(ContentHubStudioRssPlugin, {

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentHub_properties),
          source: resourceManager.getResourceBundle(null, ContentHubRss_properties),
        }),
      ],

    }), config));
  }
}

export default ContentHubStudioRssPlugin;
