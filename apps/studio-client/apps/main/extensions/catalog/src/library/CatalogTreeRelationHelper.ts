import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import ContentLocalizationUtil from "@coremedia/studio-client.cap-base-models/content/ContentLocalizationUtil";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import BulkOperationResult from "@coremedia/studio-client.cap-rest-client/content/results/BulkOperationResult";
import CopyResult from "@coremedia/studio-client.cap-rest-client/content/results/CopyResult";
import Logger from "@coremedia/studio-client.client-core-impl/logging/Logger";
import StringUtil from "@jangaroo/ext-ts/String";
import MessageBoxWindow from "@jangaroo/ext-ts/window/MessageBox";
import { as, is } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import CatalogStudioPlugin_properties from "../CatalogStudioPlugin_properties";

/**
 * Contains some catalog content actions and helper methods.
 */
class CatalogTreeRelationHelper {
  static readonly PROPERTY_CONTEXTS: string = "contexts";

  static readonly PROPERTY_CHILDREN: string = "children";

  static readonly CONTENT_TYPE_CATEGORY: string = CatalogHelper.CONTENT_TYPE_CM_CATEGORY;

  static readonly CONTENT_TYPE_PRODUCT: string = CatalogHelper.CONTENT_TYPE_CM_PRODUCT;

  static showCheckoutError(target: Content): void {
    const docType = ContentLocalizationUtil.localizeDocumentTypeName(target.getType().getName());
    const msg = StringUtil.format(CatalogStudioPlugin_properties.catalog_checkout_error_message, docType, target.getName());
    MessageBoxWindow.getInstance().alert(CatalogStudioPlugin_properties.catalog_checkout_error_title, msg);
  }

  /**
   * Copies the products into the given category, updates the linking afterwards.
   * @param sources the products to copy
   * @param newParent the parent category
   * @param callback the callback called once the process is finished.
   */
  static copyAndLinkProducts(sources: Array<any>, newParent: Content, callback: AnyFunction): void {
    const contentRepository = session._.getConnection().getContentRepository();
    contentRepository.copyRecursivelyTo(sources, newParent.getParent(), (result: CopyResult): void => {
      if (result.successful) {
        for (const item of result.results) {
          if (is(item.content, Content)) {
            const newProduct: Content = item.content;
            newProduct.getProperties().set(CatalogTreeRelationHelper.PROPERTY_CONTEXTS, [newParent]);
            newProduct.checkIn();
          }
        }
        if (callback) {
          callback();
        }
      } else {
        Logger.error("Failed to copy source into parent folder '" + newParent.getName() + "': " + result.error.errorName);
      }
    });
  }

  /**
   * Validates if the list of given content can be modified.
   */
  static validateCheckoutState(contents: Array<any>): boolean {
    for (const source of contents as Content[]) {
      if (source && source.isCheckedOutByOther()) {
        CatalogTreeRelationHelper.showCheckoutError(source);
        return false;
      }
    }
    return true;
  }

  /**
   * Filters the given contents for the given type
   * @param contents the contents to filter
   * @param type the type to filter for.
   */
  static filterForType(contents: Array<any>, type: string): Array<any> {
    const filtered = [];
    for (const content of contents as Content[]) {
      const typeName = content.getType().getName();
      if (typeName === type) {
        filtered.push(content);
      }
    }
    return filtered;
  }

  /**
   * Restores the original check-in/out state
   */
  static restoreCheckInOutState(checkedInContents: Array<any>): void {
    for (const c of checkedInContents as Content[]) {
      c.checkIn();
    }
  }

  /**
   * Stores all contents that are currently checked out
   */
  static storeCheckInOutState(contents: Array<any>): Array<any> {
    const checkedInContents = [];
    for (const parent of contents as Content[]) {
      if (!parent.isCheckedOut()) {
        checkedInContents.push(parent);
      }
    }
    return checkedInContents;
  }

  /**
   * Adds a category or product to the given category.
   * @param parentCategory the category to add the child to
   * @param child the child to add, may be a product or a category.
   * @return the content that has been modified to update the linking AND was not checked out before
   */
  static addCategoryChild(parentCategory: Content, child: Content): Content {
    let returnValue: Content = null;
    if (child.getType().isSubtypeOf(CatalogTreeRelationHelper.CONTENT_TYPE_CATEGORY)) {
      if (!parentCategory.isCheckedOut()) {
        returnValue = parentCategory;
      }
      const parentChildren = as(parentCategory.getProperties().get(CatalogTreeRelationHelper.PROPERTY_CHILDREN), Array).slice();
      parentChildren.push(child);
      parentCategory.getProperties().set(CatalogTreeRelationHelper.PROPERTY_CHILDREN, parentChildren);
    } else if (child.getType().isSubtypeOf(CatalogTreeRelationHelper.CONTENT_TYPE_PRODUCT)) {
      const contexts = as(child.getProperties().get(CatalogTreeRelationHelper.PROPERTY_CONTEXTS), Array).slice();
      if (contexts.indexOf(parentCategory) === -1) {
        if (!child.isCheckedOut()) {
          returnValue = child;
        }
        contexts.push(parentCategory);
      }
      child.getProperties().set(CatalogTreeRelationHelper.PROPERTY_CONTEXTS, contexts);
    }
    return returnValue;
  }

  /**
   * Removes a category or product to the given category.
   * @param parentCategory the category to remove the child from
   * @param child the child to remove, may be a product or a category.
   * @return the content that has been modified to update the linking AND was not checked out before
   */
  static removeCategoryChild(parentCategory: Content, child: Content): Content {
    let returnValue: Content = null;
    if (child.getType().isSubtypeOf(CatalogTreeRelationHelper.CONTENT_TYPE_CATEGORY)) {
      if (!parentCategory.isCheckedOut()) {
        returnValue = parentCategory;
      }
      const parentChildren = as(parentCategory.getProperties().get(CatalogTreeRelationHelper.PROPERTY_CHILDREN), Array).slice();
      while (parentChildren.indexOf(child) !== -1) {
        parentChildren.splice(parentChildren.indexOf(child), 1);
      }
      parentCategory.getProperties().set(CatalogTreeRelationHelper.PROPERTY_CHILDREN, parentChildren);
    } else if (child.getType().isSubtypeOf(CatalogTreeRelationHelper.CONTENT_TYPE_PRODUCT)) {
      const contexts: Array<any> = child.getProperties().get(CatalogTreeRelationHelper.PROPERTY_CONTEXTS).slice();
      if (contexts.indexOf(parentCategory) !== -1) {
        if (!child.isCheckedOut()) {
          returnValue = child;
        }
        contexts.splice(contexts.indexOf(parentCategory), 1);
      }
      child.getProperties().set(CatalogTreeRelationHelper.PROPERTY_CONTEXTS, contexts);
    }
    return returnValue;
  }

  /**
   * Updates the location of the dropped content. This ensures that products or categories that are moved
   * to another category are also moved to the corresponding folder.
   * This is mandatory to resolve the unique name problem.
   * @param sources the sources that have been dropped to a category
   * @param target the target categories the sources have been dropped to
   */
  static updateLocation(sources: Array<any>, target: Content, callback?: AnyFunction): void {
    const repository = session._.getConnection().getContentRepository();
    repository.moveTo(sources, target.getParent(), (result: BulkOperationResult): void => {
      if (result.error) {
        Logger.error("Error copying products or categories to folder " + target.getParent().getPath() + ": "
        + result.error.errorCode + "/" + result.error.errorName);
      }
      if (callback) {
        callback(result);
      }
    });
  }
}

export default CatalogTreeRelationHelper;
