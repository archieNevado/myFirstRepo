import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Ext from "@jangaroo/ext-ts";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyExplorerPanel from "../administration/TaxonomyExplorerPanel";
import CutKeywordAction from "./CutKeywordAction";

interface CutKeywordActionBaseConfig extends Config<Action> {
}

class CutKeywordActionBase extends Action {
  declare Config: CutKeywordActionBaseConfig;

  #selectionExpression: ValueExpression = null;

  #clipboardValueExpression: ValueExpression = null;

  readonly items: Array<any>;

  constructor(config: Config<CutKeywordAction> = null) {
    super((()=>{
      config.handler = bind(this, this.#cutNode);
      config.disabled = true;
      config.text = TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_cut_button_label;
      return config;
    })());
    this.#clipboardValueExpression = config.clipboardValueExpression;
    this.#selectionExpression = config.selectionExpression;
    this.#selectionExpression.addChangeListener(bind(this, this.#updateDisabled));
  }

  override addComponent(comp: Component): void {
    super.addComponent(comp);
    this.#updateDisabled();
  }

  #updateDisabled(): void {
    const selections: Array<any> = this.#selectionExpression.getValue();
    let disable = selections.length === 0;
    for (const node of selections as TaxonomyNode[]) {
      if (node.isRoot()) {
        disable = true;
        break;
      }
    }

    this.setDisabled(disable);
  }

  /**
   * Remembers the node for a cut'n paste action
   */
  #cutNode(): void {
    const taxonomyExplorer = as(Ext.getCmp("taxonomyExplorerPanel"), TaxonomyExplorerPanel);

    const previousSelection: Array<any> = this.#clipboardValueExpression.getValue();
    const selections: Array<any> = this.#selectionExpression.getValue();
    this.#clipboardValueExpression.setValue(selections);

    if (previousSelection) {
      for (const prevSelection of previousSelection as TaxonomyNode[]) {
        if (taxonomyExplorer.getColumnContainer(prevSelection)) {
          taxonomyExplorer.getColumnContainer(prevSelection).updateNode(prevSelection);
        }
      }
    }

    for (const selection of selections as TaxonomyNode[]) {
      taxonomyExplorer.getColumnContainer(selection).updateNode(selection);
    }

    this.#updateDisabled();
  }

  override removeComponent(comp: Component): void {
    super.removeComponent(comp);
    if (this.items && this.items.length === 0) {
      this.#selectionExpression && this.#selectionExpression.removeChangeListener(bind(this, this.#updateDisabled));
    }
  }
}

export default CutKeywordActionBase;
