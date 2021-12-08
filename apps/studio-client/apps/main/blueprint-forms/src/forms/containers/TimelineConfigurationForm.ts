import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import StatefulNumberField from "@coremedia/studio-client.ext.ui-components/components/StatefulNumberField";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Binding from "@coremedia/studio-client.ext.ui-components/plugins/Binding";
import BlockEnterPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BlockEnterPlugin";
import HorizontalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HorizontalSpacingPlugin";
import Label from "@jangaroo/ext-ts/form/Label";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import TimelineConfigurationFormBase from "./TimelineConfigurationFormBase";
import TimelineSettings from "./TimelineSettings";
import TimelineViewModel from "./TimelineViewModel";

interface TimelineConfigurationFormConfig extends Config<TimelineConfigurationFormBase>, Partial<Pick<TimelineConfigurationForm,
  "propertyName"
>> {
}

/**
 * This is a form panel which combines several form elements to an editor for local settings to configure
 * the fixed index behaviour. A combination of integer field and reset button.
 */
class TimelineConfigurationForm extends TimelineConfigurationFormBase {
  declare Config: TimelineConfigurationFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.timelineConfigurationForm";

  constructor(config: Config<TimelineConfigurationForm> = null) {
    super((()=> ConfigUtils.apply(Config(TimelineConfigurationForm, {
      itemId: "timelineConfigurationForm",
      header: false,
      hideSingleComponentLabel: false,
      propertyNames: [],
      collapsed: config.collapsed || true,
      settingsVE: config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.propertyName),

      ...ConfigUtils.append({
        plugins: [
          Config(Binding, {
            source: "timelineSettings." + TimelineSettings.TIMELINE_PROPERTY_NAME,
            destination: "timelineViewModel." + TimelineViewModel.TIMELINE_PROPERTY_NAME,
            twoWay: true,
          }),
        ],
      }),
      items: [
        Config(Panel, {
          plugins: [
            Config(HorizontalSpacingPlugin, { modifier: SpacingBEMEntities.HORIZONTAL_SPACING_MODIFIER_200 }),
          ],
          items: [
            Config(Label, { text: BlueprintDocumentTypes_properties.CMVideo_sequence_starttime }),
            Config(StatefulNumberField, {
              allowDecimals: false,
              width: 50,
              minValue: 0,
              allowBlank: false,
              itemId: "startTimeNumberField",
              plugins: [
                Config(BlockEnterPlugin),
                Config(BindPropertyPlugin, {
                  bindTo: ValueExpressionFactory.create(TimelineViewModel.TIMELINE_PROPERTY_NAME, this.timelineViewModel),
                  bidirectional: true,
                  reverseTransformer: (v: number): number => v * 1000,
                  transformer: (v: number): number => v / 1000,
                }),
              ],
            }),
            Config(Label, { text: BlueprintDocumentTypes_properties.CMVideo_sequence_units }),
          ],
          layout: Config(HBoxLayout),
        }),
      ],
    }), config))());
  }

  /** the property of the Bean to bind in this field */
  propertyName: string = null;
}

export default TimelineConfigurationForm;
