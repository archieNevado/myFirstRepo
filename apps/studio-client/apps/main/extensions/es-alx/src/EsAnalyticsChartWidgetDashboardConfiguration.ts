import DashboardConfiguration from "@coremedia/studio-client.main.editor-components/sdk/dashboard/DashboardConfiguration";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EsAnalyticsChartWidgetState from "./EsAnalyticsChartWidgetState";
import EsAnalyticsChartWidgetType from "./EsAnalyticsChartWidgetType";

interface EsAnalyticsChartWidgetDashboardConfigurationConfig extends Config<DashboardConfiguration> {
}

/** Used in watf tests. Do a full text search to find usage. */
class EsAnalyticsChartWidgetDashboardConfiguration extends DashboardConfiguration {
  declare Config: EsAnalyticsChartWidgetDashboardConfigurationConfig;

  static override readonly xtype: string = "acme.config.esAnalyticsChartWidgetDashboardConfiguration";

  constructor(config: Config<EsAnalyticsChartWidgetDashboardConfiguration> = null) {
    super(ConfigUtils.apply(Config(EsAnalyticsChartWidgetDashboardConfiguration, {

      widgets: [
        new EsAnalyticsChartWidgetState({}),
      ],

      types: [
        new EsAnalyticsChartWidgetType({}),
      ],

    }), config));
  }
}

export default EsAnalyticsChartWidgetDashboardConfiguration;
