import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import RemoveTaxonomyAction from "./RemoveTaxonomyAction";

interface RemoveTaxonomyActionBaseConfig extends Config<Action> {
}

/**
 * Opens the taxonomy editor and shows the given taxonomy in the tree.
 */
class RemoveTaxonomyActionBase extends Action {
  declare Config: RemoveTaxonomyActionBaseConfig;

  #propertyName: string = null;

  #bindTo: ValueExpression = null;

  #selectedValuesExpression: ValueExpression = null;

  #propertyValueExpression: ValueExpression = null;

  #selectedPositionsExpression: ValueExpression = null;

  readonly items: Array<any>;

  constructor(config: Config<RemoveTaxonomyAction> = null) {
    super((()=>{
      config.handler = bind(this, this.removeTaxonomy);
      config.text = TaxonomyStudioPlugin_properties.TaxonomyLinkList_keyword_remove_text;
      return config;
    })());
    this.#propertyName = config.propertyName;
    this.#bindTo = config.bindTo;
    this.#selectedPositionsExpression = config.selectedPositionsExpression;
    this.#selectedValuesExpression = config.selectedValuesExpression;
    this.#selectedValuesExpression.addChangeListener(bind(this, this.#updateDisabled));
    this.#propertyValueExpression = this.#bindTo.extendBy("properties", this.#propertyName);
  }

  override addComponent(comp: Component): void {
    super.addComponent(comp);
    this.#updateDisabled();
  }

  /**
   * Update enabling/disabling selection depending.
   */
  #updateDisabled(): void {
    this.setDisabled(true);
    if (this.#selectedValuesExpression && this.#selectedValuesExpression.getValue() && as(this.#selectedValuesExpression.getValue(), Array).length > 0) {
      this.setDisabled(false);
    }
  }

  removeTaxonomy(): void {
    const originalValue: Array<any> = this.#propertyValueExpression.getValue();
    if (!originalValue) {
      // Should not happen, but be cautious.
      return;
    }
    const selectedPositions: Array<any> = this.#selectedPositionsExpression.getValue();
    const newValue = originalValue.filter((val: any, pos: number): boolean =>
      selectedPositions.indexOf(pos) < 0,
    );
    this.#propertyValueExpression.setValue(newValue);
  }

  override removeComponent(comp: Component): void {
    super.removeComponent(comp);
    if (this.items && this.items.length === 0) {
      this.#selectedValuesExpression && this.#selectedValuesExpression.removeChangeListener(bind(this, this.#updateDisabled));
    }
  }
}

export default RemoveTaxonomyActionBase;
