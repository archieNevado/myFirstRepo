import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import ProductVariant from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductVariant";
import AbstractTreeRelation from "@coremedia/studio-client.client-core/data/AbstractTreeRelation";
import { as, is } from "@jangaroo/runtime";

class CategoryTreeRelationImpl extends AbstractTreeRelation {

  override getChildrenOf(node: any): Array<any> {
    const category = as(node, Category);
    if (!category) {
      return undefined;
    } else {
      return category.getSubCategories();
    }
  }

  override getParentUnchecked(node: any): any {
    if (is(node, CatalogObject)) {
      //when parent (for category and product variant) ...
      if (is(node, Category) || is(node, ProductVariant)) {
        //... then take it.
        return node.getParent();
        //when otherwise there is category (for product)...
      } else if (is(node, Product)) {
        //... then take the category.
        return node.getCategory();
      }
    } else {
      return undefined;
    }
  }
}

export default CategoryTreeRelationImpl;
