import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import CollapsiblePanel from "@coremedia/studio-client.ext.ui-components/components/panel/CollapsiblePanel";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMElement from "@coremedia/studio-client.ext.ui-components/models/bem/BEMElement";
import BEMModifier from "@coremedia/studio-client.ext.ui-components/models/bem/BEMModifier";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import FacetComboField from "./FacetComboField";
import FacetFilterFieldWrapper from "./FacetFilterFieldWrapper";
import FacetTagField from "./FacetTagField";

interface FacetFilterFieldWrapperBaseConfig extends Config<CollapsiblePanel>, Partial<Pick<FacetFilterFieldWrapperBase,
  "facet" |
  "stateBean" |
  "removeHandler"
>> {
}

class FacetFilterFieldWrapperBase extends CollapsiblePanel {
  declare Config: FacetFilterFieldWrapperBaseConfig;

  static readonly BLOCK: BEMBlock = new BEMBlock("cm-filter-panel");

  static readonly ELEMENT_BODY: BEMElement = FacetFilterFieldWrapperBase.BLOCK.createElement("body");

  static readonly ELEMENT_HEADER: BEMElement = FacetFilterFieldWrapperBase.BLOCK.createElement("header");

  static readonly ELEMENT_REMOVE: BEMElement = FacetFilterFieldWrapperBase.BLOCK.createElement("remove");

  static readonly MODIFIER_CUSTOMIZED: BEMModifier = FacetFilterFieldWrapperBase.BLOCK.createModifier("customized");

  facet: Facet = null;

  stateBean: Bean = null;

  removeHandler: AnyFunction = null;

  #facetExpression: ValueExpression = null;

  #selectedFacetValuesExpression: ValueExpression = null;

  #modifierVE: ValueExpression = null;

  constructor(config: Config<FacetFilterFieldWrapper> = null) {
    super(config);

    let xType = FacetComboField.xtype;
    if (config.facet.isMultiSelect()) {
      xType = FacetTagField.xtype;
    }

    const editorCfg: Record<string, any> = {
      facetValueExpression: this.getFacetExpression(config),
      selectedFacetValuesExpression: this.getSelectedFacetValuesExpression(config),
      xtype: xType,
    };

    const editor: any = ComponentManager.create(editorCfg);
    this.add(editor);
  }

  reset(): void {
    this.getSelectedFacetValuesExpression().setValue([]);
  }

  protected getFacetExpression(config: Config<FacetFilterFieldWrapper>): ValueExpression {
    if (!this.#facetExpression) {
      this.#facetExpression = ValueExpressionFactory.createFromValue(config.facet);
    }
    return this.#facetExpression;
  }

  getSelectedFacetValuesExpression(config: Config<FacetFilterFieldWrapper> = null): ValueExpression {
    if (!this.#selectedFacetValuesExpression) {
      this.#selectedFacetValuesExpression = ValueExpressionFactory.create(config.facet.getKey(), config.stateBean);
      if (!this.#selectedFacetValuesExpression.getValue()) {
        this.#selectedFacetValuesExpression.setValue([]);
      }
    }
    return this.#selectedFacetValuesExpression;
  }

  protected getModifierVE(): ValueExpression {
    if (!this.#modifierVE) {
      this.#modifierVE = ValueExpressionFactory.createFromFunction((): Array<any> => {
        const facets: Array<any> = this.getSelectedFacetValuesExpression().getValue();
        if (facets === undefined) {
          return [];
        }

        if (facets.length === 0) {
          return [];
        } else {
          return [FacetFilterFieldWrapperBase.MODIFIER_CUSTOMIZED.getIdentifier()];
        }
      });
    }
    return this.#modifierVE;
  }

  protected removeFilter(): void {
    this.removeHandler(this.facet);
  }

  protected formatItemId(facet: Facet): string {
    const id = facet.getKey();
    return id.replace(/\\/g, "-").replace(/\./g, "-");
  }
}

export default FacetFilterFieldWrapperBase;
