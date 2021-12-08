import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import InputChipsFieldBase from "@coremedia/studio-client.main.editor-components/sdk/components/ChipsField/InputChipsFieldBase";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import CategoryFacetTagField from "./CategoryFacetTagField";
import CategoryFacetsPropertyFieldBase from "./CategoryFacetsPropertyFieldBase";

interface CategoryFacetTagFieldBaseConfig extends Config<InputChipsFieldBase>, Partial<Pick<CategoryFacetTagFieldBase,
  "facet" |
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "structPropertyName"
>> {
}

class CategoryFacetTagFieldBase extends InputChipsFieldBase {
  declare Config: CategoryFacetTagFieldBaseConfig;

  static readonly LABEL: string = "label";

  static readonly QUERY: string = "query";

  facet: Facet = null;

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  structPropertyName: string = null;

  #facetTagsExpression: ValueExpression = null;

  #multiFacetsExpression: ValueExpression = null;

  constructor(config: Config<CategoryFacetTagField> = null) {
    super(config);

    this.#multiFacetsExpression = this.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME).extendBy([this.facet.getKey()]);
    this.#multiFacetsExpression.addChangeListener(bind(this, this.#multiFacetsStructChanged));

    //This would also work with a BindPlugin, but we don't want to write values when they are not valid anymore
    //This ensures that we validate the persisted data before actually loading it into the editor
    this.#facetTagsExpression = this.#multiFacetsExpression.extendBy(CategoryFacetsPropertyFieldBase.MULTI_FACETS_QUERIES_STRUCT_NAME);

    this.#setTagValues(this.#facetTagsExpression);
    this.on("change", bind(this, this.#onInputChange));

    this.#facetTagsExpression.addChangeListener(bind(this, this.#setTagValues));
  }

  #onInputChange(): void {
    const values: Array<any> = this.getValue() || [];
    this.#facetTagsExpression.setValue(values);
  }

  /**
   * When the data structure is reverted to another version or reverted, the given data may not exist anymore.
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

  #setTagValues(ve: ValueExpression): void {
    const values: Array<any> = ve.getValue();
    if (this.#allValuesAreValid(values)) {
      this.setValue(values);
    }
  }

  #allValuesAreValid(queryValues: Array<any>): boolean {
    if (!queryValues) {
      return false;
    }

    for (const q of queryValues as string[]) {
      if (!this.#isValidQuery(q)) {
        return false;
      }
    }
    return true;
  }

  #isValidQuery(q: string): boolean {
    for (const value of this.facet.getValues()) {
      if (value.query === q) {
        return true;
      }
    }
    return false;
  }

  protected override onDestroy(): void {
    super.onDestroy();
    this.#multiFacetsExpression && this.#multiFacetsExpression.addChangeListener(bind(this, this.#multiFacetsStructChanged));
    this.#facetTagsExpression && this.#facetTagsExpression.removeChangeListener(bind(this, this.#setTagValues));
  }
}

export default CategoryFacetTagFieldBase;
