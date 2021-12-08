import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import JsonStore from "@jangaroo/ext-ts/data/JsonStore";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import int from "@jangaroo/runtime/int";
import EsAnalyticsChartBase from "./EsAnalyticsChartBase";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";
import EsChart from "./EsChart";

interface EsAnalyticsChartConfig extends Config<EsAnalyticsChartBase>, Partial<Pick<EsAnalyticsChart,
  "timeStampValueExpression" |
  "chartTitle" |
  "chartHeight"
>> {
}

class EsAnalyticsChart extends EsAnalyticsChartBase {
  declare Config: EsAnalyticsChartConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChart";

  static readonly TIME_RANGE_WEEK: int = 7;

  static readonly TIME_RANGE_MONTH: int = 30;

  static readonly TIME_RANGE_COMBO_ITEM_ID: string = "esAlxTimeRangeComboId";

  static readonly ES_CHART_ITEM_ID: string = "esChartItemId";

  constructor(config: Config<EsAnalyticsChart> = null) {
    config = ConfigUtils.apply({ chartHeight: 250 }, config);
    super(ConfigUtils.apply(Config(EsAnalyticsChart, {
      ...{ labelAlign: "left" },

      items: [
        Config(LocalComboBox, {
          itemId: EsAnalyticsChart.TIME_RANGE_COMBO_ITEM_ID,
          displayField: "value",
          valueField: "id",
          encodeItems: true,
          fieldLabel: EsAnalyticsStudioPlugin_properties.chart_time_range_label,
          value: EsAnalyticsChart.TIME_RANGE_WEEK,
          ...ConfigUtils.append({
            plugins: [
              Config(BindPropertyPlugin, {
                bindTo: config.timeRangeValueExpression,
                bidirectional: true,
                componentEvent: "select",
              }),
            ],
          }),
          store: new JsonStore({
            fields: ["id", "value"],
            data: [
              {
                id: EsAnalyticsChart.TIME_RANGE_WEEK,
                value: EsAnalyticsStudioPlugin_properties.chart_last_7_days,
              },
              {
                id: EsAnalyticsChart.TIME_RANGE_MONTH,
                value: EsAnalyticsStudioPlugin_properties.chart_last_30_days,
              },
            ],
          }),
        }),
        Config(EsChart, {
          bindTo: config.bindTo,
          fieldLabel: config.chartTitle,
          labelAlign: "top",
          chartHeight: config.chartHeight,
          timeStampValueExpression: config.timeStampValueExpression,
          itemId: EsAnalyticsChart.ES_CHART_ITEM_ID,
        }),
      ],

      layout: Config(VBoxLayout, { align: "stretch" }),
      plugins: [
        Config(VerticalSpacingPlugin),
      ],

    }), config));
  }

  timeStampValueExpression: ValueExpression = null;

  chartTitle: string = null;

  chartHeight: number = NaN;
}

export default EsAnalyticsChart;
