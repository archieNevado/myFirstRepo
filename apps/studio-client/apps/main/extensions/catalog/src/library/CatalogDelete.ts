import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentRepository from "@coremedia/studio-client.cap-rest-client/content/ContentRepository";
import DeleteResult from "@coremedia/studio-client.cap-rest-client/content/results/DeleteResult";
import Logger from "@coremedia/studio-client.client-core-impl/logging/Logger";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogTreeRelation from "./CatalogTreeRelation";
import CatalogTreeRelationHelper from "./CatalogTreeRelationHelper";

class CatalogDelete {

  #treeRelation: CatalogTreeRelation = null;

  #contents: Array<any> = null;

  #callback: AnyFunction = null;

  constructor(catalogTreeRelation: CatalogTreeRelation, contents: Array<any>, callback?: AnyFunction) {
    this.#treeRelation = catalogTreeRelation;
    this.#contents = contents;
    this.#callback = callback;
  }

  execute(): void {
    const typeName: string = this.#contents[0].getType().getName();

    //check if there is a parent that is linking to the content to be deleted
    //do not allow to delete the content unless the number of parent is less or equals "1".
    ValueExpressionFactory.createFromFunction((): Array<any> =>
      this.#treeRelation.getParents(this.#contents[0]),
    ).loadValue((parents: Array<any>): void => {
      if (parents.length > 1 && this.#contents[0].getType().isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_CATEGORY)) {
        Logger.error("Delete action for a catalog item found multiple parents for '"
        + this.#contents[0].getName() + "' (e.g. " + parents[0].getName() + ")");
      }
      //there is exact one parent, so we unlink the node from it and delete it
      else {
        var repository: ContentRepository = this.#contents[0].getRepository();
        var deletions = [];
        var parentsToDelete = [];
        var modifications = [];

        for (const source of this.#contents as Content[]) {
          //check only the relation of categories
          if (source.getType().isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_CATEGORY)) {
            //unlink the category from the parent
            if (CatalogTreeRelationHelper.validateCheckoutState([parents[0]])) {
              modifications.push(CatalogTreeRelationHelper.removeCategoryChild(parents[0], source));
              //remember the folder for deletion
              parentsToDelete.push(source.getParent());
              deletions.push(source);
            }
          } else {
            deletions.push(source);
          }
        }
      }

      //finally delete the content
      if (deletions.length > 0) {
        ValueExpressionFactory.createFromFunction((): boolean => {
          for (const deletedContent of deletions as Content[]) {
            if (deletedContent.isCheckedOutByCurrentSession()) {
              deletedContent.revert();
              return undefined;
            }
            editorContext._.getWorkAreaTabManager().closeTab(deletedContent);
          }
          return true;
        }).loadValue((): void =>
          repository.deleteAll(deletions, (result: DeleteResult): void => {
            //update checkin state
            for (const modified of modifications as Content[]) {
              if (modified && !modified.isDeleted() && modified.isCheckedOut()) {
                modified.checkIn();
              }
            }
            //delete the parent folder too
            if (parentsToDelete.length > 0) {
              repository.deleteAll(parentsToDelete, (parentDeletionResult: DeleteResult): void => {
                //we pass the result of the children here
                this.#callback(result);
              });
            } else {
              this.#callback(result);
            }
          }),
        );

      }
    });
  }
}

export default CatalogDelete;
