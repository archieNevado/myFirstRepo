import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Container from "@jangaroo/ext-ts/container/Container";
import Label from "@jangaroo/ext-ts/form/Label";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";
import EsChartBase from "./EsChartBase";

interface EsChartConfig extends Config<EsChartBase>, Partial<Pick<EsChart,
  "timeStampValueExpression" |
  "chartHeight"
>> {
}

class EsChart extends EsChartBase {
  declare Config: EsChartConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.esanalytics.esChart";

  static readonly NO_DATA_FIELD_ITEM_ID: string = "noDataLabelItemId";

  static readonly CHART_PANEL_ITEM_ID: string = "chartContainerItemId";

  static readonly ES_CHART_SWITCHER_ITEM_ID: string = "esChartSwitcher";

  static readonly TIME_STAMP_ITEM_ID: string = "timeStampItemId";

  constructor(config: Config<EsChart> = null) {
    config = ConfigUtils.apply({ chartHeight: 250 }, config);
    super(ConfigUtils.apply(Config(EsChart, {

      plugins: [
        Config(BindPropertyPlugin, {
          bindTo: config.bindTo,
          componentProperty: "lineChartData",
        }),
      ],
      items: [
        Config(SwitchingContainer, {
          itemId: EsChart.ES_CHART_SWITCHER_ITEM_ID,
          activeItemValueExpression: config.bindTo,
          transformer: EsChartBase.getActiveItemId,
          items: [
            /* this label is only visible when there is no analytics data */
            Config(Label, {
              itemId: EsChart.NO_DATA_FIELD_ITEM_ID,
              text: EsAnalyticsStudioPlugin_properties.chart_data_unavailable,
            }),
            /* this chart panel is only visible when there is valid analytics data */
            Config(Panel, {
              itemId: EsChart.CHART_PANEL_ITEM_ID,
              height: config.chartHeight,
            }),
          ],
        }),
        /* last fetched timestamp */
        Config(Container, {
          items: [
            Config(Label, { text: EsAnalyticsStudioPlugin_properties.chart_time_stamp_update }),
            Config(Label, {
              itemId: EsChart.TIME_STAMP_ITEM_ID,
              plugins: [
                Config(BindPropertyPlugin, {
                  bindTo: config.timeStampValueExpression,
                  componentProperty: "text",
                  transformer: EsChartBase.transformTime,
                }),
              ],
            }),
          ],
          layout: Config(HBoxLayout, { align: "middle" }),
        }),
      ],
      layout: Config(VBoxLayout, { align: "stretch" }),
    }), config));
  }

  timeStampValueExpression: ValueExpression = null;

  chartHeight: number = NaN;
}

export default EsChart;
