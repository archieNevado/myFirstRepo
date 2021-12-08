import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "CatalogStudioPlugin".
 * @see CatalogStudioPlugin_properties#INSTANCE
 */
interface CatalogStudioPlugin_properties {

/**
 *Product
 */
  CMProduct_text: string;
  CMProduct_productName_text: string;
  CMProduct_productName_emptyText: string;
  CMProduct_productName_toolTip: string;
  CMProduct_productCode_text: string;
  CMProduct_productCode_emptyText: string;
  CMProduct_teaserText_text: string;
  CMProduct_teaserText_emptyText: string;
  CMProduct_teaserText_toolTip: string;
  CMProduct_detailText_text: string;
  CMProduct_detailText_emptyText: string;
  CMProduct_detailText_toolTip: string;
  CMProduct_downloads_text: string;
  CMProduct_downloads_emptyText: string;
  CMProduct_downloads_toolTip: string;
  CMProduct_shortDescription_text: string;
  CMProduct_shortDescription_emptyText: string;
  CMProduct_shortDescription_toolTip: string;
  CMProduct_longDescription_text: string;
  CMProduct_longDescription_emptyText: string;
  CMProduct_longDescription_toolTip: string;
  CMProduct_contexts_text: string;
  CMProduct_contexts_emptyText: string;
  CMProduct_contexts_toolTip: string;
  CMProduct_parentChannel_text: string;
  CMProduct_parentChannel_emptyText: string;
/**
 *Category
 */
  CMCategory_title_emptyText: string;
  CMCategory_text: string;
  CMCategory_products_text: string;
  CMCategory_title_text: string;
  CMCategory_title_toolTip: string;
  CMCategory_categoryName_text: string;
  CMCategory_categoryName_emptyText: string;
  CMCategory_displayName_text: string;
  CMCategory_teaserText_text: string;
  CMCategory_teaserText_emptyText: string;
  CMCategory_shortDescription_text: string;
  CMCategory_shortDescription_emptyText: string;
  CMCategory_detailText_text: string;
  CMCategory_detailText_emptyText: string;
  CMCategory_parentChannel_text: string;
  CMCategory_parentChannel_emptyText: string;
/**
 *Search Combo
 */
  Catalog_show_all: string;
/**
 * Referrer Lists
 */
  CMCategory_no_products_for_picture: string;
  CMCategory_no_parent: string;
  CMCategory_no_products: string;
/**
 *Tabs
 */
  Tab_catalog_structure_title: string;
  Corporate_catalog_title: string;
  ProductAssets_Downloads_label: string;
/**
 *Property Groups
 */
  PropertyGroup_Assets_label: string;
  PropertyGroup_SubCategories_label: string;
  PropertyGroup_RelatedProducts_label: string;
/**
 * Show in tree
 */
  Catalog_show_search_fails_for_Content: string;
  All_icon: string;
  CMCategory_icon: string;
  CMProduct_icon: string;
/**
 * Tree Relation message
 */
  catalog_checkout_error_title: string;
  catalog_checkout_error_message: string;
  catalog_copy_or_link_title: string;
  catalog_copy_or_link_message: string;
  catalog_copy_btn_text: string;
  catalog_link_btn_text: string;
/**
 * Undelete error message
 */
  catalog_undelete_err_title: string;
  catalog_undelete_err_message: string;
/**
 * Filters
 */
  Filter_LostAndFound_text: string;
  Filter_LostAndFound_checkbox: string;
/**
 * Collection View
 */
  catalog_lists_product_code_column: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "CatalogStudioPlugin".
 * @see CatalogStudioPlugin_properties
 */
const CatalogStudioPlugin_properties: CatalogStudioPlugin_properties = {
  CMProduct_text: "Product",
  CMProduct_productName_text: "Product Title",
  CMProduct_productName_emptyText: "Enter the title of the product here",
  CMProduct_productName_toolTip: "Title of the product",
  CMProduct_productCode_text: "Product Code",
  CMProduct_productCode_emptyText: "Enter the code of the product here.",
  CMProduct_teaserText_text: "Short Description",
  CMProduct_teaserText_emptyText: "Enter a short description of the product here",
  CMProduct_teaserText_toolTip: "Short description of the product",
  CMProduct_detailText_text: "Long Description",
  CMProduct_detailText_emptyText: "Enter a long description of the product here",
  CMProduct_detailText_toolTip: "Long description of the product",
  CMProduct_downloads_text: "Downloads",
  CMProduct_downloads_emptyText: "Assign downloads by dragging from the Library here.",
  CMProduct_downloads_toolTip: "Downloads for the product",
  CMProduct_shortDescription_text: "Short Description",
  CMProduct_shortDescription_emptyText: "Enter a short description of the product here.",
  CMProduct_shortDescription_toolTip: "Short description of the product",
  CMProduct_longDescription_text: "Long Description",
  CMProduct_longDescription_emptyText: "Enter a long description of the product here",
  CMProduct_longDescription_toolTip: "Long description of the product",
  CMProduct_contexts_text: "Categories",
  CMProduct_contexts_emptyText: "Assign product categories by dragging from the Library here.",
  CMProduct_contexts_toolTip: "Product Categories",
  CMProduct_parentChannel_text: "Parent Categories",
  CMProduct_parentChannel_emptyText: "Add categories by dragging from the Library here.",
  CMCategory_title_emptyText: "Enter the title of the category here.",
  CMCategory_text: "Category",
  CMCategory_products_text: "Products",
  CMCategory_title_text: "Category Title",
  CMCategory_title_toolTip: "The title of the category",
  CMCategory_categoryName_text: "Category Name",
  CMCategory_categoryName_emptyText: "Enter the name of the category here.",
  CMCategory_displayName_text: "Display Name",
  CMCategory_teaserText_text: "Short Description",
  CMCategory_teaserText_emptyText: "Enter the short description of the category here.",
  CMCategory_shortDescription_text: "Short Description",
  CMCategory_shortDescription_emptyText: "Enter the short description of the category here.",
  CMCategory_detailText_text: "Long Description",
  CMCategory_detailText_emptyText: "Enter the long description of the category here.",
  CMCategory_parentChannel_text: "Parent Category",
  CMCategory_parentChannel_emptyText: "Add a category by dragging it from the Library here.",
  Catalog_show_all: "All",
  CMCategory_no_products_for_picture: "This picture is not linked from any product.",
  CMCategory_no_parent: "This category is a top category and has no parent.",
  CMCategory_no_products: "This category has no products.",
  Tab_catalog_structure_title: "Catalog Structure",
  Corporate_catalog_title: "Catalog",
  ProductAssets_Downloads_label: "Downloads",
  PropertyGroup_Assets_label: "Assets",
  PropertyGroup_SubCategories_label: "Child Categories",
  PropertyGroup_RelatedProducts_label: "Related Products",
  Catalog_show_search_fails_for_Content: "The selected content cannot not be shown in the Library with the current settings. To show it, change the preferred Site in the preferences.",
  All_icon: CoreIcons_properties.type_object,
  CMCategory_icon: CoreIcons_properties.type_category,
  CMProduct_icon: CoreIcons_properties.type_product,
  catalog_checkout_error_title: "Error Editing Catalog",
  catalog_checkout_error_message: "Failed to execute action because {0} '{1}' is checked out by another user.",
  catalog_copy_or_link_title: "Create Link or Copy Content Item?",
  catalog_copy_or_link_message: "The clipboard selection has been copied from another category. Should it be copied or linked into category '{0}'?",
  catalog_copy_btn_text: "Copy",
  catalog_link_btn_text: "Link",
  catalog_undelete_err_title: "Error Restoring Content",
  catalog_undelete_err_message: "Failed to restore the linking of '{0}'. The content has been opened so that the linking can be restored manually.",
  Filter_LostAndFound_text: "Orphaned Catalog Items",
  Filter_LostAndFound_checkbox: "Only Show Orphaned Catalog Items",
  catalog_lists_product_code_column: "Product Code",
};

export default CatalogStudioPlugin_properties;
