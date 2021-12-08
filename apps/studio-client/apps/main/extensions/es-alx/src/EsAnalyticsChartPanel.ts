import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EsAnalyticsChart from "./EsAnalyticsChart";
import EsAnalyticsChartPanelBase from "./EsAnalyticsChartPanelBase";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";

interface EsAnalyticsChartPanelConfig extends Config<EsAnalyticsChartPanelBase> {
}

class EsAnalyticsChartPanel extends EsAnalyticsChartPanelBase {
  declare Config: EsAnalyticsChartPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartPanel";

  static readonly ES_ALX_CHART: string = "esAlxChartItemId";

  constructor(config: Config<EsAnalyticsChartPanel> = null) {
    super((()=> ConfigUtils.apply(Config(EsAnalyticsChartPanel, {
      title: EsAnalyticsStudioPlugin_properties.chart_container_label,
      itemId: "esAnalyticsChartForm",

      items: [
        Config(EsAnalyticsChart, {
          itemId: EsAnalyticsChartPanel.ES_ALX_CHART,
          bindTo: this.getAlxData("data"),
          timeRangeValueExpression: this.getTimeRangeValueExpression(),
          timeStampValueExpression: this.getAlxData("timeStamp"),
          chartHeight: 200,
        }),
      ],

    }), config))());
  }
}

export default EsAnalyticsChartPanel;
