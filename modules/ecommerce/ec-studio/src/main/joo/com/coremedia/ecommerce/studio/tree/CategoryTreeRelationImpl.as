package com.coremedia.ecommerce.studio.tree {
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ui.data.AbstractTreeRelation;

internal class CategoryTreeRelationImpl extends AbstractTreeRelation {

  override public function getChildrenOf(node:Object):Array {
    var category:Category = node as Category;
    if (!category) {
      return undefined;
    } else {
      return category.getSubCategories();
    }
  }

  override public function getParentUnchecked(node:Object):Object {
    var category:Category = node as Category;
    if (!category) {
      return undefined;
    } else {
      return category.getParent();
    }
  }
}
}