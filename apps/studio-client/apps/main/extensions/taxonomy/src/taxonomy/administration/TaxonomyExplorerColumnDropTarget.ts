import Ext from "@jangaroo/ext-ts";
import DragDropManager from "@jangaroo/ext-ts/dd/DragDropManager";
import DragSource from "@jangaroo/ext-ts/dd/DragSource";
import DropTarget from "@jangaroo/ext-ts/dd/DropTarget";
import Event from "@jangaroo/ext-ts/event/Event";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyNode from "../TaxonomyNode";
import ColumnExpander from "./ColumnExpander";
import TaxonomyExplorerColumnBase from "./TaxonomyExplorerColumnBase";
import TaxonomyExplorerPanel from "./TaxonomyExplorerPanel";

class TaxonomyExplorerColumnDropTarget extends DropTarget {
  #column: TaxonomyExplorerColumnBase = null;

  #expander: ColumnExpander = null;

  constructor(component: TaxonomyExplorerColumnBase) {
    super(component.getEl(), Config(DropTarget, { ddGroup: "taxonomies" }));
    this.#column = component;
  }

  notifyOnNodeOver(nodeData: any, source: DragSource, e: Event, data: any): string {
    return this.notifyOver(source, e, data);
  }

  notifyOnNodeDrop(nodeData: any, source: DragSource, e: Event, data: any): boolean {
    return this.notifyDrop(source, e, data);
  }

  notifyOnContainerOver(source: DragSource, e: Event, data: any): string {
    return this.notifyOver(source, e, data);
  }

  override notifyEnter(source: DragSource, e: Event, data: any): string {
    return this.notifyOver(source, e, data);
  }

  override notifyOver(source: DragSource, e: Event, data: any): string {
    const sourceNodes = TaxonomyExplorerColumnDropTarget.#getSourceNodes(data);
    const targetNode = this.#isWriteable(data, e);
    if (!targetNode) {
      if (this.#expander) {
        this.#expander.cancel();
        this.#expander = null;
      }
      return this.dropNotAllowed;
    }

    //display the new columns if a node (not leaf) is hovered long enough
    const sourceNode: TaxonomyNode = sourceNodes[0];
    if (targetNode.getLevel() >= sourceNode.getLevel() && !this.#isAlreadyExpanded(targetNode)) { //=>we can not destroy our drag source, so we can only expand child columns
      this.#expand(targetNode);
    } else if (this.#expander) {
      this.#expander.cancel();
    }

    return this.dropAllowed;
  }

  /**
   * Checks if the current column is already expanded, which means
   * it is already resolved as parent.
   * @param targetNode
   * @return
   */
  #isAlreadyExpanded(targetNode: TaxonomyNode): boolean {
    return targetNode.getRef() == this.#column.getParentNode().getRef();
  }

  /**
   * Expand the node if is not a
   * @param activeTargetNode
   */
  #expand(targetNode: TaxonomyNode): void {
    if (!this.#expander) {
      this.#expander = new ColumnExpander(targetNode);
      this.#expander.expand();
      return;
    }

    //check existing expandler if the hovered not is already expanding.
    if (!this.#expander.expands(targetNode)) {
      this.#expander.cancel();
      this.#expander = new ColumnExpander(targetNode);
      this.#expander.expand();
    }
  }

  override notifyOut(source: DragSource, e: Event, data: any): void {
    //nothing
  }

  override notifyDrop(source: DragSource, e: Event, data: any): boolean {
    const sourceNodes = TaxonomyExplorerColumnDropTarget.#getSourceNodes(data);
    const targetNode = this.#isWriteable(data, e);
    if (!targetNode) {
      return false;
    }

    const taxonomyExplorer = as(Ext.getCmp("taxonomyExplorerPanel"), TaxonomyExplorerPanel);
    taxonomyExplorer.moveNodes(sourceNodes, targetNode);
    return true;
  }

  /**
   *
   * @param data
   * @return
   */
  static #getSourceNodes(data: any): Array<any> {
    const result = [];
    const records: Array<any> = data.records;
    for (const record of records) {
      const json: any = record.data;
      const sourceNode = new TaxonomyNode(json);
      result.push(sourceNode);
    }
    return result;
  }

  /**
   * Checks if a drop can be performed.
   * @param data
   * @param e
   * @return
   */
  #isWriteable(data: any, e: Event): TaxonomyNode {
    const sourceNodes = TaxonomyExplorerColumnDropTarget.#getSourceNodes(data);

    //check if the mouse if over a region with records
    const target: any = e.getTarget();
    const rowIndex = this.#column.getView().indexOf(target);
    if (!this.#column.getStore().getAt(rowIndex)) { //we drop an a column, not on a specific node
      //no drop on the root column, only on root nodes!
      if (this.#column.getItemId() === "taxonomyRootsColumn") {
        return null;
      }

      if (!data.view.grid) {
        return null;
      }

      //check if the dragged node is hovering over a column that is a child of it
      //this could be enabled but an additional check is missing then: if the new parent is the dragged node itself!
      if (parseInt(this.#column.getItemId().split("-")[1]) > parseInt(data.view.grid.getItemId().split("-")[1])) {
        return null;
      }

      if (this.#column.getItemId() !== data.view.grid.getItemId()) {
        const parentNode = this.#column.getParentNode();
        return parentNode;
      }
      return null;
    }
    const targetJson: any = this.#column.getStore().getAt(rowIndex).data;
    const targetNode = new TaxonomyNode(targetJson);

    for (const s1 of sourceNodes as TaxonomyNode[]) {
      //check if the mouse is still inside the dragged record
      if (s1.getRef() === targetNode.getRef()) {
        return null;
      }

      //check if we are still inside the same taxonomy tree
      if (s1.getTaxonomyId() !== targetNode.getTaxonomyId()) {
        return null;
      }

      //check if the dragged node is a parent of the entered node
      if (targetNode.getLevel() > s1.getLevel()) {
        return null;
      }
    }

    //check if the mouse is on the immediate parent, so dropping makes no sense (and also leads to errors)
    //We using the fact here that the parent must be the selected node of the corresponding column since
    //we can not determine the parent synchronously.
    const taxonomyExplorer = as(Ext.getCmp("taxonomyExplorerPanel"), TaxonomyExplorerPanel);
    const targetColumn = taxonomyExplorer.getColumnContainer(targetNode);
    const selected = as(targetColumn.getSelectionModel(), RowSelectionModel).getSelection()[0];

    for (const s4 of sourceNodes as TaxonomyNode[]) {
      if (targetNode.getLevel() === s4.getLevel() - 1 && selected.data.ref === targetNode.getRef()) { //direct parent and selected check
        return null;
      }
    }
    DragDropManager.refreshCache({ taxonomies: true }); //new drop zones are not registered during a drag!!!!!
    return targetNode;
  }
}

export default TaxonomyExplorerColumnDropTarget;
