import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "TaxonomyStudioPlugin".
 * @see TaxonomyStudioPlugin_properties#INSTANCE
 */
interface TaxonomyStudioPlugin_properties {

/**
 * Taxonomy Search
 */
  TaxonomySearch_empty_text: string;
  TaxonomySearch_empty_linklist_text: string;
  TaxonomySearch_empty_search_text: string;
  TaxonomySearch_loading_text: string;
  TaxonomySearch_no_hit: string;
/**
 * Taxonomy Editor
 */
  TaxonomyEditor_deletion_blocked_text: string;
  TaxonomyEditor_deletion_failed_text: string;
  TaxonomyEditor_deletion_failed_title: string;
  TaxonomyEditor_deletion_text_referrer_warning: string;
  TaxonomyEditor_deletion_text: string;
  TaxonomyEditor_deletion_title: string;
  TaxonomyEditor_title: string;
  TaxonomyEditor_icon: string;
  TaxonomyExplorerPanel_add_button_label: string;
  TaxonomyExplorerPanel_reload_button_label: string;
  TaxonomyExplorerPanel_remove_button_label: string;
  TaxonomyExplorerPanel_cut_button_label: string;
  TaxonomyExplorerPanel_delete_button_label: string;
  TaxonomyExplorerPanel_paste_button_label: string;
  TaxonomyExplorerColumn_emptyText_loading: string;
  TaxonomyExplorerColumn_emptyText_no_keywords: string;
  TaxonomyExplorerColumn_undefined: string;
/**
 * Taxonomy Preferences
 */
  TaxonomyPreferences_option_name: string;
  TaxonomyPreferences_tab_title: string;
  TaxonomyPreferences_value_nameMatching_text: string;
  TaxonomyPreferences_value_semantic_opencalais_text: string;
  TaxonomyPreferences_settings_tooltip: string;
/**
 * Taxonomy Suggestions
 */
  TaxonomySuggestions_empty_text: string;
  TaxonomySuggestions_loading: string;
/**
 * Taxonomy LinkList
 */
  TaxonomyLinkList_add_suggestion_action_text: string;
  TaxonomyLinkList_edit_action_text: string;
  TaxonomyLinkList_empty_chooser_text: string;
  TaxonomyLinkList_keyword_remove_text: string;
  TaxonomyLinkList_singleSelection_title: string;
  TaxonomyLinkList_status_loading_text: string;
  TaxonomyLinkList_suggestions_add_all: string;
  TaxonomyLinkList_suggestions_reload: string;
  TaxonomyLinkList_suggestions_title: string;
  TaxonomyLinkList_title: string;
  TaxonomyLinkList_contextMenu_chooseTag: string;
/**
 * Actions
 */
  Taxonomy_action_icon: string;
  Taxonomy_action_tooltip: string;
  taxonomy_selection_dialog_title: string;
/**
 * Misc
 */
  TaxonomyType_Location_text: string;
  TaxonomyType_Subject_text: string;
  TaxonomyChooser_selection_text: string;
  TaxonomyChooser_show_children: string;
  TaxonomyChooser_search_tag_title: string;
  TaxonomyChooser_search_tag_emptyText: string;
/**
 * Load Mask
 */
  Taxonomy_loadmask_text: string;
  Favbar_taxonomies_button_label: string;
/**
 * Tags i18n
 */
  Location: string;
  Subject: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "TaxonomyStudioPlugin".
 * @see TaxonomyStudioPlugin_properties
 */
const TaxonomyStudioPlugin_properties: TaxonomyStudioPlugin_properties = {
  TaxonomySearch_empty_text: "Enter a tag suggestion",
  TaxonomySearch_empty_linklist_text: "Enter a tag or drag and drop suggestions here.",
  TaxonomySearch_empty_search_text: "Search…",
  TaxonomySearch_loading_text: "Loading…",
  TaxonomySearch_no_hit: "No search result.",
  TaxonomyEditor_deletion_blocked_text: "The selected tags or their children are linked to {0} content(s). These links must be manually removed before the selected tags can be deleted.",
  TaxonomyEditor_deletion_failed_text: "Failed to delete tags: {0}<br><br>Please check log file for details.",
  TaxonomyEditor_deletion_failed_title: "Error",
  TaxonomyEditor_deletion_text_referrer_warning: "The selected tags or their children are still linked to {0} content(s).<br/><br/>Do you really want to delete the selected tags?",
  TaxonomyEditor_deletion_text: "Do you really want to delete the selected tags?",
  TaxonomyEditor_deletion_title: "Delete Tags",
  TaxonomyEditor_title: "Tags",
  TaxonomyEditor_icon: CoreIcons_properties.taxonomy,
  TaxonomyExplorerPanel_add_button_label: "Add Child Tag",
  TaxonomyExplorerPanel_reload_button_label: "Reload Tags",
  TaxonomyExplorerPanel_remove_button_label: "Delete Selected Tag",
  TaxonomyExplorerPanel_cut_button_label: "Cut Tag",
  TaxonomyExplorerPanel_delete_button_label: "Delete Tag",
  TaxonomyExplorerPanel_paste_button_label: "Paste Tag",
  TaxonomyExplorerColumn_emptyText_loading: "Loading Tags...",
  TaxonomyExplorerColumn_emptyText_no_keywords: "No tags found.",
  TaxonomyExplorerColumn_undefined: "undefined",
  TaxonomyPreferences_option_name: "Suggestions",
  TaxonomyPreferences_tab_title: "Tags",
  TaxonomyPreferences_value_nameMatching_text: "Name Matching",
  TaxonomyPreferences_value_semantic_opencalais_text: "Semantic Evaluation (OpenCalais)",
  TaxonomyPreferences_settings_tooltip: "The suggestions setting defines the type of evaluation that is performed when looking up tags for the given content",
  TaxonomySuggestions_empty_text: "No matching suggestions found.",
  TaxonomySuggestions_loading: "Loading suggestions...",
  TaxonomyLinkList_add_suggestion_action_text: "Add Tag",
  TaxonomyLinkList_edit_action_text: "Show in Tag",
  TaxonomyLinkList_empty_chooser_text: "Add a Tag from the List below",
  TaxonomyLinkList_keyword_remove_text: "Remove Tag",
  TaxonomyLinkList_singleSelection_title: "Selected Tag",
  TaxonomyLinkList_status_loading_text: "Loading…",
  TaxonomyLinkList_suggestions_add_all: "Add All",
  TaxonomyLinkList_suggestions_reload: "Reload Suggestions",
  TaxonomyLinkList_suggestions_title: "Suggested Tags",
  TaxonomyLinkList_title: "Selected Tags",
  TaxonomyLinkList_contextMenu_chooseTag: "Choose tag",
  Taxonomy_action_icon: CoreIcons_properties.taxonomy,
  Taxonomy_action_tooltip: "Choose tag",
  taxonomy_selection_dialog_title: "Choose a Tag",
  TaxonomyType_Location_text: "Location Tags",
  TaxonomyType_Subject_text: "Subject Tags",
  TaxonomyChooser_selection_text: "Browse all available tags",
  TaxonomyChooser_show_children: "Show Children",
  TaxonomyChooser_search_tag_title: "Search",
  TaxonomyChooser_search_tag_emptyText: "Search tag…",
  Taxonomy_loadmask_text: "Loading Tags ...",
  Favbar_taxonomies_button_label: "Tags",
  Location: "Location",
  Subject: "Subject",
};

export default TaxonomyStudioPlugin_properties;
