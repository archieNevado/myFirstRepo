
/**
 * Interface values for ResourceBundle "BlueprintViewtypes".
 * @see BlueprintViewtypes_properties#INSTANCE
 */
interface BlueprintViewtypes_properties {

  Combo_viewtype_StandardAnzeigevariante: string;
  Combo_viewtype_Defaultvariant: string;
  Combo_viewtype_no_image: string;
/**
 * Brick Layouts
 */
  square_text: string;
  square_description: string;
  portrait_text: string;
  portrait_description: string;
  landscape_text: string;
  landscape_description: string;
  carousel_text: string;
  carousel_description: string;
  "left-right_text": string;
  "left-right_description": string;
  "full-details_text": string;
  "full-details_description": string;
  hero_text: string;
  hero_description: string;
  search_text: string;
  search_description: string;
  shoppable_text: string;
  shoppable_description: string;

/*
 * Custom Layouts in Themes
 */
  "50-50-portrait_text": string;
  "50-50-portrait_description": string;
  "50-50-landscape_text": string;
  "50-50-landscape_description": string;
  "language-chooser_text": string;
  "language-chooser_description": string;
  "category-header_text": string;
  "category-header_description": string;
  "category-list-categories_text": string;
  "category-list-categories_description": string;
  "category-list-products_text": string;
  "category-list-products_description": string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "BlueprintViewtypes".
 * @see BlueprintViewtypes_properties
 */
const BlueprintViewtypes_properties: BlueprintViewtypes_properties = {
  Combo_viewtype_StandardAnzeigevariante: "Default Viewtype",
  Combo_viewtype_Defaultvariant: "Default Viewtype",
  Combo_viewtype_no_image: "(no image information available)",
  square_text: "Square Banner",
  square_description: "Display content in square view (3 in a row).",
  portrait_text: "Portrait Banner",
  portrait_description: "Displays content in portrait view (5 in a row).",
  landscape_text: "Landscape Banner",
  landscape_description: "Displays content in landscape view (3 in a row).",
  carousel_text: "Carousel Banner",
  carousel_description: "Displays content in a carousel that can show multiple banners at once.",
  "left-right_text": "Left-Right Banner",
  "left-right_description": "Displays content alternately on the left and right.",
  "full-details_text": "Detail View",
  "full-details_description": "Displays content with full detail, e.g. as a full article with bodytext, instead of a banner.",
  hero_text: "Hero Banner",
  hero_description: "Displays content as a hero banner.",
  search_text: "Search",
  search_description: "Placeholder for a search field",
  shoppable_text: "Shoppable Video",
  shoppable_description: "Shows a video with assigned products",
  "50-50-portrait_text": "50:50 Portrait Banner",
  "50-50-portrait_description": "Displays content in portrait view (2 in a row).",
  "50-50-landscape_text": "50:50 Landscape Banner",
  "50-50-landscape_description": "Displays content in landscape view (2 in a row).",
  "language-chooser_text": "Language Chooser",
  "language-chooser_description": "Placeholder for Language Chooser",
  "category-header_text": "Category Header",
  "category-header_description": "Displays a mini gap of a category with headline and image.",
  "category-list-categories_text": "Category List",
  "category-list-categories_description": "List all subcategories of a category.",
  "category-list-products_text": "Product List",
  "category-list-products_description": "List all products of a category.",
};

export default BlueprintViewtypes_properties;
