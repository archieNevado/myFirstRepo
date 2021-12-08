import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EsAnalyticsChart from "./EsAnalyticsChart";
import EsAnalyticsChartWidgetBase from "./EsAnalyticsChartWidgetBase";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";
import EsChart from "./EsChart";

interface EsAnalyticsChartWidgetConfig extends Config<EsAnalyticsChartWidgetBase> {
}

class EsAnalyticsChartWidget extends EsAnalyticsChartWidgetBase {
  declare Config: EsAnalyticsChartWidgetConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartWidget";

  static readonly ES_ALX_CHART: string = "esAlxChartItemId";

  constructor(config: Config<EsAnalyticsChartWidget> = null) {
    super((()=> ConfigUtils.apply(Config(EsAnalyticsChartWidget, {
      layout: "anchor",
      scrollable: true,

      items: [
        Config(EsAnalyticsChart, {
          itemId: EsAnalyticsChartWidget.ES_ALX_CHART,
          bindTo: this.getAlxData("data"),
          timeRangeValueExpression: this.getTimeRangeValueExpression(),
          chartTitle: EsAnalyticsStudioPlugin_properties.chart_title_page_views,
          timeStampValueExpression: this.getAlxData("timeStamp"),
          chartHeight: 240,
        }),

        Config(EsChart, {
          bindTo: this.getPublicationData("data"),
          timeStampValueExpression: this.getPublicationData("timeStamp"),
          chartHeight: 200,
          color: "#7ed900",
          fieldLabel: EsAnalyticsStudioPlugin_properties.chart_title_publications,
          labelAlign: "top",
          chartLabelName: EsAnalyticsStudioPlugin_properties.chart_label_publications,
        }),
      ],

    }), config))());
  }
}

export default EsAnalyticsChartWidget;
