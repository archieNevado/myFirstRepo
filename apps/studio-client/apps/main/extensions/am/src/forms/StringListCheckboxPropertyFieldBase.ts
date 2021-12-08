import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import AdvancedFieldContainer from "@coremedia/studio-client.ext.ui-components/components/AdvancedFieldContainer";
import StatefulCheckbox from "@coremedia/studio-client.ext.ui-components/components/StatefulCheckbox";
import ValidationStateMixin from "@coremedia/studio-client.ext.ui-components/mixins/ValidationStateMixin";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import Events from "@jangaroo/ext-ts/Events";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import { as, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import StringListCheckboxPropertyField from "./StringListCheckboxPropertyField";

interface StringListCheckboxPropertyFieldBaseConfig extends Config<AdvancedFieldContainer>, Config<ValidationStateMixin>, Partial<Pick<StringListCheckboxPropertyFieldBase,
  "structName" |
  "propertyName" |
  "availableValuesValueExpression" |
  "bindTo" |
  "hideIssues"
>> {
  listeners?: Events<AdvancedFieldContainer> & Events<ValidationStateMixin>;
}

class StringListCheckboxPropertyFieldBase extends AdvancedFieldContainer {
  declare Config: StringListCheckboxPropertyFieldBaseConfig;

  #propertyValueExpression: ValueExpression = null;

  #structValueExpression: ValueExpression = null;

  constructor(config: Config<StringListCheckboxPropertyField> = null) {
    super(config);
    this.initValidationStateMixin();
  }

  /**
   * The name of the struct property.
   */
  structName: string = null;

  /**
   * The name of the property inside the struct.
   */
  propertyName: string = null;

  /**
   * A value expression evaluating to a list of strings. For each string one checkbox is rendered.
   * The checkbox label is localized in the file AMStudioPlugin.properties with the following pattern:
   * 'Asset_[structName]_[propertyName]_[value]_text'.
   */
  availableValuesValueExpression: ValueExpression = null;

  bindTo: ValueExpression = null;

  hideIssues: boolean = false;

  #getPropertyValueExpression(): ValueExpression {
    if (!this.#propertyValueExpression) {
      this.#propertyValueExpression = this.bindTo.extendBy("properties", this.structName, this.propertyName);
    }
    return this.#propertyValueExpression;
  }

  #getStructValueExpression(): ValueExpression {
    if (!this.#structValueExpression) {
      this.#structValueExpression = this.bindTo.extendBy("properties", this.structName);
    }
    return this.#structValueExpression;
  }

  computeCheckboxConfigs(): any {
    let availableValues: Array<any> = this.availableValuesValueExpression.getValue();
    const struct = as(this.#getStructValueExpression().getValue(), RemoteBean);

    // make sure everything is loaded
    if (availableValues === undefined || struct === undefined) {
      return undefined;
    }
    if (struct && !struct.isLoaded()) {
      struct.load();
      return undefined;
    }

    // extend available values with values stored in the struct (if any)
    const valuesStoredInContent: Array<any> = this.#getPropertyValueExpression().getValue();
    if (valuesStoredInContent) {
      const valuesOnlyStoredInContent = valuesStoredInContent.filter((value: string): boolean =>
        availableValues.indexOf(value) === -1,
      );
      availableValues = availableValues.concat(valuesOnlyStoredInContent);
    }
    availableValues = availableValues.filter((value: string): boolean =>
      ! !value,
    );

    const checkboxConfigs = availableValues.map((availableValue: string): Config<Checkbox> => {
      const propertyKey = "Asset_" + this.structName + "_" + this.propertyName + "_" + availableValue + "_text";
      const checked: boolean = valuesStoredInContent && valuesStoredInContent.indexOf(availableValue) !== -1;

      const checkboxConfig = Config(StatefulCheckbox);
      checkboxConfig.boxLabel = AMStudioPlugin_properties[propertyKey] || availableValue;
      checkboxConfig.name = this.propertyName;
      checkboxConfig.inputValue = availableValue;
      checkboxConfig.itemId = availableValue;
      checkboxConfig.hideLabel = true;
      checkboxConfig.checked = checked;
      const propertyFieldPluginConfig = Config(PropertyFieldPlugin);
      propertyFieldPluginConfig.propertyName = this.structName + "." + this.propertyName + "." + availableValue;
      const showIssuesPluginConfig = Config(ShowIssuesPlugin);
      showIssuesPluginConfig.bindTo = this.bindTo;
      showIssuesPluginConfig.hideIssues = this.hideIssues;
      showIssuesPluginConfig.propertyName = this.structName + "." + this.propertyName + "." + availableValue;
      checkboxConfig.plugins = [
        propertyFieldPluginConfig,
        showIssuesPluginConfig,
      ];
      return checkboxConfig;
    });

    return checkboxConfigs;
  }

  transformer(strings: Array<any>): any {
    const valueObject: Record<string, any> = {};

    if (strings.length == 1 && strings[0]) {
      valueObject[this.propertyName] = strings[0];
    } else {
      valueObject[this.propertyName] = strings;
    }
    return valueObject;
  }

  reverseTransformer(selectedCheckboxes: any): Array<any> {
    if (selectedCheckboxes[this.propertyName]) {
      return [].concat(selectedCheckboxes[this.propertyName]);
    }
    return [];
  }

}

interface StringListCheckboxPropertyFieldBase extends ValidationStateMixin{}

mixin(StringListCheckboxPropertyFieldBase, ValidationStateMixin);

export default StringListCheckboxPropertyFieldBase;
