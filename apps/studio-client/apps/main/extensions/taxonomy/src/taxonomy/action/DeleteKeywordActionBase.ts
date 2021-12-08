import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Ext from "@jangaroo/ext-ts";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyExplorerPanel from "../administration/TaxonomyExplorerPanel";
import DeleteKeywordAction from "./DeleteKeywordAction";

interface DeleteKeywordActionBaseConfig extends Config<Action> {
}

class DeleteKeywordActionBase extends Action {
  declare Config: DeleteKeywordActionBaseConfig;

  #selectionExpression: ValueExpression = null;

  readonly items: Array<any>;

  constructor(config: Config<DeleteKeywordAction> = null) {
    super((()=>{
      config.handler = bind(this, this.#deleteNodes);
      config.disabled = true;
      config.text = TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_delete_button_label;
      return config;
    })());
    this.#selectionExpression = config.selectionExpression;
    this.#selectionExpression.addChangeListener(bind(this, this.#updateDisabled));
  }

  override addComponent(comp: Component): void {
    super.addComponent(comp);
    this.#updateDisabled();
  }

  #updateDisabled(): void {
    const selections: Array<any> = this.#selectionExpression.getValue();
    let disable: boolean = selections.length === 0;
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
  #deleteNodes(): void {
    const taxonomyExplorer = as(Ext.getCmp("taxonomyExplorerPanel"), TaxonomyExplorerPanel);
    taxonomyExplorer.deleteNodes();
    this.#updateDisabled();
  }

  override removeComponent(comp: Component): void {
    super.removeComponent(comp);
    if (this.items && this.items.length === 0) {
      this.#selectionExpression && this.#selectionExpression.removeChangeListener(bind(this, this.#updateDisabled));
    }
  }
}

export default DeleteKeywordActionBase;
