import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import AbstractTreeRelation from "@coremedia/studio-client.client-core/data/AbstractTreeRelation";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { as, cast, is } from "@jangaroo/runtime";
import augmentationService from "../augmentation/augmentationService";
import categoryTreeRelation from "./categoryTreeRelation";

class AugmentedCategoryTreeRelationImpl extends AbstractTreeRelation {

  override getChildrenOf(node: any): Array<any> {
    //TODO: currently this method is not used by page grid which is interested only in the parent relation.
    return null;
  }

  override getParentUnchecked(node: any): any {
    //first check if the node is a catalog object
    if (is(node, CatalogObject)) {
      return this.#getParentUncheckedForCatalogObject(as(node, CatalogObject));
    }

    //now check if the node is a content
    if (is(node, Content)) {
      return this.#getParentUncheckedForContent(as(node, Content));
    }
  }

  #getParentUncheckedForContent(content: Content) {
    //we need to check if the content is a site root document...
    const siteId = editorContext._.getSitesService().getSiteIdFor(content);
    if (siteId === undefined) {
      return undefined;
    } else if (siteId === null) {
      return null;
    }

    const siteRootDocument = editorContext._.getSitesService().getSiteRootDocument(siteId);
    //...if so the parent is null which means the parent hierarchy ends here.
    if (content === siteRootDocument) {
      return null;
    }

    const catalogObject = augmentationService.getCatalogObject(content);

    if (!catalogObject) {
      return catalogObject;
    }

    const product = as(catalogObject, Product);
    if (product) {
      const productCategory = product.getCategory();
      if (!productCategory) {
        return productCategory;
      }
      return this.getParentUnchecked(productCategory);
    }

    const category = cast(Category, categoryTreeRelation.getParentUnchecked(catalogObject));
    if (category === null) {
      //this is a root category.
      //we define the parent of the root category document as the site root document.
      //TODO: this is not clean as site root document is strictly not augmenting a category.
      return siteRootDocument;
    } else if (category) {
      const augmentedCategory = augmentationService.getContent(category);
      if (augmentedCategory === undefined) {
        return undefined;
      }
      if (augmentedCategory === null) {
        return this.getParentUnchecked(category);
      }
      if (augmentedCategory) {
        return augmentedCategory;
      }
    }
  }

  #getParentUncheckedForCatalogObject(catalogObject: CatalogObject) {
    const augmentingContent = augmentationService.getContent(catalogObject);
    //if the catalog object is augmented...
    if (augmentingContent) {
      //then it's it.
      return augmentingContent;
    } else {
      //otherwise continue with the parent
      return this.getParentUnchecked(categoryTreeRelation.getParentUnchecked(catalogObject));
    }
  }
}

export default AugmentedCategoryTreeRelationImpl;
