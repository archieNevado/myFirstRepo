
/**
 * Interface values for ResourceBundle "CatalogValidator".
 * @see CatalogValidator_properties#INSTANCE
 */
interface CatalogValidator_properties {

  Validator_categoryIsNotLinkedInCatalog_text: string;
  Validator_productIsNotLinkedInCatalog_text: string;
  Validator_category_loop_text: string;
  Validator_duplicate_category_parent_text: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "CatalogValidator".
 * @see CatalogValidator_properties
 */
const CatalogValidator_properties: CatalogValidator_properties = {
  Validator_categoryIsNotLinkedInCatalog_text: "This category is not linked in the product catalog and can only be found through the library search.",
  Validator_productIsNotLinkedInCatalog_text: "This product is not linked in the product catalog and can only be found through the library search.",
  Validator_category_loop_text: "The category hierarchy contains a loop.",
  Validator_duplicate_category_parent_text: "The category is linked by more than one parent category.",
};

export default CatalogValidator_properties;
