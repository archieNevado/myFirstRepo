import TreeModel from "@coremedia/studio-client.ext.ui-components/models/TreeModel";
import RepositoryTreeDragDropModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/RepositoryTreeDragDropModel";

/**
 * Drag and Drop model for the content based catalog tree.
 * The catalog tree needs a separate DragDropModel because of the TreeModel
 * instance that is passed in the constructor. When a drag is executed the tree model of the
 * source and the target are checked if they are equal. Otherwise, the drag and drop action is not allowed.
 */
class RepositoryCatalogTreeDragDropModel extends RepositoryTreeDragDropModel {

  constructor(treeModel: TreeModel) {
    super(treeModel);
  }

  override toString(): string {
    return "DnD Model for the Repository Catalog";
  }
}

export default RepositoryCatalogTreeDragDropModel;
