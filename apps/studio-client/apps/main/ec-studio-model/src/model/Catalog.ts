import CatalogObject from "./CatalogObject";
import Category from "./Category";

abstract class Catalog extends CatalogObject {

  abstract getRootCategory(): Category;

  abstract isDefault(): boolean;
}

export default Catalog;
