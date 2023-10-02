import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import FacetComboField from "./FacetComboField";

interface FacetComboFieldBaseConfig extends Config<LocalComboBox>, Partial<Pick<FacetComboFieldBase,
  "facetValueExpression" |
  "selectedFacetValuesExpression" |
  "comboValuesExpression"
>> {
}

class FacetComboFieldBase extends LocalComboBox {
  declare Config: FacetComboFieldBaseConfig;

  static readonly LABEL: string = "label";

  static readonly QUERY: string = "query";

  facetValueExpression: ValueExpression = null;

  selectedFacetValuesExpression: ValueExpression = null;

  comboValuesExpression: ValueExpression = null;

  constructor(config: Config<FacetComboFieldBase> = null) {
    super(config);
    this.on("change", bind(this, this.#onInputChange));
    this.selectedFacetValuesExpression.addChangeListener(bind(this, this.#selectionChanged));
  }

  protected override afterRender(): void {
    super.afterRender();
    this.#selectionChanged(this.selectedFacetValuesExpression);
  }

  protected getComboValuesExpression(config: Config<FacetComboField>): ValueExpression {
    if (!this.comboValuesExpression) {
      this.comboValuesExpression = ValueExpressionFactory.createFromFunction((): Array<any> => {
        const result = [].concat(config.facetValueExpression.getValue().getValues());
        result.push({
          "query": "",
          "label": "---",
        });
        return result;
      });
      return this.comboValuesExpression;
    }
  }

  #selectionChanged(ve: ValueExpression): void {
    const value = ve.getValue();
    if ((!value || value.length === 0) && this.rendered) {
      this.un("change", bind(this, this.#onInputChange));
      this.clearValue();
      this.on("change", bind(this, this.#onInputChange));
    } else if (value && value.length > 0) {
      this.setValue(value);
    }
  }

  /**
   * The value is a single string.
   * We convert it to an array to store a StringList anyway, same format as for multi facets.
   */
  #onInputChange(): void {
    const value: string = this.getValue() || "";
    if (value === "") {
      this.selectedFacetValuesExpression.setValue([]);
    } else {
      this.selectedFacetValuesExpression.setValue([value]);
    }
  }

  protected override onDestroy(): void {
    super.onDestroy();
    this.selectedFacetValuesExpression && this.selectedFacetValuesExpression.removeChangeListener(bind(this, this.#selectionChanged));
  }
}

export default FacetComboFieldBase;
