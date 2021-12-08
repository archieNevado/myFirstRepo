import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import SetPropertyLabelPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyLabelPlugin";
import Radio from "@jangaroo/ext-ts/form/field/Radio";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../../LivecontextStudioPlugin_properties";
import ViewSettingsRadioGroupBase from "./ViewSettingsRadioGroupBase";

interface ViewSettingsRadioGroupConfig extends Config<ViewSettingsRadioGroupBase>, Partial<Pick<ViewSettingsRadioGroup,
  "radioButtonFormName" |
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "inheritLabel" |
  "propertyName"
>> {
}

class ViewSettingsRadioGroup extends ViewSettingsRadioGroupBase {
  declare Config: ViewSettingsRadioGroupConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.viewSettingsRadioGroup";

  static readonly INHERITED_SETTING: string = "inherited";

  static readonly ENABLED_SETTING: string = "enabled";

  static readonly DISABLED_SETTING: string = "disabled";

  radioButtonFormName: string = null;

  // called by generated constructor code
  #__initialize__(config: Config<ViewSettingsRadioGroup>): void {
    this.radioButtonFormName = ViewSettingsRadioGroupBase.getNameId();
  }

  constructor(config: Config<ViewSettingsRadioGroup> = null) {
    super((()=>{
      const this$ = this;this.#__initialize__(config);
      config = ConfigUtils.apply({ propertyName: "localSettings.shopNow" }, config);
      return ConfigUtils.apply(Config(ViewSettingsRadioGroup, {
        columns: 1,
        itemId: "viewSettingsPropertyField",

        plugins: [
          Config(SetPropertyLabelPlugin, {
            bindTo: config.bindTo,
            propertyName: config.propertyName,
          }),
          Config(BindPropertyPlugin, {
            componentProperty: "value",
            bindTo: this.getRadioGroupValueExpression(),
            transformer: (state: string): any => {
              const radioValueObject: Record<string, any> = {};
              radioValueObject[this.radioButtonFormName] = state;
              return radioValueObject;
            },
            reverseTransformer: (radio: Radio): string =>
              (radio ? radio[this.radioButtonFormName] : ""),
            bidirectional: true,
          }),
        ],
        items: [
          Config(Radio, {
            inputValue: ViewSettingsRadioGroup.INHERITED_SETTING,
            itemId: ViewSettingsRadioGroup.INHERITED_SETTING,
            boxLabel: ConfigUtils.asString(config.inheritLabel || LivecontextStudioPlugin_properties.CMProductTeaser_settings_inherit),
            plugins: [
              Config(BindDisablePlugin, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              }),
              Config(BindVisibilityPlugin, { bindTo: this.getInheritOptionVisibleExpression(config.bindTo) }),
            ],
          }),
          Config(Radio, {
            inputValue: ViewSettingsRadioGroup.ENABLED_SETTING,
            itemId: ViewSettingsRadioGroup.ENABLED_SETTING,

            boxLabel: LivecontextStudioPlugin_properties.CMProductTeaser_settings_enabled,
          }),
          Config(Radio, {
            inputValue: ViewSettingsRadioGroup.DISABLED_SETTING,
            itemId: ViewSettingsRadioGroup.DISABLED_SETTING,
            boxLabel: LivecontextStudioPlugin_properties.CMProductTeaser_settings_disabled,
          }),
        ],
        defaultType: Radio["xtype"],
        defaults: Config<Radio>({
          name: this.radioButtonFormName,
          hideLabel: true,
          ...{ inGroup: true },
          plugins: [
            Config(BindDisablePlugin, {
              bindTo: config.bindTo,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
          ],
        }),

      }), config);
    })());
  }

  /**
   * A property path expression leading to the Bean whose property is edited.
   * This property editor assumes that this bean has a property 'properties'.
   */
  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  /*label of the first radio button.
    Default is resourceManager.getString('com.coremedia.livecontext.studio.LivecontextStudioPlugin', 'CMProductTeaser_settings_inherit')  */
  inheritLabel: string = null;

  /**
   * The name of the sting property of the Bean to bind in this field.
   * The string property holds the id of the catalog product
   */
  propertyName: string = null;
}

export default ViewSettingsRadioGroup;
