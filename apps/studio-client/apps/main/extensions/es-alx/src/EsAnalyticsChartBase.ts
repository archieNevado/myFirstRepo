import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import EsAnalyticsChart from "./EsAnalyticsChart";
import EsChart from "./EsChart";

interface EsAnalyticsChartBaseConfig extends Config<Panel>, Config<HidableMixin>, Partial<Pick<EsAnalyticsChartBase,
  "bindTo" |
  "timeRangeValueExpression" |
  "hideText"
>> {
}

class EsAnalyticsChartBase extends Panel {
  declare Config: EsAnalyticsChartBaseConfig;

  bindTo: ValueExpression = null;

  timeRangeValueExpression: ValueExpression = null;

  #chartPanel: EsChart = null;

  constructor(config: Config<EsAnalyticsChartBase> = null) {
    super(config);
    this.mon(this.#getChartPanel(), "resize", (): void => this.#getChartPanel().initChartWhenAvailable());
  }

  #getChartPanel(): EsChart {
    if (!this.#chartPanel) {
      this.#chartPanel = as(this.queryById(EsAnalyticsChart.ES_CHART_ITEM_ID), EsChart);
    }
    return this.#chartPanel;
  }

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return as(this.getTitle(), String);
  }

}

interface EsAnalyticsChartBase extends HidableMixin{}

mixin(EsAnalyticsChartBase, HidableMixin);

export default EsAnalyticsChartBase;
