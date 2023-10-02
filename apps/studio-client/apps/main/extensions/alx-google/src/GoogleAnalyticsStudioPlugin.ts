import AnalyticsDeepLinkButtonContainer from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/AnalyticsDeepLinkButtonContainer";
import AnalyticsMiscPanel from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/AnalyticsMiscPanel";
import AnalyticsProvider from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/AnalyticsProvider";
import AnalyticsRetrievalPanel from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/AnalyticsRetrievalPanel";
import AnalyticsTrackingPanel from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/AnalyticsTrackingPanel";
import CMALXEventListForm from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/CMALXEventListForm";
import CMALXPageListForm from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/CMALXPageListForm";
import OpenAnalyticsHomeUrlButton from "@coremedia-blueprint/studio-client.main.alx-studio-plugin/OpenAnalyticsHomeUrlButton";
import StudioAppsImpl from "@coremedia/studio-client.app-context-models/apps/StudioAppsImpl";
import studioApps from "@coremedia/studio-client.app-context-models/apps/studioApps";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import ExtensionsMenuToolbar from "@coremedia/studio-client.main.editor-components/sdk/desktop/ExtensionsMenuToolbar";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import GoogleAnalyticsCMALXBaseListRetrievalTab from "./GoogleAnalyticsCMALXBaseListRetrievalTab";
import GoogleAnalyticsMiscFields from "./GoogleAnalyticsMiscFields";
import GoogleAnalyticsReportPreviewButton from "./GoogleAnalyticsReportPreviewButton";
import GoogleAnalyticsRetrievalFields from "./GoogleAnalyticsRetrievalFields";
import GoogleAnalyticsStudioPluginContentTypes_properties from "./GoogleAnalyticsStudioPluginContentTypes_properties";
import GoogleAnalyticsStudioPlugin_properties from "./GoogleAnalyticsStudioPlugin_properties";
import GoogleAnalyticsTrackingFields from "./GoogleAnalyticsTrackingFields";

interface GoogleAnalyticsStudioPluginConfig extends Config<StudioPlugin> {
}

class GoogleAnalyticsStudioPlugin extends StudioPlugin {
  declare Config: GoogleAnalyticsStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsStudioPlugin";

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    const buttonCfg = Config(OpenAnalyticsHomeUrlButton);
    buttonCfg.serviceName = "googleAnalytics";
    buttonCfg.ariaLabel = "ignored";
    const button = new OpenAnalyticsHomeUrlButton(buttonCfg);

    cast(StudioAppsImpl, studioApps._).getSubAppLauncherRegistry().registerSubAppLauncher("cmGoogleAnalytics", (): void => {
      button.handler();
    });
  }

  constructor(config: Config<GoogleAnalyticsStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(GoogleAnalyticsStudioPlugin, {

      rules: [
        Config(AnalyticsRetrievalPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(GoogleAnalyticsRetrievalFields),
              ],
            }),
          ],
        }),

        Config(AnalyticsTrackingPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(GoogleAnalyticsTrackingFields),
              ],
            }),
          ],
        }),

        Config(AnalyticsMiscPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(GoogleAnalyticsMiscFields),
              ],
            }),
          ],
        }),

        Config(CMALXPageListForm, {
          plugins: [
            Config(AddItemsPlugin, {
              index: 1,
              items: [
                Config(GoogleAnalyticsCMALXBaseListRetrievalTab),
              ],
            }),
          ],
        }),

        Config(CMALXEventListForm, {
          plugins: [
            Config(AddItemsPlugin, {
              index: 1,
              items: [
                Config(GoogleAnalyticsCMALXBaseListRetrievalTab),
              ],
            }),
          ],
        }),

        Config(ExtensionsMenuToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(OpenAnalyticsHomeUrlButton, {
                  itemId: "btn-analytics-googleAnalytics",
                  tooltip: GoogleAnalyticsStudioPlugin_properties.googleanalytics_fav_btn_tooltip,
                  text: GoogleAnalyticsStudioPlugin_properties.googleanalytics_fav_btn_text,
                  iconCls: CoreIcons_properties.analytics,
                  serviceName: "googleAnalytics",
                }),
              ],
            }),
          ],
        }),

        Config(AnalyticsDeepLinkButtonContainer, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(GoogleAnalyticsReportPreviewButton),
              ],
            }),
          ],
        }),
      ],

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, GoogleAnalyticsStudioPluginContentTypes_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Editor_properties),
          source: resourceManager.getResourceBundle(null, GoogleAnalyticsStudioPlugin_properties),
        }),
        new AnalyticsProvider({
          providerName: "googleAnalytics",
          localizedProviderName: GoogleAnalyticsStudioPlugin_properties.googleanalytics_service_provider,
        }),
      ],

    }), config));
  }
}

export default GoogleAnalyticsStudioPlugin;
