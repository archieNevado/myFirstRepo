import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import StringPropertyField
  from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ComponentUtil from "@coremedia/studio-client.ext.ui-components/util/ComponentUtil";

interface LocalizedStringPropertyFieldConfig extends Config<FieldContainer>, Partial<Pick<LocalizedStringPropertyField,
  "bindTo" |
  "lang">> {
}

class LocalizedStringPropertyField extends FieldContainer {
  declare Config: LocalizedStringPropertyFieldConfig;

  constructor(config: Config<LocalizedStringPropertyField> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#__initialize__(config);
    super(ConfigUtils.apply(Config(LocalizedStringPropertyField, {
      margin: "0 0 10 0",
      itemId: ComponentUtil.formatItemId(config.lang),
      cls: "localized-string-propertyfield",
      items: [
        Config(StringPropertyField, {
          labelSeparator: "",
          bindTo: config.bindTo,
          propertyName: this$.#propertyName,
          flex: 1,
        }),
      ],
      layout: Config(HBoxLayout, { align: "stretch" }),
    }), config));
  }

  bindTo: ConfigBasedValueExpression = null;

  #propertyName: string = null;

  #__initialize__(config: Config<LocalizedStringPropertyField>): void {
    this.#propertyName = "localSettings.translations." + config.lang;
  }

  #basePropertyName: string = null;

  get basePropertyName(): string {
    return this.#basePropertyName;
  }

  set basePropertyName(value: string) {
    this.#basePropertyName = value;
  }

  #lang: string = null;

  get lang(): string {
    return this.#lang;
  }

  set lang(value: string) {
    this.#lang = value;
  }
}

export default LocalizedStringPropertyField;
