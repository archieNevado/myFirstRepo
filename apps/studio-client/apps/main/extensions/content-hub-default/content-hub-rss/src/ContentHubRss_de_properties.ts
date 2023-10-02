import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import ContentHubRss_properties from "./ContentHubRss_properties";

/**
 * Overrides of ResourceBundle "ContentHubRss" for Locale "de".
 * @see ContentHubRss_properties#INSTANCE
 */
ResourceBundleUtil.override(ContentHubRss_properties, {
  author_header: "Autor",
  lastModified_header: "Zuletzt bearbeitet",
  folder_type_feed_name: "RSS Feed",
  adapter_type_rss_name: "RSS Feeds",
  item_type_rss_name: "Feed Eintrag",
  metadata_sectionName: "Metadaten",
  text_sectionItemKey: "Text",
  author_sectionItemKey: "Autor",
  published_sectionItemKey: "Publiziert",
  lastModified_sectionItemKey: "Zuletzt bearbeitet",
  link_sectionItemKey: "Link",
});
