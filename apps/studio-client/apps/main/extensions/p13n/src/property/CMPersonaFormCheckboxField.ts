import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import PersonaContextHelper from "@coremedia/studio-client.main.cap-personalization-ui/util/PersonaContextHelper";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import { mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface CMPersonaFormCheckboxFieldConfig extends Config<Checkbox>, Config<HidableMixin>, Partial<Pick<CMPersonaFormCheckboxField,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "hideText" |
  "propertyContext" |
  "propertyName"
>> {
}

class CMPersonaFormCheckboxField extends Checkbox {
  declare Config: CMPersonaFormCheckboxFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.cmPersonaFormCheckboxField";

  constructor(config: Config<CMPersonaFormCheckboxField> = null) {
    super((()=> ConfigUtils.apply(Config(CMPersonaFormCheckboxField, {

      plugins: [
        Config(BindPropertyPlugin, {
          bindTo: config.bindTo.extendBy(config.propertyContext + PersonaContextHelper.CONTEXT_NAME_SEPARATOR + config.propertyName),
          bidirectional: true,
        }),
        Config(BindDisablePlugin, {
          bindTo: this.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
        }),
      ],

    }), config))());
  }

  /**
   * property path expression leading to the Content to use for the property field
   */
  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return this.getFieldLabel();
  }

  /** the context of the Bean-property to bind in this field */
  propertyContext: string = null;

  /** the property of the Bean to bind in this field */
  propertyName: string = null;
}

interface CMPersonaFormCheckboxField extends HidableMixin{}

mixin(CMPersonaFormCheckboxField, HidableMixin);

export default CMPersonaFormCheckboxField;
