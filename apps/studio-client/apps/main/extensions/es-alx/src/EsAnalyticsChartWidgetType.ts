import ComponentBasedWidgetType from "@coremedia/studio-client.main.editor-components/sdk/dashboard/ComponentBasedWidgetType";
import Dashboard_properties from "@coremedia/studio-client.main.editor-components/sdk/dashboard/Dashboard_properties";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import EsAnalyticsChartWidget from "./EsAnalyticsChartWidget";
import EsAnalyticsChartWidgetEditor from "./EsAnalyticsChartWidgetEditor";
import EsAnalyticsStudioPlugin_properties from "./EsAnalyticsStudioPlugin_properties";

interface EsAnalyticsChartWidgetTypeConfig extends Config<ComponentBasedWidgetType> {
}

class EsAnalyticsChartWidgetType extends ComponentBasedWidgetType {
  declare Config: EsAnalyticsChartWidgetTypeConfig;

  constructor(config: Config<EsAnalyticsChartWidgetType> = null) {
    super(ConfigUtils.apply(Config<EsAnalyticsChartWidgetType>({
      name: EsAnalyticsStudioPlugin_properties.widget_type,
      description: EsAnalyticsStudioPlugin_properties.widget_description,
      iconCls: Dashboard_properties.Widget_SimpleSearch_icon,

      widgetComponent: Config(EsAnalyticsChartWidget),
      editorComponent: Config(EsAnalyticsChartWidgetEditor),

    }), config));
  }
}

export default EsAnalyticsChartWidgetType;
