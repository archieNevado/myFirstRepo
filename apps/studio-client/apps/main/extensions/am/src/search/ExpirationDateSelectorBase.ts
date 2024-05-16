import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import StatefulDateField from "@coremedia/studio-client.ext.ui-components/components/StatefulDateField";
import Container from "@jangaroo/ext-ts/container/Container";
import { bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";

interface ExpirationDateSelectorBaseConfig extends Config<Container>, Partial<Pick<ExpirationDateSelectorBase,
  "dateKey" |
  "selectedKeyValueExpression" |
  "selectedDateValueExpression"
>> {
}

class ExpirationDateSelectorBase extends Container {
  declare Config: ExpirationDateSelectorBaseConfig;

  constructor(config: Config<ExpirationDateSelectorBase> = null) {
    const defaults = Config(ExpirationDateSelectorBase);
    defaults.dateKey = "byDate";
    super(ConfigUtils.apply(defaults, config));

    const dateField = cast(StatefulDateField, this.down("datefield"));
    dateField.on("select", ExpirationDateSelectorBase.#doChange);
    dateField.on("afterrender", ExpirationDateSelectorBase.#datefieldRendered);

    this.selectedKeyValueExpression.addChangeListener(bind(this, this.#onSelectedKeyChange));
  }

  /**
   * The key that triggers the datefield visibility.
   */
  dateKey: string = null;

  /**
   * A value expression that will be bound to the selected combo box entry.
   */
  selectedKeyValueExpression: ValueExpression = null;

  /**
   * A value expression that will be bound to the selected date or null if no datefield is displayed.
   */
  selectedDateValueExpression: ValueExpression = null;

  #onSelectedKeyChange(source: ValueExpression): void {
    if (source.getValue() === this.dateKey) {
      if (this.selectedDateValueExpression.getValue() === null) {
        this.selectedDateValueExpression.setValue(new Date());
      }
    } else {
      this.selectedDateValueExpression.setValue(null);
    }
  }

  static #doChange(dateField: StatefulDateField): void {
    const value = dateField.getValue();
    dateField.fireEvent("change", dateField, value, dateField.originalValue);
    dateField.originalValue = value;
  }

  static #datefieldRendered(dateField: StatefulDateField): void {
    dateField.getEl().on("blur", (): void =>
      ExpirationDateSelectorBase.#doChange(dateField),
    );
  }

  comboboxEntryTransformer(keys: Array<any>): Array<any> {
    return keys.map((key: string): any =>
      ({
        id: key,
        name: AMStudioPlugin_properties["Filter_ExpirationDate_" + key + "_text"] || key,
      }),
    );
  }

  datefieldVisibilityTransformer(code: string): boolean {
    return code === this.dateKey;
  }

  protected override onDestroy(): void {
    this.selectedKeyValueExpression.removeChangeListener(bind(this, this.#onSelectedKeyChange));
    super.onDestroy();
  }
}

export default ExpirationDateSelectorBase;
