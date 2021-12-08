import BulkUndeleteMethod from "@coremedia/studio-client.cap-rest-client-impl/content/impl/BulkUndeleteMethod";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import BulkOperationResult from "@coremedia/studio-client.cap-rest-client/content/results/BulkOperationResult";
import SearchParameters from "@coremedia/studio-client.cap-rest-client/content/search/SearchParameters";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import MessageBoxUtil from "@coremedia/studio-client.main.editor-components/sdk/util/MessageBoxUtil";
import StringUtil from "@jangaroo/ext-ts/String";
import { bind } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogStudioPlugin_properties from "../CatalogStudioPlugin_properties";
import CatalogTreeRelation from "./CatalogTreeRelation";
import CatalogTreeRelationHelper from "./CatalogTreeRelationHelper";

class CatalogUndelete {

  #treeRelation: CatalogTreeRelation = null;

  #contents: Array<any> = null;

  #callback: AnyFunction = null;

  constructor(catalogTreeRelation: CatalogTreeRelation, contents: Array<any>, callback?: AnyFunction) {
    this.#treeRelation = catalogTreeRelation;
    this.#contents = contents;
    this.#callback = callback;
  }

  execute(): void {
    new BulkUndeleteMethod(this.#contents[0].getRepository(), this.#contents, bind(this, this.#updateRestoredItems)).execute();
  }

  /**
   * Restores the linking for every restored content.
   * @param result the result of the undelete operation
   */
  #updateRestoredItems(result: BulkOperationResult): void {
    if (result.successful) {
      for (const content of this.#contents as Content[]) {
        this.#findParentAndRestoreLink(content);
      }
    }

    if (this.#callback) {
      this.#callback.call(null, result);
    }
  }

  /**
   * Looks up if there is a category in the same folder.
   * If not, the product is opened so that the user can link it manually.
   * Since there is a validator, the user will see what the problem is.
   */
  //TODO restoring check-in/out states
  #findParentAndRestoreLink(catalogItem: Content): void {
    ValueExpressionFactory.createFromFunction((): Content =>
      this.#findUndeletedParent(catalogItem),
    ).loadValue((category: Content): void => {
      //no category found?
      if (category === null) {
        const path = this.#getParentFolder(catalogItem).getPath();

        //then try to find a deleted parent that matches
        this.#findDeletedParent(catalogItem, path, (deletedParent: Content): void => {
          if (deletedParent == null) {
            this.#executeErrorHandling(catalogItem);
          } else {
            new BulkUndeleteMethod(catalogItem.getRepository(), [deletedParent], (result: BulkOperationResult): void => {
              if (result.successful) {
                //restore the linking to the catalog, maybe the link still exists for the restored
                //content but the method takes care of it and filters duplicates
                CatalogTreeRelationHelper.addCategoryChild(deletedParent, catalogItem);

                //we only restored the deleted parent until now, but we finally have to restore the linking for it too.
                //we do this by invoking all recursively
                this.#findParentAndRestoreLink(deletedParent);
              } else {
                this.#executeErrorHandling(catalogItem);
              }
            }).execute();
          }
        });
      } else {
        //restore the linking to the catalog, maybe the link still exists for the restored
        //content but the method takes care of it and filters duplicates
        CatalogTreeRelationHelper.addCategoryChild(category, catalogItem);
      }
    });
  }

  /**
   * The default error handling for restoring errors is to open the document.
   * This way, the user can restore the linking but also gets an hint about the problem.
   * @param content the content that has been restored, but could not been linked to a parent.
   */
  #executeErrorHandling(content: Content): void {
    editorContext._.getContentTabManager().openDocument(content);
    const title = CatalogStudioPlugin_properties.catalog_undelete_err_title;
    const message = StringUtil.format(CatalogStudioPlugin_properties.catalog_undelete_err_message, content.getName());
    MessageBoxUtil.showInfo(title, message);
  }

  /**
   * Executes a search query to find a deleted category that last path matches the expected parent category path
   * @param catalogItem the catalog item to find the deleted parent category for
   * @param matchingPath the path where the category is expected
   * @param callback the callback called with the deleted category or null if no such value was found
   */
  #findDeletedParent(catalogItem: Content, matchingPath: string, callback: AnyFunction): void {
    const site = editorContext._.getSitesService().getSiteFor(catalogItem);
    //http://localhost:40080/api/content/search?query=&contentType=Document_&folder=content%2F1&orderBy=type%20desc&orderBy=name%20desc&includeSubfolders=true&filterQuery=(status%3A3)&limit=-1&includeSubtypes=true&_dc=1442318843685
    const params = Object.setPrototypeOf({}, SearchParameters.prototype);
    params.folder = site.getSiteRootFolder().getUriPath();
    params.includeSubfolders = true;
    params.contentType = [CatalogTreeRelation.CONTENT_TYPE_CATEGORY];
    params.filterQuery = ["isdeleted:true"];
    params.query = "";

    const searchService = editorContext._.getSession().getConnection().getContentRepository().getSearchService();
    const result = searchService.search(params);
    result.load((): void => {
      const hits = result.getHits();
      for (const hit of hits as Content[]) {
        if (hit.isDeleted() && !hit.isDestroyed() && hit.getLastParent() != null) {
          const pathInfo = this.#getPathInfo(hit);
          if (pathInfo !== null && pathInfo === matchingPath) {
            callback.call(null, hit);
            return;
          }
        }
      }
      callback.call(null, null);
    });
  }

  /**
   * Returns the full path of the deleted content
   * @param deleted the deleted content
   * @return the formatted path
   */
  #getPathInfo(deleted: Content): string {
    const pathArray = [];
    let currentContent = deleted.getLastParent();
    while (currentContent && currentContent.isRoot() === false) {
      if (currentContent.isDestroyed()) {
        return null;
      }
      const name = currentContent.getName();
      pathArray.push(name);
      if (currentContent.isDeleted()) {
        currentContent = currentContent.getLastParent();
      } else {
        currentContent = currentContent.getParent();
      }
    }
    pathArray.reverse();
    return "/" + pathArray.join("/");
  }

  /**
   * Returns the first document for the given type of the folder of the given catalog item.
   * @param catalogItem the un-deleted catalog item
   * @return the parent category or null if no such category could be resolved.
   */
  #findUndeletedParent(catalogItem: Content): Content {
    const type = catalogItem.getType().getName();

    if (type == CatalogTreeRelation.CONTENT_TYPE_PRODUCT) {
      //the restored product still has the category link, so we only have to check if
      //one of the parent categories is not deleted too
      const categories = this.#treeRelation.getParents(catalogItem);
      for (const parent of categories as Content[]) {
        if (!parent.isDeleted()) {
          return parent;
        }
      }
    }

    const children = this.#getParentFolder(catalogItem).getChildDocuments();
    for (const child of children as Content[]) {
      if (!child.isLoaded()) {
        child.load();
        return undefined;
      }

      if (!child.getType()) {
        return undefined;
      }

      if (!child.getPath()) {
        return undefined;
      }

      if (child.getType().getName() == CatalogTreeRelation.CONTENT_TYPE_CATEGORY) {
        return child;
      }
    }
    return null;
  }

  /**
   * Returns the parent folder depending on the content type: for a product or category
   * @param content the content to retrieve the parent folder for
   */
  #getParentFolder(content: Content): Content {
    const type = content.getType().getName();
    if (type == CatalogTreeRelation.CONTENT_TYPE_CATEGORY) {
      return content.getParent().getParent();
    } else if (type == CatalogTreeRelation.CONTENT_TYPE_PRODUCT) {
      return content.getParent();
    }
    return null;
  }
}

export default CatalogUndelete;
