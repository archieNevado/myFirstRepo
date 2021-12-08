import CMArticleSystemForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/components/CMArticleSystemForm";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import ConfigureDashboardPlugin from "@coremedia/studio-client.main.editor-components/sdk/dashboard/ConfigureDashboardPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EsAnalyticsChartPanel from "./EsAnalyticsChartPanel";
import EsAnalyticsChartWidgetState from "./EsAnalyticsChartWidgetState";
import EsAnalyticsChartWidgetType from "./EsAnalyticsChartWidgetType";
import EsAnalyticsStudioPluginBase from "./EsAnalyticsStudioPluginBase";

interface EsAnalyticsStudioPluginConfig extends Config<EsAnalyticsStudioPluginBase> {
}

class EsAnalyticsStudioPlugin extends EsAnalyticsStudioPluginBase {
  declare Config: EsAnalyticsStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsStudioPlugin";

  constructor(config: Config<EsAnalyticsStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(EsAnalyticsStudioPlugin, {

      rules: [
        /* add the analytics chart panel to the article metadata tab */
        Config(CMArticleSystemForm, {
          plugins: [
            Config(AddItemsPlugin, {
              index: 2,
              items: [
                Config(EsAnalyticsChartPanel),
              ],
            }),
          ],
        }),
      ],

      configuration: [
        new ConfigureDashboardPlugin({
          widgets: [
            new EsAnalyticsChartWidgetState({
              rowspan: 3,
              column: 2,
              content: null,
            }),
          ],
          types: [
            new EsAnalyticsChartWidgetType({}),
          ],
        }),

      ],

    }), config));
  }
}

export default EsAnalyticsStudioPlugin;
