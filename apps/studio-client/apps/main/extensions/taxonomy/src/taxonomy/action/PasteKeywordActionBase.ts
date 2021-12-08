import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Ext from "@jangaroo/ext-ts";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyExplorerPanel from "../administration/TaxonomyExplorerPanel";
import PasteKeywordAction from "./PasteKeywordAction";

interface PasteKeywordActionBaseConfig extends Config<Action> {
}

class PasteKeywordActionBase extends Action {
  declare Config: PasteKeywordActionBaseConfig;

  #selectionExpression: ValueExpression = null;

  #clipboardValueExpression: ValueExpression = null;

  readonly items: Array<any>;

  constructor(config: Config<PasteKeywordAction> = null) {
    super((()=>{
      config.handler = bind(this, this.pasteNodes);
      config.text = TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_paste_button_label;
      config.disabled = true;
      return config;
    })());
    this.#clipboardValueExpression = config.clipboardValueExpression;
    this.#selectionExpression = config.selectionExpression;
    this.#selectionExpression.addChangeListener(bind(this, this.#updateDisabled));
    this.#clipboardValueExpression.addChangeListener(bind(this, this.#updateDisabled));
  }

  override addComponent(comp: Component): void {
    super.addComponent(comp);
    this.#updateDisabled();
  }

  #updateDisabled(): void {
    const selection: Array<any> = this.#selectionExpression.getValue();
    let disabled: boolean = !this.#clipboardValueExpression.getValue() || this.#clipboardValueExpression.getValue().length === 0;

    for (const node of selection as TaxonomyNode[]) {
      if (this.#isNotPasteable(node)) {
        disabled = true;
        break;
      }

    }
    this.setDisabled(disabled);
  }

  #isNotPasteable(node: TaxonomyNode): boolean {
    if (this.#clipboardValueExpression.getValue()) {
      for (const clipboardNode of Object.values(this.#clipboardValueExpression.getValue() || {}) as TaxonomyNode[]) {
        if (clipboardNode.getRef() === node.getRef()) {
          return true;
        }
        if (clipboardNode.getTaxonomyId() !== node.getTaxonomyId()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Copies the cutted node as a child of the selected node.
   */
  protected pasteNodes(): void {
    const targetNodes: Array<any> = this.#selectionExpression.getValue();
    const sourceNodes: Array<any> = this.#clipboardValueExpression.getValue();
    if (sourceNodes.length > 0 && targetNodes.length > 0) {
      const taxonomyExplorer = as(Ext.getCmp("taxonomyExplorerPanel"), TaxonomyExplorerPanel);
      taxonomyExplorer.moveNodes(sourceNodes, targetNodes[0]);
    }
  }

  override removeComponent(comp: Component): void {
    super.removeComponent(comp);
    if (this.items && this.items.length === 0) {
      this.#selectionExpression && this.#selectionExpression.removeChangeListener(bind(this, this.#updateDisabled));
      this.#clipboardValueExpression && this.#clipboardValueExpression.removeChangeListener(bind(this, this.#updateDisabled));
    }
  }
}

export default PasteKeywordActionBase;
