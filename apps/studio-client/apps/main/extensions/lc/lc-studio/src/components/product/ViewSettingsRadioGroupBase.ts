import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StatefulRadioGroup from "@coremedia/studio-client.ext.ui-components/components/StatefulRadioGroup";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import ViewSettingsRadioGroup from "./ViewSettingsRadioGroup";

interface ViewSettingsRadioGroupBaseConfig extends Config<StatefulRadioGroup> {
}

class ViewSettingsRadioGroupBase extends StatefulRadioGroup {
  declare Config: ViewSettingsRadioGroupBaseConfig;

  static #radioButtonFormName: int = 0;

  static readonly #LOCAL_SETTINGS_PROPERTY: string = "localSettings";

  static readonly #SHOP_NOW_PROPERTY: string = "shopNow";

  #radioGroupExpression: ValueExpression = null;

  #propertyValueExpression: ValueExpression = null;

  #defaultSetting: string = null;

  #rootChannel: boolean = false;

  #bindTo: ValueExpression = null;

  constructor(config: Config<ViewSettingsRadioGroup> = null) {
    super(config);
    this.#bindTo = config.bindTo;
    this.#defaultSetting = ViewSettingsRadioGroup.INHERITED_SETTING;
    this.#propertyValueExpression = config.bindTo.extendBy("properties.localSettings." + ViewSettingsRadioGroupBase.#SHOP_NOW_PROPERTY);

    this.#isNotRootChannelExpression(config.bindTo).loadValue((isNotRootChannel: boolean): void => {
      this.#rootChannel = !isNotRootChannel;
      if (this.#rootChannel) {
        this.#defaultSetting = ViewSettingsRadioGroup.ENABLED_SETTING;
      }

      ValueExpressionFactory.createFromFunction((): string => {
        const content: Content = config.bindTo.getValue();
        const localSettings: Struct = content.getProperties().get(ViewSettingsRadioGroupBase.#LOCAL_SETTINGS_PROPERTY);
        if (!localSettings) {
          return this.#defaultSetting;
        }

        const shopNowProperty: string = localSettings.get(ViewSettingsRadioGroupBase.#SHOP_NOW_PROPERTY);
        if (!shopNowProperty) {
          return this.#defaultSetting;
        }

        return shopNowProperty;
      }).loadValue((setting: string): void =>
        this.#applyListeners(setting),
      );
    });
  }

  /**
   * Since we have to load a lot of stuff initially via FunctionValueExpression, we apply
   * the listeners after we have set the defaults.
   */
  #applyListeners(defaultValue: string): void {
    this.getRadioGroupValueExpression().addChangeListener(bind(this, this.#radioGroupChanged));
    this.getRadioGroupValueExpression().setValue(defaultValue);
    this.#propertyValueExpression.addChangeListener(bind(this, this.#propertyValueChanged));
  }

  /**
   * ExtJS is so stupid and handles all "name" attributes of radio buttons as global ids.
   * Therefore we have to generated the name attribute value for each premular the component is used on.
   * @return A unique name attribute value used for the radio boxes.
   */
  static getNameId(): string {
    ViewSettingsRadioGroupBase.#radioButtonFormName++;
    return "radioButtonFormName_" + ViewSettingsRadioGroupBase.#radioButtonFormName;
  }

  /**
   * Returns the ValueExpression that calculates if the "inherit" option should be visible.
   */
  protected getInheritOptionVisibleExpression(bindTo: ValueExpression): ValueExpression {
    return this.#isNotRootChannelExpression(bindTo);
  }

  protected getRadioGroupValueExpression(): ValueExpression {
    if (!this.#radioGroupExpression) {
      this.#radioGroupExpression = ValueExpressionFactory.createFromValue(this.#defaultSetting);
    }

    return this.#radioGroupExpression;
  }

  #radioGroupChanged(ve: ValueExpression): void {
    const value: string = ve.getValue();

    if (value == this.#defaultSetting) {
      const content: Content = this.#bindTo.getValue();
      const localSettings: Struct = content.getProperties().get(ViewSettingsRadioGroupBase.#LOCAL_SETTINGS_PROPERTY);
      if (localSettings) {
        localSettings.getType().removeProperty(ViewSettingsRadioGroupBase.#SHOP_NOW_PROPERTY);
      }
    } else {
      this.#propertyValueExpression.setValue(value);
    }
  }

  /**
   * Evaluates if the current channel is a root channel.
   * The first radio box of this component is not visible in this case and the default is "enabled".
   *
   * @param bindTo the ValueExpression that contains the content
   */
  #isNotRootChannelExpression(bindTo: ValueExpression): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean => {
      const content: Content = bindTo.getValue();
      const referrersWithNamedDescriptor = content.getReferrersWithNamedDescriptor("CMSite", "root");
      if (referrersWithNamedDescriptor === undefined) {
        return undefined;
      }

      return referrersWithNamedDescriptor.length == 0;
    });
  }

  #propertyValueChanged(ve: ValueExpression): void {
    const value: string = ve.getValue();
    if (value) {
      this.#radioGroupExpression.setValue(value);
    } else {
      this.#radioGroupExpression.setValue(this.#defaultSetting);
    }
  }

  override onRemoved(destroying: boolean): void {
    this.getRadioGroupValueExpression().removeChangeListener(bind(this, this.#radioGroupChanged));
    this.#propertyValueExpression.removeChangeListener(bind(this, this.#propertyValueChanged));

    super.onRemoved(destroying);
  }
}

export default ViewSettingsRadioGroupBase;
