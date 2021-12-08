import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogTreeRelation from "./CatalogTreeRelation";
import CatalogTreeRelationHelper from "./CatalogTreeRelationHelper";

class CatalogMove {
  #treeRelation: CatalogTreeRelation = null;

  #sources: Array<any> = null;

  #newParent: Content = null;

  #callback: AnyFunction = null;

  constructor(catalogTreeRelation: CatalogTreeRelation, sources: Array<any>, newParent: Content, callback?: AnyFunction) {
    this.#treeRelation = catalogTreeRelation;
    this.#sources = sources;
    this.#newParent = newParent;
    this.#callback = callback;
  }

  execute(): void {
    const sourceParent = this.#treeRelation.getParent(this.#sources[0]);
    if (CatalogTreeRelationHelper.validateCheckoutState(this.#sources.concat(sourceParent).concat(this.#newParent))) {
      const modifications = [];

      // update linking
      for (const source of this.#sources as Content[]) {
        modifications.push(CatalogTreeRelationHelper.addCategoryChild(this.#newParent, source));
        modifications.push(CatalogTreeRelationHelper.removeCategoryChild(sourceParent, source));
      }

      //move the content, we use the callback to check the check-in state afterwards
      CatalogTreeRelationHelper.updateLocation(this.#sources, this.#newParent, (): void => {
        //update checkin state
        for (const modified of modifications as Content[]) {
          if (modified && modified.isCheckedOut()) {
            modified.checkIn();
          }
        }
        if (this.#callback) {
          this.#callback();
        }
      });
    }
  }

}

export default CatalogMove;
