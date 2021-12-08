import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMModifier from "@coremedia/studio-client.ext.ui-components/models/bem/BEMModifier";
import ObservableUtil from "@coremedia/studio-client.ext.ui-components/util/ObservableUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import Model from "@jangaroo/ext-ts/data/Model";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import GridViewDragDropPlugin from "@jangaroo/ext-ts/grid/plugin/DragDrop";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import KeyEvent from "@jangaroo/runtime/KeyEvent";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyNodeFactory from "../TaxonomyNodeFactory";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyUtil from "../TaxonomyUtil";
import TaxonomyExplorerColumn from "./TaxonomyExplorerColumn";
import TaxonomyExplorerColumnDropTarget from "./TaxonomyExplorerColumnDropTarget";
import TaxonomyExplorerPanel from "./TaxonomyExplorerPanel";

interface TaxonomyExplorerColumnBaseConfig extends Config<GridPanel> {
}

class TaxonomyExplorerColumnBase extends GridPanel {
  declare Config: TaxonomyExplorerColumnBaseConfig;

  static readonly DRAG_DROP_PLUGIN_ID: string = "dragdrop";

  static readonly #COLUMN_ENTRY_BLOCK: BEMBlock = new BEMBlock("cm-column-entry");

  static readonly #COLUMN_ENTRY_MODIFIER_EMPTY: BEMModifier = TaxonomyExplorerColumnBase.#COLUMN_ENTRY_BLOCK.createModifier("empty");

  static readonly #COLUMN_ENTRY_MODIFIER_NOT_EXTENDABLE: BEMModifier = TaxonomyExplorerColumnBase.#COLUMN_ENTRY_BLOCK.createModifier("no-extendable");

  static readonly #COLUMN_ENTRY_MODIFIER_LEAF: BEMModifier = TaxonomyExplorerColumnBase.#COLUMN_ENTRY_BLOCK.createModifier("leaf");

  static readonly #COLUMN_ENTRY_MODIFIER_MARKED_FOR_COPY: BEMModifier = TaxonomyExplorerColumnBase.#COLUMN_ENTRY_BLOCK.createModifier("marked-for-copy");

  #globalSelectedNodeExpression: ValueExpression = null;

  #entriesValueExpression: ValueExpression = null;

  #siteSelectionExpression: ValueExpression = null;

  #parentNode: TaxonomyNode = null;

  #activeNode: TaxonomyNode = null;

  #taxonomyExplorerColumnDropTarget: TaxonomyExplorerColumnDropTarget = null;

  constructor(config: Config<TaxonomyExplorerColumn> = null) {
    super(config);
    this.#siteSelectionExpression = config.siteSelectionExpression;
    this.#parentNode = config.parentNode;
    this.#globalSelectedNodeExpression = config.selectedNodeExpression;
    this.initColumn();

    this.getSelectionModel().addListener("selectionchange", bind(this, this.selectionChanged));
    this.addListener("rowclick", bind(this, this.#onPanelClick));
    this.addListener("beforedestroy", bind(this, this.#onBeforeDestroy), this, { single: true });
  }

  //Dnd is causing focus issues/console errors: since we don't need the focus on the column, we skip the parent call
  override focus(selectText?: any, delay?: any, callback: AnyFunction = null, scope: AnyFunction = null): this {
    return this;
  }

  protected override afterRender(): void {
    super.afterRender();
    this.#addKeyNavigation();

    this.#taxonomyExplorerColumnDropTarget = new TaxonomyExplorerColumnDropTarget(this);

    const ddPlugin = as(this.getView().getPlugin(TaxonomyExplorerColumnBase.DRAG_DROP_PLUGIN_ID), GridViewDragDropPlugin);
    if (ddPlugin && ddPlugin.dragZone) {
      ddPlugin.dragZone["getDragText"] = bind(this, this.#getDragText);
      //DnD is causing focus issues, so we skip the focus handling here and simply call the parent
      ddPlugin.dragZone["onValidDrop"] = ((target: any, e: any, id: any): void => {
        ddPlugin.dragZone["callParent"]([target, e, id]);
      });
      ddPlugin.dropZone.addToGroup("taxonomies");
      ddPlugin.dropZone.onNodeOver = bind(this.#taxonomyExplorerColumnDropTarget, this.#taxonomyExplorerColumnDropTarget.notifyOnNodeOver);
      ddPlugin.dropZone.onNodeDrop = bind(this.#taxonomyExplorerColumnDropTarget, this.#taxonomyExplorerColumnDropTarget.notifyOnNodeDrop);
      ddPlugin.dropZone.onContainerOver = bind(this.#taxonomyExplorerColumnDropTarget, this.#taxonomyExplorerColumnDropTarget.notifyOnContainerOver);
    }
  }

  /**
   * Registers the additional key handlers for left/right navigation.
   */
  #addKeyNavigation(): void {
    this.addListener("cellkeydown", bind(this, this.#onKeyInput));
  }

  #onKeyInput(grid: any, td: any, cellIndex: any, record: any, tr: any, rowIndex: any, e: any): any {
    const key: number = e.getKey();
    if (key === KeyEvent.DOM_VK_LEFT) {
      if (this.#globalSelectedNodeExpression && this.#parentNode) {
        this.#activeNode = this.#parentNode;
        this.#globalSelectedNodeExpression.setValue([this.#parentNode]);
        TaxonomyExplorerColumnBase.#getExplorerPanel().getColumnContainer(this.#parentNode).selectNode(this.#parentNode);
        return false;
      }
    } else if (key === KeyEvent.DOM_VK_RIGHT && !this.#activeNode.isLeaf()) {
      this.#activeNode.loadChildren(true, (list: TaxonomyNodeList): void => {
        if (list.size() > 0) {
          const selectNode: TaxonomyNode = list.getNodes()[0];
          this.#globalSelectedNodeExpression.setValue([selectNode]);
          TaxonomyExplorerColumnBase.#getExplorerPanel().getColumnContainer(selectNode).selectNode(selectNode);
        }
      });
      return false;
    } else if (key === KeyEvent.DOM_VK_RIGHT) {
      return false;
    }

    return true;
  }

  /**
   * Triggered when the reload button is pressed.
   */
  reload(): void {
    this.getEntriesValueExpression().setValue(undefined);
    this.initColumn(true);
  }

  /**
   * Returns the parent node this column has been created for.
   * @return
   */
  getParentNode(): TaxonomyNode {
    return this.#parentNode;
  }

  getEntriesValueExpression(): ValueExpression {
    if (!this.#entriesValueExpression) {
      const emptyBean = beanFactory._.createLocalBean();
      this.#entriesValueExpression = ValueExpressionFactory.create("nodes", emptyBean);
    }
    return this.#entriesValueExpression;
  }

  protected selectionChanged(): void {
    const selections: Array<any> = as(this.getSelectionModel(), RowSelectionModel).getSelection();
    const selectionResult = [];
    for (const selection of selections as Model[]) {
      const selectedNode = TaxonomyNode.forValues(selection.data.name,
        selection.data.type,
        selection.data.ref,
        selection.data.siteId,
        selection.data.level,
        selection.data.root,
        selection.data.leaf,
        selection.data.taxonomyId,
        selection.data.selectable,
        selection.data.extendable);
      selectionResult.push(selectedNode);
    }

    if (this.#globalSelectedNodeExpression) {
      this.#activeNode = null;
      if (selectionResult.length > 0) {
        this.#activeNode = selectionResult[0];
      }
      this.#globalSelectedNodeExpression.setValue(selectionResult);
    }
  }

  /**
   * Selects the given node in the list
   * record entry.
   * @param node
   * @param callback optional callback method
   */
  selectNode(node: TaxonomyNode, callback?: AnyFunction): void {
    this.#activeNode = node;
    this.#doSelect(callback);
  }

  /**
   * Selects the active node or clears the selection
   * if the active node is not set.
   * @param callback optional callback method
   */
  #doSelect(callback?: AnyFunction): void {
    ValueExpressionFactory.createFromFunction((): boolean => {
      if (!this.getStore().isLoaded()) {
        ObservableUtil.dependOn(this.getStore(), "load");
        return undefined;
      }
      return true;
    }).loadValue((loaded: boolean): void => {
      if (this.#activeNode) {
        for (let i = 0; i < this.getStore().getCount(); i++) {
          const nodeRecord = this.getStore().getAt(i);
          if (nodeRecord.data.ref === this.#activeNode.getRef()) {
            as(this.getSelectionModel(), RowSelectionModel).select([nodeRecord], false, true);
            this.getView().focusRow(i);
          }
        }
      } else {
        as(this.getSelectionModel(), RowSelectionModel).deselectRange(0, this.getStore().getCount() - 1);
      }

      if (callback) {
        callback.call(null);
      }
    });
  }

  /**
   * Searches the list for the given node and updates the
   * record entry.
   * @param node
   */
  updateNode(node: TaxonomyNode): boolean {
    const nodeStore = this.getStore();
    for (let i = 0; i < nodeStore.getCount(); i++) {
      const nodeRecord = nodeStore.getAt(i);
      if (nodeRecord.data.ref === node.getRef()) {
        nodeRecord.data.name = TaxonomyUtil.escapeHTML(node.getName());
        nodeRecord.data.type = node.getType();
        nodeRecord.data.root = node.isRoot();
        nodeRecord.data.extendable = node.isExtendable();
        nodeRecord.data.selectable = node.isSelectable();
        nodeRecord.data.leaf = node.isLeaf();
        nodeRecord.commit(false);
        return true;
      }
    }
    return false;
  }

  /**
   * Loads the values into the list.
   */
  initColumn(reload: boolean = true): void {
    this.#updateLoadStatus(TaxonomyStudioPlugin_properties.TaxonomyExplorerColumn_emptyText_loading);
    const callback: AnyFunction = (list: TaxonomyNodeList): void => {
      this.getEntriesValueExpression().setValue(list.toJson());
      if (list.toJson().length === 0) {
        this.#updateLoadStatus(TaxonomyStudioPlugin_properties.TaxonomyExplorerColumn_emptyText_no_keywords);
      }
    };
    if (this.#parentNode) {
      this.#parentNode.loadChildren(true, callback);
    } else {
      const site: string = this.#siteSelectionExpression.getValue();
      TaxonomyNodeFactory.loadTaxonomies(site, callback, reload);
    }
  }

  #updateLoadStatus(text: string): void {
    this.getView().setEmptyText(text);
    if (this.isVisible(true)) {
      this.getView().refresh();
    }
  }

  #getDragText(): string {
    const selection: Array<any> = this.#globalSelectedNodeExpression.getValue();
    const names = [];
    for (const node of selection as TaxonomyNode[]) {
      names.push(TaxonomyUtil.escapeHTML(node.getName()));
    }
    let text = names.join(", ");
    if (text.length > 100) {
      text = text.substr(0, 100) + "...";
    }

    return text;
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Displays the image for each link list item.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected nameColRenderer(value: any, metaData: any, record: Model): string {
    let name: string = record.data.name;
    const modifiers = [];

    if (record.data.root) {
      if (record.data.siteId) {
        const i18nName = TaxonomyStudioPlugin_properties[name];
        if (i18nName) {
          name = i18nName;
        }

        const site = editorContext._.getSitesService().getSite(record.data.siteId);
        if (site) {
          const siteName = site.getName();
          const locale = site.getLocale();
          name += " (" + siteName + " - " + locale.getDisplayName() + ")";
        }
      }
    } else {
      if (name.length === 0) {
        name = TaxonomyStudioPlugin_properties.TaxonomyExplorerColumn_undefined;
        modifiers.push(TaxonomyExplorerColumnBase.#COLUMN_ENTRY_MODIFIER_EMPTY);
      }
      if (!record.data.extendable || !record.data.selectable) {
        modifiers.push(TaxonomyExplorerColumnBase.#COLUMN_ENTRY_MODIFIER_NOT_EXTENDABLE);
      } else if (record.data.leaf) {
        modifiers.push(TaxonomyExplorerColumnBase.#COLUMN_ENTRY_MODIFIER_LEAF);
      }
    }
    if (TaxonomyExplorerColumnBase.#getExplorerPanel().isMarkedForCopying(record.data.ref)) {
      modifiers.push(TaxonomyExplorerColumnBase.#COLUMN_ENTRY_MODIFIER_MARKED_FOR_COPY);
    }

    // determine css classes
    let cls = TaxonomyExplorerColumnBase.#COLUMN_ENTRY_BLOCK.getCSSClass();
    modifiers.forEach((modifier: BEMModifier): void => {
      cls += " " + modifier.getCSSClass();
    });

    // convert to html
    return "<div class=\"" + cls + "\">" + name + "</div>";
  }

  /**
   * Executed for a regular click on the panel, updates
   * backward selections that are on the same selection path.
   */
  #onPanelClick(): void {
    this.selectionChanged();
  }

  /**
   * Returns the parent taxonomy explorer panel.
   * @return
   */
  static #getExplorerPanel(): TaxonomyExplorerPanel {
    return as(Ext.getCmp("taxonomyExplorerPanel"), TaxonomyExplorerPanel);
  }

  //noinspection JSMethodCanBeStatic,JSUnusedLocalSymbols
  /**
   * The pointer column renderer shows a '>' symbol if the node is not a leaf.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected pointerColRenderer(value: any, metaData: any, record: Model): string {
    const leaf: boolean = record.data.leaf;
    if (!leaf) {
      return "<span class=\"" + CoreIcons_properties.breadcrumb_arrow_right + "\"></span>";
    }
    return "";
  }

  #onBeforeDestroy(): void {
    this.#taxonomyExplorerColumnDropTarget && this.#taxonomyExplorerColumnDropTarget.unreg();
    if (this.getEl()) {
      this.removeListener("cellkeydown", bind(this, this.#onKeyInput));
    }
  }
}

export default TaxonomyExplorerColumnBase;
