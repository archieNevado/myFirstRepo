import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyStudioPluginBase from "../../TaxonomyStudioPluginBase";
import TaxonomyNodeFactory from "../TaxonomyNodeFactory";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyUtil from "../TaxonomyUtil";
import EditTaxonomyAction from "./EditTaxonomyAction";
import OpenTaxonomyEditorActionBase from "./OpenTaxonomyEditorActionBase";

interface EditTaxonomyActionBaseConfig extends Config<Action> {
}

/**
 * Opens the taxonomy editor and shows the given taxonomy in the tree.
 */
class EditTaxonomyActionBase extends Action {
  declare Config: EditTaxonomyActionBaseConfig;

  #taxonomyId: string = null;

  #bindTo: ValueExpression = null;

  readonly items: Array<any>;

  constructor(config: Config<EditTaxonomyAction> = null) {
    super((()=>{
      config.handler = bind(this, this.showTaxonomy);
      config.text = TaxonomyStudioPlugin_properties.TaxonomyLinkList_edit_action_text;
      return config;
    })());
    this.#taxonomyId = config.taxonomyId;
    this.#bindTo = config.bindTo;
    this.#bindTo.addChangeListener(bind(this, this.#updateDisabled));
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
    TaxonomyStudioPluginBase.isAdministrationEnabled((enabled: boolean) => {
      if (enabled && this.#bindTo && this.#bindTo.getValue() && as(this.#bindTo.getValue(), Array).length > 0) {
        this.setDisabled(false);
      }
    });
  }

  /**
   * 1. Open Editor
   * 2. Select node
   */
  showTaxonomy(): void {
    const activeContent: Content = this.#bindTo.getValue()[0];
    const restId = TaxonomyUtil.parseRestId(activeContent);
    const siteId = editorContext._.getSitesService().getSiteIdFor(activeContent);
    TaxonomyNodeFactory.loadPath(this.#taxonomyId, restId, siteId, (nodeList: TaxonomyNodeList): void => {
      const node = nodeList.getNode(restId);
      TaxonomyUtil.setLatestSelection(node);
      OpenTaxonomyEditorActionBase.showTaxonomyAdministrationWithLatestSelection();
    });
  }

  override removeComponent(comp: Component): void {
    super.removeComponent(comp);
    if (this.items && this.items.length === 0) {
      this.#bindTo && this.#bindTo.removeChangeListener(bind(this, this.#updateDisabled));
    }
  }
}

export default EditTaxonomyActionBase;
