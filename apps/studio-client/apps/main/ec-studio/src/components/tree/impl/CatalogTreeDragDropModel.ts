import DragDropModel from "@coremedia/studio-client.ext.ui-components/models/DragDropModel";
import TreeModel from "@coremedia/studio-client.ext.ui-components/models/TreeModel";
import DragSource from "@jangaroo/ext-ts/dd/DragSource";
import { mixin } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";

class CatalogTreeDragDropModel implements DragDropModel {

  #treeModel: TreeModel = null;

  constructor(catalogTree: TreeModel) {
    this.#treeModel = catalogTree;
  }

  performDefaultAction(droppedNodeIds: Array<any>, targetNodeId: string, callback?: AnyFunction): void {
  }

  performAlternativeAction(droppedNodeIds: Array<any>, targetNodeId: string, callback?: AnyFunction): void {
  }

  allowDefaultAction(source: DragSource, nodeIds: Array<any>, targetNodeId: string): boolean {
    return false;
  }

  allowAlternativeAction(source: DragSource, nodeIds: Array<any>, targetNodeId: string): boolean {
    return false;
  }

  getModelItemId(): string {
    return undefined;
  }
}
mixin(CatalogTreeDragDropModel, DragDropModel);

export default CatalogTreeDragDropModel;
