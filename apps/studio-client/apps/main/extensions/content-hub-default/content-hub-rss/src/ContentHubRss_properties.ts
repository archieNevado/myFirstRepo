import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "ContentHubRss".
 * @see ContentHubRss_properties#INSTANCE
 */
interface ContentHubRss_properties {

/**
 *Rss
 */
  author_header: string;
  lastModified_header: string;
  folder_type_feed_name: string;
  folder_type_feed_icon: string;
  adapter_type_rss_name: string;
  adapter_type_rss_icon: string;
  item_type_rss_name: string;
  item_type_rss_icon: string;
  metadata_sectionName: string;
  text_sectionItemKey: string;
  author_sectionItemKey: string;
  published_sectionItemKey: string;
  lastModified_sectionItemKey: string;
  link_sectionItemKey: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ContentHubRss".
 * @see ContentHubRss_properties
 */
const ContentHubRss_properties: ContentHubRss_properties = {
  author_header: "Author",
  lastModified_header: "Last Modified",
  folder_type_feed_name: "RSS Feed",
  folder_type_feed_icon: CoreIcons_properties.rss_feed,
  adapter_type_rss_name: "RSS Feeds",
  adapter_type_rss_icon: CoreIcons_properties.rss_feed,
  item_type_rss_name: "Feed Entry",
  item_type_rss_icon: CoreIcons_properties.rss_item,
  metadata_sectionName: "Metadata",
  text_sectionItemKey: "Text",
  author_sectionItemKey: "Author",
  published_sectionItemKey: "Published",
  lastModified_sectionItemKey: "Last modified",
  link_sectionItemKey: "Link",
};

export default ContentHubRss_properties;
