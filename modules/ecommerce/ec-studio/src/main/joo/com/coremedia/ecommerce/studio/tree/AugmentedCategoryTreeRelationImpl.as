package com.coremedia.ecommerce.studio.tree {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.augmentation.augmentationService;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ui.data.AbstractTreeRelation;

internal class AugmentedCategoryTreeRelationImpl extends AbstractTreeRelation {

  override public function getChildrenOf(node:Object):Array {
    //TODO: currently this method is not used by page grid which is interested only in the parent relation.
    return null;
  }

  override public function getParentUnchecked(node:Object):Object {
    var nodeCategory:Category = node as Category;
    if (nodeCategory) {
      var augmentedNodeCategory:Content = augmentationService.getContent(nodeCategory);
      if (augmentedNodeCategory) {
        return augmentedNodeCategory;
      } else {
        return getParentUnchecked(categoryTreeRelation.getParentUnchecked(nodeCategory));
      }
    }

    var content:Content = node as Content;
    //we need to check if the content is a site root document...
    var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
    if (siteId === undefined) {
      return undefined;
    } else if (siteId === null) {
      return null;
    }
    var siteRootDocument:Content = editorContext.getSitesService().getSiteRootDocument(siteId);
    //...if so the parent is null which means the parent hierarchy ends here.
    if (content === siteRootDocument) {
      return null;
    }
    var category:Category = augmentationService.getCategory(content);
    while (true) {
      switch (category) {
        case undefined:
          return undefined;
        case null:
          return null;
      }
      category = Category(categoryTreeRelation.getParentUnchecked(category));
      if (category === null) {
        //this is a root category.
        //we define the parent of the root category document as the site root document.
        //TODO: this is not clean as site root document is strictly not augmenting a category.
        return siteRootDocument;
      } else if (category) {
        var augmentedCategory:Content = augmentationService.getContent(category);
        if (augmentedCategory === undefined) {
          return undefined;
        }
        if (augmentedCategory) {
          return augmentedCategory;
        }
      }

    }
  }
}
}