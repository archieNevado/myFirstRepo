package com.coremedia.blueprint.studio.esanalytics {
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.mixins.IHidableMixin;

import ext.panel.Panel;

public class EsAnalyticsChartBase extends Panel implements IHidableMixin {

  [Bindable]
  public var bindTo:ValueExpression;

  [Bindable]
  public var timeRangeValueExpression:ValueExpression;

  private var chartPanel:EsChart;

  public function EsAnalyticsChartBase(config:EsAnalyticsChartBase = null) {
    super(config);
    mon(getChartPanel(), "resize", function():void {getChartPanel().initChartWhenAvailable()});
  }

  private function getChartPanel():EsChart {
    if (!chartPanel) {
      chartPanel = queryById(EsAnalyticsChart.ES_CHART_ITEM_ID) as EsChart
    }
    return chartPanel;
  }

  /** @private */
  [Bindable]
  public function set hideText(newHideText:String):void {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  [Bindable]
  public function get hideText():String {
    return getTitle();
  }

  /** @private */
  [Bindable]
  public native function set hideId(newHideId:String):void;

  /** @inheritDoc */
  [Bindable]
  public native function get hideId():String;

}
}
