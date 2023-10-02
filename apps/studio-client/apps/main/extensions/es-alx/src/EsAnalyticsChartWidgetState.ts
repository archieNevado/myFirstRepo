import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import WidgetState from "@coremedia/studio-client.main.editor-components/sdk/dashboard/WidgetState";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EsAnalyticsChartWidget from "./EsAnalyticsChartWidget";

interface EsAnalyticsChartWidgetStateConfig extends Config<WidgetState>, Partial<Pick<EsAnalyticsChartWidgetState,
  "content" |
  "name"
>> {
}

class EsAnalyticsChartWidgetState extends WidgetState {
  declare Config: EsAnalyticsChartWidgetStateConfig;

  constructor(config: Config<EsAnalyticsChartWidgetState> = null) {
    super(ConfigUtils.apply(Config(EsAnalyticsChartWidgetState, { widgetTypeId: EsAnalyticsChartWidget.xtype }), config));
  }

  /**
   * The content
   */
  content: Content = null;

  /**
   * The name
   */
  name: string = null;
}

export default EsAnalyticsChartWidgetState;
