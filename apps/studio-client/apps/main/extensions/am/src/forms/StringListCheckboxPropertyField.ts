import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StatefulCheckboxGroup from "@coremedia/studio-client.ext.ui-components/components/StatefulCheckboxGroup";
import BindItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindItemsPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import SetPropertyLabelPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyLabelPlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import StringListCheckboxPropertyFieldBase from "./StringListCheckboxPropertyFieldBase";

interface StringListCheckboxPropertyFieldConfig extends Config<StringListCheckboxPropertyFieldBase>, Partial<Pick<StringListCheckboxPropertyField,
  "forceReadOnlyValueExpression"
>> {
}

/**
 * A property field rendering checkboxes for a list of strings binding to a StringListProperty inside a Struct.
 */
class StringListCheckboxPropertyField extends StringListCheckboxPropertyFieldBase {
  declare Config: StringListCheckboxPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.stringListCheckboxPropertyField";

  static readonly CHECKBOX_GROUP_ITEM_ID: string = "checkboxGroup";

  constructor(config: Config<StringListCheckboxPropertyField> = null) {
    super((()=> ConfigUtils.apply(Config(StringListCheckboxPropertyField, {
      labelSeparator: "",
      labelAlign: "top",
      defaultField: createComponentSelector().itemId(StringListCheckboxPropertyField.CHECKBOX_GROUP_ITEM_ID).build(),

      plugins: [
        Config(PropertyFieldPlugin, { propertyName: config.structName + "." + config.propertyName }),
        Config(SetPropertyLabelPlugin, {
          bindTo: config.bindTo,
          propertyName: config.structName + "." + config.propertyName,
        }),
      ],

      items: [
        Config(StatefulCheckboxGroup, {
          itemId: StringListCheckboxPropertyField.CHECKBOX_GROUP_ITEM_ID,
          layout: Config(VBoxLayout),
          ...ConfigUtils.append({
            plugins: [
              Config(PropertyFieldPlugin, { propertyName: config.propertyName }),
              Config(BindPropertyPlugin, {
                bindTo: config.bindTo.extendBy("properties").extendBy(config.structName).extendBy(config.propertyName),
                bidirectional: true,
                transformer: bind(this, this.transformer),
                reverseTransformer: bind(this, this.reverseTransformer),
              }),
              Config(BindItemsPlugin, { valueExpression: ValueExpressionFactory.createFromFunction(bind(this, this.computeCheckboxConfigs)) }),
              Config(ShowIssuesPlugin, {
                bindTo: config.bindTo,
                propertyName: config.structName + "." + config.propertyName,
              }),
              Config(BindDisablePlugin, {
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                bindTo: config.bindTo,
              }),
            ],
          }),
        }),
      ],
      layout: Config(VBoxLayout),
    }), config))());
  }

  forceReadOnlyValueExpression: ValueExpression = null;
}

export default StringListCheckboxPropertyField;
