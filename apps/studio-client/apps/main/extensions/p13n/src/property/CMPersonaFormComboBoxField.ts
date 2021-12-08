import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import PersonaContextHelper from "@coremedia/studio-client.main.cap-personalization-ui/util/PersonaContextHelper";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CMPersonaFormComboBox from "./CMPersonaFormComboBox";

interface CMPersonaFormComboBoxFieldConfig extends Config<CMPersonaFormComboBox>, Partial<Pick<CMPersonaFormComboBoxField,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "propertyContext" |
  "propertyName" |
  "values"
>> {
}

class CMPersonaFormComboBoxField extends CMPersonaFormComboBox {
  declare Config: CMPersonaFormComboBoxFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.cmPersonaFormComboBoxField";

  constructor(config: Config<CMPersonaFormComboBoxField> = null) {
    super((()=> ConfigUtils.apply(Config(CMPersonaFormComboBoxField, {
      properties: config.values,
      typeAhead: true,
      queryMode: "local",
      triggerAction: "all",

      plugins: [
        Config(BindPropertyPlugin, {
          bindTo: config.bindTo.extendBy(config.propertyContext + PersonaContextHelper.CONTEXT_NAME_SEPARATOR + config.propertyName),
          bidirectional: true,
        }),
        Config(BindDisablePlugin, {
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          bindTo: this.bindTo,
        }),
      ],

    }), config))());
  }

  /**
   * property path expression leading to the Content to use for the property field
   */
  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  /** the context of the Bean-property to bind in this field */
  propertyContext: string = null;

  /** the property of the Bean to bind in this field */
  propertyName: string = null;

  /** the values of the Bean to bind in this field */
  values: any = null;
}

export default CMPersonaFormComboBoxField;
