import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";

/**
 * Service to deal with augmented catalog objects. Augmented catalog objects
 * have a content proxy object holding its external id.
 */
abstract class IAugmentationService {

  /**
   * Return the content object holding the catalog object's external id (if any)
   */
  abstract getContent(catalogObject: CatalogObject): Content;

  /**
   * Lookup the catalog object with the content's external id
   */
  abstract getCatalogObject(content: Content): CatalogObject;

  /**
   * Lookup the catalog object with the content's external id if it is a category
   */
  abstract getCategory(content: Content): Category;

  /**
   * Lookup the catalog object with the content's external id if it is a product
   */
  abstract getProduct(content: Content): Product;
}

export default IAugmentationService;
