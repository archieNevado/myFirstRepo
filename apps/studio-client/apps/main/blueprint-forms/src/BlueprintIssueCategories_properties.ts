
/**
 * Interface values for ResourceBundle "BlueprintIssueCategories".
 * @see BlueprintIssueCategories_properties#INSTANCE
 */
interface BlueprintIssueCategories_properties {

  /**
   *Default names of Issue Categories
   *The property keys must consist of a prefix ("Search_Filter_issue_categories_"), followed by the name of the category.
   */
  Search_Filter_issue_categories_all: string;
  Search_Filter_issue_categories_localization: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "BlueprintIssueCategories".
 * @see BlueprintIssueCategories_properties
 */
const BlueprintIssueCategories_properties: BlueprintIssueCategories_properties = {
  Search_Filter_issue_categories_all: "All Categories",
  Search_Filter_issue_categories_localization: "Localization",
};

export default BlueprintIssueCategories_properties;
