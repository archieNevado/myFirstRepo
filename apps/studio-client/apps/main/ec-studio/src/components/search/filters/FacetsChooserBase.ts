import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import ECommerceStudioPlugin_properties from "../../../ECommerceStudioPlugin_properties";
import FacetUtil from "./FacetUtil";
import FacetsChooser from "./FacetsChooser";

interface FacetsChooserBaseConfig extends Config<LocalComboBox>, Partial<Pick<FacetsChooserBase,
  "facetsExpression" |
  "selectedFacetsExpression"
>> {
}

class FacetsChooserBase extends LocalComboBox {
  declare Config: FacetsChooserBaseConfig;

  facetsExpression: ValueExpression = null;

  selectedFacetsExpression: ValueExpression = null;

  #facetListExpression: ValueExpression = null;

  #disabledExpression: ValueExpression = null;

  constructor(config: Config<FacetsChooser> = null) {
    super(config);
    this.on("change", bind(this, this.#onInputChange));
    this.selectedFacetsExpression.addChangeListener(bind(this, this.#selectionChanged));
  }

  #onInputChange(): void {
    const value: string = this.getValue() || null;
    if (value) {
      const facet = FacetUtil.findFacetForKey(this.facetsExpression.getValue(), value);
      const selection: Array<any> = this.selectedFacetsExpression.getValue();
      const updated = [facet].concat(selection);
      this.selectedFacetsExpression.setValue(updated);
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

  protected getEmptyTextExpression(config: Config<FacetsChooser>): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string => {
      const selection: Array<any> = config.selectedFacetsExpression.getValue();
      const facets: Array<any> = config.facetsExpression.getValue();

      if (facets === undefined || selection === undefined) {
        return undefined;
      }

      if (facets.length === 0) {
        return ECommerceStudioPlugin_properties.CollectionView_search_no_filter_allAdded_text;
      }

      if (selection && facets && selection.length === facets.length) {
        return ECommerceStudioPlugin_properties.CollectionView_search_filter_allAdded_text;
      }

      return ECommerceStudioPlugin_properties.CollectionView_search_filter_combo_emptyText;
    });
  }

  protected getDisabledExpression(config: Config<FacetsChooser>): ValueExpression {
    if (!this.#disabledExpression) {
      this.#disabledExpression = ValueExpressionFactory.createFromFunction((): boolean => {
        const facets: Array<any> = config.facetsExpression.getValue();
        return !facets || facets.length === 0;
      });
    }
    return this.#disabledExpression;
  }

  protected getFacetListExpression(config: Config<FacetsChooser>): ValueExpression {
    if (!this.#facetListExpression) {
      this.#facetListExpression = ValueExpressionFactory.createFromFunction((): Array<any> => {
        const categoryFacets: Array<any> = config.facetsExpression.getValue();
        if (categoryFacets === undefined) {
          return undefined;
        }

        const updatedValues = categoryFacets.filter((f: Facet): boolean => {
          const selection: Array<any> = config.selectedFacetsExpression.getValue();
          return FacetUtil.findFacetForKey(selection, f.getKey()) === null;
        });

        updatedValues.sort((f1: Facet, f2: Facet): int => {
          const l1 = FacetUtil.localizeFacetLabel(f1.getLabel());
          const l2 = FacetUtil.localizeFacetLabel(f2.getLabel());
          return l1.localeCompare(l2);
        });

        return updatedValues;
      });
    }
    return this.#facetListExpression;
  }

  protected override onDestroy(): void {
    super.onDestroy();
    this.selectedFacetsExpression && this.selectedFacetsExpression.removeChangeListener(bind(this, this.#selectionChanged));
  }
}

export default FacetsChooserBase;
