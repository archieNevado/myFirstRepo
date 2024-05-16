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
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BooleanPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import IntegerPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/IntegerPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Container from "@jangaroo/ext-ts/container/Container";
import TextField from "@jangaroo/ext-ts/form/field/Text";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import WebtrendsCMALXBaseListRetrievalTab from "./WebtrendsCMALXBaseListRetrievalTab";
import WebtrendsReportPreviewButton from "./WebtrendsReportPreviewButton";
import WebtrendsRetrievalFields from "./WebtrendsRetrievalFields";
import WebtrendsStudioPluginContentTypes_properties from "./WebtrendsStudioPluginContentTypes_properties";
import WebtrendsStudioPlugin_properties from "./WebtrendsStudioPlugin_properties";

interface WebtrendsStudioPluginConfig extends Config<StudioPlugin> {
}

class WebtrendsStudioPlugin extends StudioPlugin {
  declare Config: WebtrendsStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.config.webtrends.webtrendsStudioPlugin";

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    const buttonCfg = Config(OpenAnalyticsHomeUrlButton);
    buttonCfg.serviceName = "webtrends";
    buttonCfg.ariaLabel = "ignored";
    const button = new OpenAnalyticsHomeUrlButton(buttonCfg);

    cast(StudioAppsImpl, studioApps._).getSubAppLauncherRegistry().registerSubAppLauncher("cmWebtrendsAnalytics", (): void => {
      typeof button.handler !== "string" && button.handler(button, null);
    });
  }

  constructor(config: Config<WebtrendsStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(WebtrendsStudioPlugin, {

      rules: [
        Config(AnalyticsRetrievalPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(WebtrendsRetrievalFields),
              ],
            }),
          ],
        }),
        Config(AnalyticsTrackingPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(PropertyFieldGroup, {
                  itemId: "webtrendsTrackingForm",
                  title: WebtrendsStudioPlugin_properties.SpacerTitle_webtrends_tracking,
                  items: [
                    Config(BooleanPropertyField, {
                      dontTransformToInteger: true,
                      propertyName: "localSettings.webtrends.disabled",
                    }),
                    Config(StringPropertyField, { propertyName: "localSettings.webtrends.dcsid" }),
                    Config(StringPropertyField, { propertyName: "localSettings.webtrends.dcssip" }),
                  ],
                }),
              ],
            }),
          ],
        }),
        Config(AnalyticsMiscPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(PropertyFieldGroup, {
                  itemId: "webtrendsConfigForm",
                  title: WebtrendsStudioPlugin_properties.SpacerTitle_webtrends_studio_config,
                  items: [
                    Config(StringPropertyField, {
                      propertyName: "localSettings.webtrends.homeUrl",
                      listeners: {
                        afterrender: (c: Container): void => {
                          cast(TextField, c.down("textfield")).vtype = "url";
                        },
                      },
                    }),
                    Config(StringPropertyField, { propertyName: "localSettings.webtrends.reportUrlPrefix" }),
                    Config(IntegerPropertyField, { propertyName: "localSettings.webtrends.spaceId" }),
                    Config(StringPropertyField, { propertyName: "localSettings.webtrends.reportId" }),
                    Config(IntegerPropertyField, { propertyName: "localSettings.webtrends.profileId" }),
                    Config(StringPropertyField, { propertyName: "localSettings.webtrends.liveUrlPrefix" }),
                  ],
                }),
              ],
            }),
          ],
        }),
        Config(CMALXPageListForm, {
          plugins: [
            Config(AddItemsPlugin, {
              index: 1,
              items: [
                Config(WebtrendsCMALXBaseListRetrievalTab, { itemId: "webtrendsTab" }),
              ],
            }),
          ],
        }),
        Config(CMALXEventListForm, {
          plugins: [
            Config(AddItemsPlugin, {
              index: 1,
              items: [
                Config(WebtrendsCMALXBaseListRetrievalTab, { itemId: "webtrendsTab" }),
              ],
            }),
          ],
        }),
        Config(AnalyticsDeepLinkButtonContainer, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(WebtrendsReportPreviewButton),
              ],
            }),
          ],
        }),
      ],
      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, WebtrendsStudioPluginContentTypes_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Editor_properties),
          source: resourceManager.getResourceBundle(null, WebtrendsStudioPlugin_properties),
        }),
        new AnalyticsProvider({
          providerName: "webtrends",
          localizedProviderName: WebtrendsStudioPlugin_properties.webtrends_service_provider,
        }),
      ],

    }), config));
  }
}

export default WebtrendsStudioPlugin;
