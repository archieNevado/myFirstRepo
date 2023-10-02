import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import ContentHubRss_properties from "./ContentHubRss_properties";

/**
 * Overrides of ResourceBundle "ContentHubRss" for Locale "ja".
 * @see ContentHubRss_properties#INSTANCE
 */
ResourceBundleUtil.override(ContentHubRss_properties, {
  author_header: "作者",
  lastModified_header: "最終更新日時",
  folder_type_feed_name: "RSSフィード",
  adapter_type_rss_name: "RSSフィード",
  item_type_rss_name: "フィードエントリ",
  metadata_sectionName: "メタデータ",
  text_sectionItemKey: "テキスト",
  author_sectionItemKey: "作者",
  published_sectionItemKey: "公開済",
  lastModified_sectionItemKey: "最終更新日時",
  link_sectionItemKey: "リンク",
});
