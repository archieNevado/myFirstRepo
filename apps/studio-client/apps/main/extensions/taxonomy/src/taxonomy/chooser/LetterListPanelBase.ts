import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMElement from "@coremedia/studio-client.ext.ui-components/models/bem/BEMElement";
import LoadMaskSkin from "@coremedia/studio-client.ext.ui-components/skins/LoadMaskSkin";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import LoadMask from "@jangaroo/ext-ts/LoadMask";
import Template from "@jangaroo/ext-ts/Template";
import XTemplate from "@jangaroo/ext-ts/XTemplate";
import Container from "@jangaroo/ext-ts/container/Container";
import Event from "@jangaroo/ext-ts/event/Event";
import DataView from "@jangaroo/ext-ts/view/View";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyUtil from "../TaxonomyUtil";
import LetterListPanel from "./LetterListPanel";
import TaxonomySelectionWindow from "./TaxonomySelectionWindow";

interface LetterListPanelBaseConfig extends Config<Container>, Partial<Pick<LetterListPanelBase,
  "nodePathExpression" |
  "loadingExpression" |
  "singleSelection"
>> {
}

/**
 * Displays the active taxonomy node sorted alphabetically.
 */
class LetterListPanelBase extends Container {
  declare Config: LetterListPanelBaseConfig;

  readonly ITEMS_CONTAINER_ITEM_ID: string = "itemsContainer";

  /**
   * Contains the row that children should be displayed next.
   */
  nodePathExpression: ValueExpression = null;

  protected static readonly LIST_BLOCK: BEMBlock = new BEMBlock("widget-content-list");

  protected static readonly LIST_ELEMENT_ENTRY: BEMElement = LetterListPanelBase.LIST_BLOCK.createElement("entry");

  loadingExpression: ValueExpression = null;

  #listValuesExpression: ValueExpression = null;

  #activeLetters: ValueExpression = null;

  #selectedLetter: ValueExpression = null;

  #selectionExpression: ValueExpression = null;

  #selectedNodeList: ValueExpression = null;

  #taxonomyId: string = null;

  #activeNodeList: TaxonomyNodeList = null;

  //used for skipping letter column rendering
  #letter2NodeMap: Bean = null;

  #singleSelection: boolean = false;

  /** If true, only one item can be selected from the list. */
  singleSelection: string = null;

  protected static readonly TEMPLATE: Template = new XTemplate(
    "<table class=\"" + LetterListPanelBase.LIST_BLOCK + "\">",
    "  <tpl for=\".\">",
    "    <tr class=\"" + LetterListPanelBase.LIST_ELEMENT_ENTRY + " cm-taxonomy-row\">",
    "      <td style=\"font-weight:bold; width: 30px; text-align: center;\"><b>{letter}</b>",
    "      </td>",
    "      <td>",
    "        <span class=\"cm-taxonomy-node {customCss}\" style=\"cursor:pointer;\">",
    "           <span class=\"cm-taxonomy-node__box\">",
    "             <span class=\"cm-taxonomy-node__name\">",
    "               <tpl if=\"!leaf\"><b>{name}</b></tpl>",
    "               <tpl if=\"leaf\">{name}</tpl>",
    "             </span>",
    "             <tpl if=\"renderControl\">",
    "               <tpl if=\"!added\"><span class=\"cm-taxonomy-node__control cm-core-icons cm-core-icons--add-special-size\" data-ref=\"{ref}\"></span></tpl>",
    "               <tpl if=\"added\"><span class=\"cm-taxonomy-node__control cm-core-icons cm-core-icons--remove-small\" data-ref=\"{ref}\"></span></tpl>",
    "             </tpl>",
    "           </span>",
    "         </span>",
    "      </td>",
    "      <td style=\"width:16px;\">",
    "        <tpl if=\"!leaf\"><span style=\"cursor:pointer;\" width=\"16\" height=\"16\" class=\"cm-core-icons cm-core-icons--arrow-right\"></span></tpl>",
    "      </td>",
    "      <td style=\"width:16px;\">",
    "      </td>",
    "    </tr>",
    "  </tpl>",
    "</table>",
  ).compile();

  #loadMask: LoadMask = null;

  constructor(config: Config<LetterListPanel> = null) {
    super(config);
    this.#activeLetters = config.activeLetters;
    this.#taxonomyId = config.taxonomyId;
    this.#activeLetters = config.activeLetters;

    this.#selectionExpression = config.selectionExpression;

    this.#selectedNodeList = config.selectedNodeList;
    this.#selectedNodeList.addChangeListener(bind(this, this.updateUI));

    this.#selectedLetter = config.selectedLetter;
    this.#selectedLetter.addChangeListener(bind(this, this.#updateSelectedLetter));

    config.loadingExpression.addChangeListener(bind(this, this.#loadingChanged));
  }

  #loadingChanged(ve: ValueExpression): void {
    const loading: boolean = ve.getValue();
    if (loading) {
      this.#loadMask.show();
    } else {
      this.#loadMask.hide();
    }
  }

  protected override afterRender(): void {
    super.afterRender();
    this.#loadMask = this.#createLoadMask();

    this.nodePathExpression.setValue(this.#taxonomyId); //lets start with the root level to show

    // listen to click events
    this.#getDataView().on("itemclick", bind(this, this.#listEntryClicked));
  }

  #createLoadMask(): LoadMask {
    const loadMaskConfig = Config(LoadMask);
    loadMaskConfig.ui = LoadMaskSkin.LIGHT.getSkin();
    loadMaskConfig.msg = TaxonomyStudioPlugin_properties.TaxonomyExplorerColumn_emptyText_loading;
    loadMaskConfig.target = this.up();
    loadMaskConfig.baseCls = "cm-thread-load-mask";
    loadMaskConfig.style = "background:rgba(0,0,0,0.65);opacity:1;z-index:1001";
    const loadMask = new LoadMask(loadMaskConfig);
    return loadMask;
  }

  #listEntryClicked(dataView: DataView, record: BeanRecord, node: HTMLElement, index: number, e: Event): void {
    // make sure only text element can be clicked
    if (e.getTarget(".cm-taxonomy-node__name", null, true)) {
      const bean = record.getBean();
      const ref: string = bean.get("ref");
      this.nodeClicked(ref);
    } else if (e.getTarget(".cm-taxonomy-node__control", null, true)) {
      const childBean = record.getBean();
      const childRef: string = childBean.get("ref");
      this.plusMinusClicked(childRef);
    } else if (e.getTarget(".cm-core-icons--arrow-right", null, true)) {
      const pathBean = record.getBean();
      const pathRef: string = pathBean.get("ref");
      this.#updateSelection(pathRef);
    }
  }

  protected getListValuesExpression(): ValueExpression {
    if (!this.#listValuesExpression) {
      this.#listValuesExpression = ValueExpressionFactory.create("nodes", beanFactory._.createLocalBean());
    }
    return this.#listValuesExpression;
  }

  /**
   * Selects the entry in the list with the active letter
   */
  #updateSelectedLetter(): void {
    const letter: string = this.#selectedLetter.getValue().toLowerCase();
    if (letter) {
      for (let i = 0; i < this.#activeNodeList.getNodes().length; i++) {
        const node: TaxonomyNode = this.#activeNodeList.getNodes()[i];
        const itemLetter = this.letterRenderer(node).toLowerCase();
        if (itemLetter === letter) {
          const table: any = this.el.query(LetterListPanelBase.LIST_BLOCK.getCSSSelector())[0];
          table.firstElementChild.children[i].scrollIntoView();
          break;
        }
      }
    }
  }

  /**
   * Refresh the path and list and button column.
   */
  updateUI(): void {
    const list: TaxonomyNodeList = this.#selectedNodeList.getValue();
    if (list) {
      this.#doUpdate();
    }
  }

  #doUpdate(): void {
    this.loadingExpression.setValue(true);
    //give the load mask some time to appear, otherwise the dialog may look stuck
    window.setTimeout((): void => {
      const list: TaxonomyNodeList = this.#selectedNodeList.getValue();
      if (list) {
        this.#activeNodeList = list;
        this.#letter2NodeMap = beanFactory._.createLocalBean();
        this.#updateLetterList(list);

        const nodes = this.#activeNodeList.getNodes();
        const result = [];
        for (const node of nodes as TaxonomyNode[]) {
          const bean = beanFactory._.createLocalBean({});
          const letter = this.letterRenderer(node);
          bean.set("letter", letter);
          bean.set("name", node.getName());
          bean.set("leaf", node.isLeaf());
          bean.set("ref", node.getRef());

          const selection: Array<any> = this.#selectionExpression.getValue();
          const added = TaxonomyUtil.isInSelection(selection, node.getRef());
          bean.set("added", added);
          if (added) {
            bean.set("customCss", "cm-taxonomy-node--leaf");
          }

          bean.set("renderControl", added || (this.#singleSelection && selection.length === 0) || !this.#singleSelection);
          result.push(bean);
        }
        this.getListValuesExpression().setValue(result);
        this.loadingExpression.setValue(false);
      }
    }, 50);
  }

  #getDataView(): DataView {
    return as(this.queryById(this.ITEMS_CONTAINER_ITEM_ID), DataView);
  }

  /**
   * Fills the letter value expression with an array of the active letters.
   * @param list
   */
  #updateLetterList(list: TaxonomyNodeList): void {
    const letters = [];
    const nodes = list.getNodes();
    for (let i = 0; i < nodes.length; i++) {
      const name: string = nodes[i].getName();
      letters.push(name.substr(0, 1).toLowerCase());
    }
    this.#activeLetters.setValue(letters);
  }

  /**
   * Fired when the user double clicks a row.
   * The next taxonomy child level of the selected node is entered then.
   */
  #updateSelection(ref: string): void {
    if (ref) {
      const id = TaxonomyUtil.getRestIdFromCapId(ref);
      if (this.#activeNodeList && !this.#activeNodeList.getNode(id).isLeaf()) {
        //fire event for path update
        this.nodePathExpression.setValue(id);
      }
    }
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Displays each letter of a taxonomy
   */
  letterRenderer(node: TaxonomyNode): string {
    const letter = node.getName().substr(0, 1).toUpperCase();
    let html = "";
    if (!this.#letter2NodeMap.get(letter) || this.#letter2NodeMap.get(letter).getRef() === node.getRef()) {
      html = letter;
      this.#letter2NodeMap.set(letter, node);
    }
    return html;
  }

  /**
   * Handler executed when the node text is clicked on.
   */
  nodeClicked(ref: string): void {
    this.#updateSelection(ref); //has the same behaviour like when double clicking a row.
  }

  /**
   * Handler executed when the plus button is clicked.
   * Used in TaxonomyRenderer#plusMinusClicked$static
   */
  plusMinusClicked(nodeRef: string): void {
    const parent = as(this.findParentByType(TaxonomySelectionWindow.xtype), TaxonomySelectionWindow);
    parent.updateSelection(nodeRef, true, true);
  }
}

export default LetterListPanelBase;
