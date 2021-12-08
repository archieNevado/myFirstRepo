import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import Ext from "@jangaroo/ext-ts";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Format from "@jangaroo/ext-ts/util/Format";
import { as, bind, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";
import EsChart from "./EsChart";

interface EsChartBaseConfig extends Config<FieldContainer>, Config<HidableMixin>, Partial<Pick<EsChartBase,
  "bindTo" |
  "color" |
  "chartLabelName" |
  "hideText"
>> {
}

class EsChartBase extends FieldContainer {
  declare Config: EsChartBaseConfig;

  bindTo: ValueExpression = null;

  color: string = null;

  chartLabelName: string = null;

  #lineChart: any = null;

  #lineChartData: Array<any> = null;

  #chartPanel: Panel = null;

  #switchContainer: SwitchingContainer = null;

  static readonly #MIN_Y_VALUE: number = 10;

  #addedListener: boolean = false;

  readonly #CHART_X_AXIS_PROPERTY_NAME: string = "key";

  readonly #DEFAULT_COLOR: string = "#4189DD";

  readonly #CHART_VALUE_NAMES: Array<any> = ["value"];

  readonly #CHART_LABEL_NAMES: Array<any> = [EsAnalyticsStudioPlugin_properties.chart_label_page_views];

  constructor(config: Config<EsChartBase> = null) {
    super(config);
    this.on("resize", bind(this, this.initChartWhenAvailable));
  }

  protected static getActiveItemId(data: Array<any>): string {
    return ((data && data.length > 0) ? EsChart.CHART_PANEL_ITEM_ID : EsChart.NO_DATA_FIELD_ITEM_ID);
  }

  /**
   * Convert the rawData from the Server to a localized chart data.
   * @param rawData the data array that comes from the server
   * @return the localized chart data
   */
  protected localizeChartData(rawData: Array<any>): Array<any> {
    return rawData.map((rawDataEntry: any): any => {
      const dateXLabel: string = rawDataEntry[this.#CHART_X_AXIS_PROPERTY_NAME];

      const dateProperties: Record<string, any> = {
        year: dateXLabel.slice(0, 4),
        month: dateXLabel.slice(4, 6),
        day: dateXLabel.slice(6, 8),
      };
      const date = new Date(dateProperties.year, dateProperties.month - 1, dateProperties.day);
      return {
        key: Format.dateRenderer(EsAnalyticsStudioPlugin_properties.shortDateFormat)(date),
        value: rawDataEntry[this.#CHART_VALUE_NAMES[0]],
      };
    });
  }

  initChartWhenAvailable(): void {
    const currentChartPanel = this.#getChartPanel();
    if (currentChartPanel && currentChartPanel.getEl()) {
      this.#initChart();
    } else if (currentChartPanel) {
      if (!this.#addedListener) {
        this.mon(currentChartPanel, "afterrender", bind(this, this.#initChart));
        this.#addedListener = true;
      }
    }
  }

  /**
   * Init the Morris LineChart.
   */
  #initChart(): void {
    if (this.#lineChartData) {
      // unfortunately, we need to reset this container on every resize and re-init the chart
      // but at least the data is not fetched again from the server (is accessible via getData())
      Ext.get(this.#getElementId()).dom.innerHTML = "";

      // a list of possible config options: http://www.oesmith.co.uk/morris.js/lines.html
      const lineChartConfig: Record<string, any> = {
        element: this.#getElementId(), // the ID of the element that this chart should be insert to
        xkey: this.#CHART_X_AXIS_PROPERTY_NAME, // the name of the x-axis key
        ykeys: this.#CHART_VALUE_NAMES, // the names of the value properties
        labels: [this.chartLabelName ? this.chartLabelName : this.#CHART_LABEL_NAMES], // the labels that map the value properties
        parseTime: false, // the chart shouldn't automatically convert the values as time object
        smooth: false, // no smooth edges at the line,
        ymax: EsChartBase.#getYMax(this.getLineChartData()),
        data: this.localizeChartData(this.getLineChartData()),
        lineColors: [this.color ? this.color : this.#DEFAULT_COLOR],
      };

      this.#setLineChart(new Morris.Line(lineChartConfig));
      this.#getSwitchContainer().updateLayout();
    }
  }

  /**
   * @return the Element ID of the panel that the chart should be rendered to
   */
  #getElementId(): string {
    return this.#getChartPanel().getEl().first().first().getAttribute("id");
  }

  #getChartPanel(): Panel {
    if (!this.#chartPanel) {
      this.#chartPanel = as(this.queryById(EsChart.CHART_PANEL_ITEM_ID), Panel);
    }
    return this.#chartPanel;
  }

  #getSwitchContainer(): SwitchingContainer {
    if (!this.#switchContainer) {
      this.#switchContainer = as(this.queryById(EsChart.ES_CHART_SWITCHER_ITEM_ID), SwitchingContainer);
    }
    return this.#switchContainer;
  }

  #setLineChart(lineChart: any): void {
    this.#lineChart = lineChart;
  }

  getLineChart(): any {
    return this.#lineChart;
  }

  setLineChartData(lineChartData: Array<any>): void {
    this.#lineChartData = lineChartData;
    this.initChartWhenAvailable();
  }

  getLineChartData(): Array<any> {
    return this.#lineChartData ? this.#lineChartData.slice(0, this.#lineChartData.length) : [];
  }

  static #getYMax(data: Array<any>): number {
    let maxYValue = EsChartBase.#MIN_Y_VALUE;
    for (var i = 0; i < data.length; i++) {
      if (data[i].value > maxYValue) {
        maxYValue = data[i].value;
      }
    }

    let limit = EsChartBase.#MIN_Y_VALUE;
    while (maxYValue / limit > 1) {
      limit = limit * 10;
    }

    for (i = 1; i < 4; i++) {
      const refinedLimit: number = i * limit / 4;
      if (maxYValue <= refinedLimit) {
        return refinedLimit;
      }
    }
    return limit;
  }

  protected static transformTime(timeStamp: Date): string {
    if (timeStamp) {
      return Format.dateRenderer(EsAnalyticsStudioPlugin_properties.dateFormat)(timeStamp);
    } else {
      return EsAnalyticsStudioPlugin_properties.chart_time_stamp_unavailable;
    }
  }

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return this.getFieldLabel();
  }
}

interface EsChartBase extends HidableMixin{}

mixin(EsChartBase, HidableMixin);

export default EsChartBase;
