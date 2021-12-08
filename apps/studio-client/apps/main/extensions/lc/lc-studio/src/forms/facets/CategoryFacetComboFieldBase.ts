import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CategoryFacetComboField from "./CategoryFacetComboField";
import CategoryFacetsPropertyFieldBase from "./CategoryFacetsPropertyFieldBase";

interface CategoryFacetComboFieldBaseConfig extends Config<LocalComboBox>, Partial<Pick<CategoryFacetComboFieldBase,
  "facet" |
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "structPropertyName"
>> {
}

class CategoryFacetComboFieldBase extends LocalComboBox {
  declare Config: CategoryFacetComboFieldBaseConfig;

  static readonly LABEL: string = "label";

  static readonly QUERY: string = "query";

  facet: Facet = null;

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  structPropertyName: string = null;

  #facetTagsExpression: ValueExpression = null;

  #multiFacetsExpression: ValueExpression = null;

  #comboValuesExpression: ValueExpression = null;

  constructor(config: Config<CategoryFacetComboField> = null) {
    super(config);

    this.#multiFacetsExpression = this.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME).extendBy([this.facet.getKey()]);
    this.#multiFacetsExpression.addChangeListener(bind(this, this.#multiFacetsStructChanged));

    //This would also work with a BindPlugin, but we don't want to write values when they are not valid anymore
    //This ensures that we validate the persisted data before actually loading it into the editor
    this.#facetTagsExpression = this.#multiFacetsExpression.extendBy(CategoryFacetsPropertyFieldBase.MULTI_FACETS_QUERIES_STRUCT_NAME);

    this.#setComboValue(this.#facetTagsExpression);
    this.on("change", bind(this, this.#onInputChange));

    this.#facetTagsExpression.addChangeListener(bind(this, this.#setComboValue));
  }

  /**
   * The value is a single string.
   * We convert it to an array to store a StringList anyway, same format as for multi facets.
   */
  #onInputChange(): void {
    const value: string = this.getValue() || "";
    if (value === "") {
      this.#facetTagsExpression.setValue([]);
    } else {
      this.#facetTagsExpression.setValue([value]);
    }
  }

  /**
   * When the data structure is reverted, the given data may not exist anymore.
   * The editor remains without being re-rendered, so we have to reset it manually.
   * @param ve the facet struct of the editor
   */
  #multiFacetsStructChanged(ve: ValueExpression): void {
    const value = ve.getValue();
    if (!value && this.rendered) {
      this.un("change", bind(this, this.#onInputChange));
      this.clearValue();
      this.on("change", bind(this, this.#onInputChange));
    }
  }

  #setComboValue(ve: ValueExpression): void {
    const values: Array<any> = ve.getValue() || [];
    if (values.length > 0) {
      const value: string = values[0];
      if (this.#isValidQuery(value)) {
        this.setValue(value);
      }
    } else {
      this.clearValue();
    }
  }

  #isValidQuery(q: string): boolean {
    for (const value of this.facet.getValues()) {
      if (value.query === q) {
        return true;
      }
    }
    return false;
  }

  protected getComboValuesExpression(config: Config<CategoryFacetComboField>): ValueExpression {
    if (!this.#comboValuesExpression) {
      this.#comboValuesExpression = ValueExpressionFactory.createFromFunction((): Array<any> => {
        const result = [].concat(config.facet.getValues());
        result.push({
          "query": "",
          "label": "---",
        });
        return result;
      });
      return this.#comboValuesExpression;
    }
  }

  protected override onDestroy(): void {
    super.onDestroy();
    this.#multiFacetsExpression && this.#multiFacetsExpression.addChangeListener(bind(this, this.#multiFacetsStructChanged));
    this.#facetTagsExpression && this.#facetTagsExpression.removeChangeListener(bind(this, this.#setComboValue));
  }
}

export default CategoryFacetComboFieldBase;
