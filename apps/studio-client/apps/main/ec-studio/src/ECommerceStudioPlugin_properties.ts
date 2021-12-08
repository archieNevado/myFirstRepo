import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "ECommerceStudioPlugin".
 * @see ECommerceStudioPlugin_properties#INSTANCE
 */
interface ECommerceStudioPlugin_properties {

  TreeView_catalog: string;
  Categorys_Products_Link_empty_text: string;
  Products_Link_empty_text: string;
  CatalogView_invalid_store_text: string;
  CatalogView_empty_text: string;
  CatalogView_multiCatalog_emptySearch_text: string;
  CatalogView_spots_selection_empty_text: string;
  CatalogView_delete_button: string;
  Catalog_DragDrop_multiSelect_text: string;
  id_header: string;
  catalog_header: string;
  description_header: string;
  CMAbstractCategory_text: string;
  Category_label: string;
  Catalog_replace_reference_text: string;
  Catalog_replace_reference_title: string;
  Catalog_replace_reference_button: string;
  Catalog_replace_reference_button_confirm: string;
  Catalog_replace_reference_button_abort: string;
  Product_label: string;
  ProductVariant_label: string;
  MarketingSpot_label: string;
  Marketing_label: string;
  CatalogTypeSelector_empty_text: string;
  StoreTree_marketing_root: string;
  StoreTree_root_category: string;
  commerceConnectionError_title: string;
  commerceConnectionError_message: string;
  commerceCatalogError_title: string;
  commerceCatalogError_message: string;
  commerceCatalogNotFoundError_title: string;
  commerceCatalogNotFoundError_message: string;
  commerceCatalogItemNotFoundError_title: string;
  commerceCatalogItemNotFoundError_message: string;
  commerceUnauthorizedError_title: string;
  commerceUnauthorizedError_message: string;
  commerceAugmentationError_title: string;
  commerceAugmentationError_message: string;
  commerceStoreItemNotFoundError_message: string;
  HeaderToolbar_commerceStore_label: string;
/**
 * Library
 */
  CollectionView_catalogRepositoryToolbar_label: string;
  CollectionView_catalogSearchToolbar_label: string;
  CollectionView_search_filter_combo_emptyText: string;
  CollectionView_search_filter_resetAll_text: string;
  CollectionView_search_filter_empty_title: string;
  CollectionView_search_filter_empty_text: string;
  CollectionView_search_filter_empty_facet_text: string;
  CollectionView_search_filter_allAdded_text: string;
  CollectionView_search_no_filter_empty_title: string;
  CollectionView_search_no_filter_empty_text: string;
  CollectionView_search_no_filter_allAdded_text: string;
  saveSearch_invalidFacets_title: string;
  saveSearch_invalidFacets_text: string;
  saveSearch_invalidFacets_delete_btn_text: string;
  saveSearch_invalidFacets_clear_btn_text: string;
  saveSearch_invalidCategory_title: string;
  saveSearch_invalidCategory_text: string;
  saveSearch_invalidCategory_delete_btn_text: string;
/**
 * Localization properties for Create Actions
 */
  Action_createCategory_text: string;
  Action_createCategory_tooltip: string;
  Action_createCategory_error_title: string;
  Action_createCategory_error_message: string;
  Action_createCategory_error_general_message: string;
  Action_createProduct_text: string;
  Action_createProduct_tooltip: string;
  Action_createProduct_error_title: string;
  Action_createProduct_error_message: string;
  Action_createProduct_error_general_message: string;
/**
 *Icons
 */
  Category_icon: string;
  AugmentedCategory_icon: string;
  Product_icon: string;
  AugmentedProduct_icon: string;
  ProductVariant_icon: string;
  MarketingSpot_icon: string;
  Marketing_icon: string;
  Store_icon: string;
  Action_createCategory_icon: string;
  Action_createProduct_icon: string;
/**
 *Validator messages
 */
  ContentValidator_CMProduct_productCode_RegExpValidator_text: string;
/**
 * Preferences
 */
  EcCatalogPreferences_tab_title: string;
  EcCatalogPreferences_catalog_title: string;
  EcCatalogPreferences_show_catalog_items: string;
  EcCatalogPreferences_sort_children: string;
/**
 * Show in tree
 */
  Catalog_show_in_tree_fails_title: string;
  Catalog_show_in_content_tree_fails_for_Content: string;
  Catalog_show_in_catalog_tree_fails_for_Content: string;
  Catalog_show_in_catalog_tree_fails_for_CatalogObject: string;
  Catalog_show_preferences_button_text: string;
  Catalog_show_switch_site_button_text: string;
  PreviewVariant_commerceHeadlessPreview_name: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ECommerceStudioPlugin".
 * @see ECommerceStudioPlugin_properties
 */
const ECommerceStudioPlugin_properties: ECommerceStudioPlugin_properties = {
  TreeView_catalog: "e-Commerce",
  Categorys_Products_Link_empty_text: "Add Categories or Products by dragging them from the Library here.",
  Products_Link_empty_text: "Add Products by dragging them from the Library here.",
  CatalogView_invalid_store_text: "Please select a site with valid e-Commerce catalog.",
  CatalogView_empty_text: "This category is empty.",
  CatalogView_multiCatalog_emptySearch_text: "No search result, possibly because the search is limited to the catalog '{0}'.\nTry searching in a different catalog.",
  CatalogView_spots_selection_empty_text: "No e-Marketing spots found.",
  CatalogView_delete_button: "Delete Entry.",
  Catalog_DragDrop_multiSelect_text: "{0} selected catalog items",
  Catalog_replace_reference_text: "The current reference to the item in the commerce <br/> catalog will be removed and you can select a new one. <br/> <br/>  Every usage of this content item will then link and refer to the new item in the commerce catalog. <br/><br/> Do you want to continue?",
  Catalog_replace_reference_title: "Change Commerce Reference",
  Catalog_replace_reference_button: "Change Reference",
  Catalog_replace_reference_button_confirm: "Yes, Change Reference",
  Catalog_replace_reference_button_abort: "Cancel",
  id_header: "ID",
  catalog_header: "Catalog",
  description_header: "Description",
  CMAbstractCategory_text: "Abstract Category",
  Category_label: "Category",
  Product_label: "Product",
  ProductVariant_label: "Product Variant",
  MarketingSpot_label: "e-Marketing Spot",
  Marketing_label: "e-Marketing Spots",
  CatalogTypeSelector_empty_text: "Catalog Type",
  StoreTree_marketing_root: "e-Marketing Spots",
  StoreTree_root_category: "Product Catalog",
  commerceConnectionError_title: "Connection Error",
  commerceConnectionError_message: "Could not connect to catalog ('{0}'). Please contact your system administrator if this problem occurs again.",
  commerceCatalogError_title: "Catalog Error",
  commerceCatalogError_message: "Catalog error occurred ('{0}'). Please contact your system administrator if this problem occurs again.",
  commerceCatalogNotFoundError_title: "Configuration Error",
  commerceCatalogNotFoundError_message: "Catalog error occurred ('{0}').",
  commerceCatalogItemNotFoundError_title: "Catalog Error",
  commerceCatalogItemNotFoundError_message: "Catalog item not found ('{0}').",
  commerceUnauthorizedError_title: "Authorization Error",
  commerceUnauthorizedError_message: "Could not authorize to catalog ('{0}'). Please contact your system administrator if this problem occurs again.",
  commerceAugmentationError_title: "Augmentation Error",
  commerceAugmentationError_message: "Cannot set default layouts. Root category is not augmented. Please augment the root category of your current catalog first.",
  commerceStoreItemNotFoundError_message: "Store item not initialized ('{0}').",
  HeaderToolbar_commerceStore_label: "Shop",
  CollectionView_catalogRepositoryToolbar_label: "Catalog Library",
  CollectionView_catalogSearchToolbar_label: "Catalog Search",
  CollectionView_search_filter_combo_emptyText: "Add filter",
  CollectionView_search_filter_resetAll_text: "Reset all",
  CollectionView_search_filter_empty_title: "Too many results?",
  CollectionView_search_filter_empty_text: "Start adding filters to refine your search.",
  CollectionView_search_filter_empty_facet_text: "Select value",
  CollectionView_search_filter_allAdded_text: "All filter have been added",
  CollectionView_search_no_filter_empty_title: "Filters unavailable",
  CollectionView_search_no_filter_empty_text: "Search refinement is not available in this category.",
  CollectionView_search_no_filter_allAdded_text: "Not available",
  saveSearch_invalidFacets_title: "Invalid Filter",
  saveSearch_invalidFacets_text: "The saved search could not be performed because some of the filters are invalid. To solve this, you can either remove the invalid filters or delete the saved search altogether.",
  saveSearch_invalidFacets_delete_btn_text: "Delete Search",
  saveSearch_invalidFacets_clear_btn_text: "Remove invalid filters",
  saveSearch_invalidCategory_title: "Cannot Show Category in the Library",
  saveSearch_invalidCategory_text: "This search folder does not exist anymore. Do you want to delete \"{0}\" from your favorites?",
  saveSearch_invalidCategory_delete_btn_text: "Yes, delete",
  Action_createCategory_text: "Create Category",
  Action_createCategory_tooltip: "Create Category",
  Action_createCategory_error_title: "Category creation failed",
  Action_createCategory_error_message: "Could not create the category. The parent category '{0}' is checked out by user '{1}'.",
  Action_createCategory_error_general_message: "Could not create the category. The parent category '{0}' is checked out by another user.",
  Action_createProduct_text: "Create Product",
  Action_createProduct_tooltip: "Create Product",
  Action_createProduct_error_title: "Product creation failed",
  Action_createProduct_error_message: "Could not create the new product. The parent category '{0}' is checked out by user '{1}'.",
  Action_createProduct_error_general_message: "Could not create the product. The parent category '{0}' is checked out by another user.",
  Category_icon: CoreIcons_properties.type_category,
  AugmentedCategory_icon: CoreIcons_properties.type_augmented_category,
  Product_icon: CoreIcons_properties.type_product,
  AugmentedProduct_icon: CoreIcons_properties.type_augmented_product,
  ProductVariant_icon: CoreIcons_properties.type_product_variant,
  MarketingSpot_icon: CoreIcons_properties.type_marketing_spot,
  Marketing_icon: CoreIcons_properties.type_marketing_spot,
  Store_icon: CoreIcons_properties.catalog,
  Action_createCategory_icon: CoreIcons_properties.create_type_category,
  Action_createProduct_icon: CoreIcons_properties.create_type_product,
  ContentValidator_CMProduct_productCode_RegExpValidator_text: "This field must not contain slashes, colons or whitespace characters.",
  EcCatalogPreferences_tab_title: "Product Catalog",
  EcCatalogPreferences_catalog_title: "Catalog",
  EcCatalogPreferences_show_catalog_items: "Show Products and Categories as Content",
  EcCatalogPreferences_sort_children: "Sort Categories by name (will deteriorate performance)",
  Catalog_show_in_tree_fails_title: "Cannot Show Content in the Library",
  Catalog_show_in_content_tree_fails_for_Content: "The selected content cannot not be shown in the Library with the current settings. Activate \"Show Products and Categories as Content\" in the preferences to show it.",
  Catalog_show_in_catalog_tree_fails_for_Content: "The selected content cannot not be shown in the Library with the current settings. To show it, either change the preferred Site or activate \"Show Products and Categories as Content\" in the preferences.",
  Catalog_show_in_catalog_tree_fails_for_CatalogObject: "The selected catalog item cannot not be shown in the Library with the current settings. To show it, change the preferred Site to '{0}'.",
  Catalog_show_preferences_button_text: "Open Preferences...",
  Catalog_show_switch_site_button_text: "Change Site",
  PreviewVariant_commerceHeadlessPreview_name: "JSON Preview",
};

export default ECommerceStudioPlugin_properties;
