import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import AddKeywordAction from "./AddKeywordAction";

interface AddKeywordActionBaseConfig extends Config<Action> {
}

class AddKeywordActionBase extends Action {
  declare Config: AddKeywordActionBaseConfig;

  #selectionExpression: ValueExpression = null;

  #propertyValueExpression: ValueExpression = null;

  readonly items: Array<any>;

  constructor(config: Config<AddKeywordAction> = null) {
    super((()=>{
      config.handler = bind(this, this.applySelection);
      config.text = TaxonomyStudioPlugin_properties.TaxonomyLinkList_add_suggestion_action_text;
      return config;
    })());
    this.#selectionExpression = config.selectionExpression;
    this.#propertyValueExpression = config.bindTo.extendBy("properties." + config.propertyName);
    this.#selectionExpression.addChangeListener(bind(this, this.#updateDisabled));
  }

  override addComponent(comp: Component): void {
    super.addComponent(comp);
    this.#updateDisabled();
  }

  #updateDisabled(): void {
    this.setDisabled(true);
    if (this.#selectionExpression.getValue() && as(this.#selectionExpression.getValue(), Array).length > 0) {
      this.setDisabled(false);
    }
  }

  applySelection(): void {
    const selection: Array<any> = this.#selectionExpression.getValue();
    const existingEntries = this.#propertyValueExpression.getValue();
    const newEntries = [];
    for (let i = 0; i < existingEntries.length; i++) {
      newEntries.push(existingEntries[i]);
    }
    for (let j = 0; j < selection.length; j++) {
      newEntries.push(selection[j]);
    }
    this.#propertyValueExpression.setValue(newEntries);
  }

  override removeComponent(comp: Component): void {
    super.removeComponent(comp);
    if (this.items && this.items.length === 0) {
      this.#selectionExpression && this.#selectionExpression.removeChangeListener(bind(this, this.#updateDisabled));
    }
  }
}

export default AddKeywordActionBase;
