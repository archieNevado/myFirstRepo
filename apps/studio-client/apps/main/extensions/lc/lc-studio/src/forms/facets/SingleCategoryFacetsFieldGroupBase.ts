import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import FacetsImpl from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/FacetsImpl";
import SearchFacetsImpl from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/SearchFacetsImpl";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import ComboBox from "@jangaroo/ext-ts/form/field/ComboBox";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, bind, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import SingleCategoryFacetsFieldGroup from "./SingleCategoryFacetsFieldGroup";

interface SingleCategoryFacetsFieldGroupBaseConfig extends Config<Panel>, Partial<Pick<SingleCategoryFacetsFieldGroupBase,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "facetNamePropertyName" |
  "facetValuePropertyName" |
  "facetsExpression"
>> {
}

class SingleCategoryFacetsFieldGroupBase extends Panel {
  declare Config: SingleCategoryFacetsFieldGroupBaseConfig;

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  facetNamePropertyName: string = null;

  facetValuePropertyName: string = null;

  facetsExpression: ValueExpression = null;

  #facetNameExpression: ValueExpression = null;

  #facetValueExpression: ValueExpression = null;

  #facetCombo: ComboBox = null;

  #facetValueCombo: ComboBox = null;

  constructor(config: Config<SingleCategoryFacetsFieldGroup> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#facetNameExpression = config.bindTo.extendBy("properties." + config.facetNamePropertyName);
    this$.#facetValueExpression = config.bindTo.extendBy("properties." + config.facetValuePropertyName);

    super(config);
    this.#facetCombo = as(this.items.get("facetCombo"), ComboBox);
    this.#facetValueCombo = as(this.items.get("facetValueCombo"), ComboBox);

    //when the list of possible facets changed select the facet anew
    this.#facetCombo.getStore().on("datachanged", (): void =>
      this.#facetNameExpression.loadValue((): void =>
        //unfortunately the combobox gets the list of possible values to late.
        //we have to give it more time so that when reverting the changes are correctly selected in the combo.
        EventUtil.invokeLater(bind(this, this.#setFacet)),
      ),
    );

    this.#facetNameExpression.addChangeListener(bind(this, this.#setFacet));

    this.#facetCombo.on("change", (): void => {
      const value: string = this.#facetCombo.getValue() || "";
      const model = this.#facetCombo.findRecord("id", value);
      if (model && value !== this.#facetNameExpression.getValue()) {
        this.#facetNameExpression.setValue(value);
        this.#facetValueExpression.setValue("");
      }
    });

    //when the list of possible facet values changed select the value anew
    this.#facetValueCombo.getStore().on("datachanged", (): void =>
      this.#facetValueExpression.loadValue((): void =>
        //unfortunately the combobox gets the list of possible values to late.
        //we have to give it more time so that when reverting the changes are correctly selected in the combo.
        EventUtil.invokeLater((): void => {
          const value = this.#facetValueExpression.getValue();
          const model = this.#facetValueCombo.findRecord("id", value);
          if (model) {
            this.#facetValueCombo.select(model);
          } else {
            this.#facetValueCombo.clearValue();
          }
        }),
      ),
    );

    this.#facetValueCombo.on("change", (): void => {
      const value: string = this.#facetValueCombo.getValue() || "";
      const model = this.#facetValueCombo.findRecord("id", value);
      if (model) {
        this.#facetValueExpression.setValue(value);
      }
    });

    this.#facetValueExpression.addChangeListener(bind(this, this.#setFacetValue));

    this.on("destroy", (): void => {
      this.#facetNameExpression.removeChangeListener(bind(this, this.#setFacet));
      this.#facetValueExpression.removeChangeListener(bind(this, this.#setFacetValue));
    });
  }

  #setFacet(): void {
    const value = this.#facetNameExpression.getValue();
    const model = this.#facetCombo.findRecord("id", value);
    if (model) {
      this.#facetCombo.select(model);
    } else {
      this.#facetCombo.clearValue();
    }
  }

  #setFacetValue(): void {
    const value = this.#facetValueExpression.getValue();
    const model = this.#facetValueCombo.findRecord("id", value);
    if (model) {
      this.#facetValueCombo.select(model);
    }
  }

  protected getFacetNamesExpression(config: Config<SingleCategoryFacetsFieldGroup>): ValueExpression {
    return ValueExpressionFactory.createFromFunction(
      (): any => {
        const facets = config.facetsExpression.getValue();
        if (!facets) {
          return facets;
        }

        const valuesToChoose = [];
        if (is(facets, FacetsImpl)) {
          const facetsFacets: any = facets.getFacets();
          if (!facetsFacets) {
            return facetsFacets;
          }

          for (const key in facetsFacets) {
            valuesToChoose.push({
              id: key,
              value: key,
            });
          }
        } else if (is(facets, SearchFacetsImpl)) {
          const facetList: Array<any> = facets.getFacets();
          for (const f of facetList as Facet[]) {
            valuesToChoose.push({
              id: f.getLabel(),
              value: f.getLabel(),
            });
          }
        }
        return valuesToChoose;
      },
    );
  }

  protected getFacetValuesExpression(config: Config<SingleCategoryFacetsFieldGroup>): ValueExpression {
    return ValueExpressionFactory.createFromFunction(
      (): any => {
        const facets = config.facetsExpression.getValue();
        if (!facets) {
          return facets;
        }
        const possibleFacetSelector = [];
        const selectedFacet: string = this.#facetNameExpression.getValue();
        if (!selectedFacet) {
          return selectedFacet;
        }

        if (is(facets, FacetsImpl)) {
          const facets2: any = facets.getFacets();
          if (facets2 === null) {
            // facets2 loaded but empty.
            return [];
          }
          const facetValues: Array<any> = facets2[selectedFacet];
          if (facetValues && facetValues.length > 0) {
            for (let i: number = 0; i < facetValues.length; i++) {
              possibleFacetSelector.push({
                id: facetValues[i].id,
                value: facetValues[i].value,
              });
            }
          }
        } else if (is(facets, SearchFacetsImpl)) {
          const facetList: Array<any> = facets.getFacets();
          for (const f of facetList as Facet[]) {
            if (f.getKey() === selectedFacet || f.getLabel() === selectedFacet) {
              for (const value of f.getValues()) {
                possibleFacetSelector.push({
                  id: value.query,
                  value: value.label,
                });
              }
            }
          }
        }

        return possibleFacetSelector;
      },
    );
  }
}

export default SingleCategoryFacetsFieldGroupBase;
