import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import SetPropertyEmptyTextPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyEmptyTextPlugin";
import SetPropertyLabelPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyLabelPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AnalyticsProviderBase from "./AnalyticsProviderBase";

interface AnalyticsProviderComboBoxConfig extends Config<LocalComboBox>, Partial<Pick<AnalyticsProviderComboBox,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "propertyName"
>> {
}

/**
 * A ComboBox to choose between one of the configured analytics service providers.
 *
 * It fetches the available choices from the applicationContext (see
 * AnalyticsPlugin) and retrieves/stores the current selected value
 * from the Content's 'settings' property.
 */
class AnalyticsProviderComboBox extends LocalComboBox {
  declare Config: AnalyticsProviderComboBoxConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.analyticsProviderComboBox";

  constructor(config: Config<AnalyticsProviderComboBox> = null) {
    super(ConfigUtils.apply(Config(AnalyticsProviderComboBox, {
      itemId: "alxComboBox",
      editable: false,
      encodeItems: true,
      store: AnalyticsProviderBase.ANALYTICS_PROVIDERS,

      plugins: [
        Config(SetPropertyLabelPlugin, {
          bindTo: config.bindTo,
          propertyName: config.propertyName,
        }),
        Config(SetPropertyEmptyTextPlugin, {
          bindTo: config.bindTo,
          propertyName: config.propertyName,
        }),
        Config(BindDisablePlugin, {
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
        }),
        Config(BindPropertyPlugin, {
          bindTo: config.bindTo.extendBy("properties", config.propertyName),
          bidirectional: true,
          componentEvent: "select",
        }),
      ],

    }), config));
  }

  bindTo: ValueExpression = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  propertyName: string = null;
}

export default AnalyticsProviderComboBox;
