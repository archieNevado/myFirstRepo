import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import TopicPages_properties from "./TopicPages_properties";

/**
 * Overrides of ResourceBundle "TopicPages" for Locale "ja".
 * @see TopicPages_properties#INSTANCE
 */
ResourceBundleUtil.override(TopicPages_properties, {
  TopicPages_administration_title: "トピックページ",
  TopicPages_grid_header_name: "タグ",
  TopicPages_search_title: "検索",
  TopicPages_grid_header_page: "編集済みページ",
  TopicPages_grid_header_options: "有効化",
  TopicPages_search_emptyText: "検索…",
  TopicPages_search_search_tooltip: "検索を開始",
  TopicPages_taxonomy_combo_title: "タグの表示",
  TopicPages_taxonomy_combo_emptyText: "利用できるタグはありません",
  TopicPages_create_link: "相互に編集するページを作成する",
  TopicPages_no_preferred_site: "<i>お好みのサイトが選択されていません<\/i>",
  TopicPages_no_channel_configured_title: "エラー",
  TopicPages_no_channel_configured: "アクティブページ '{0}' のためのトピックページ設定は、新しいトピックページのデフォルトレイアウトとはリンクしていません。",
  TopicPages_name: "トピックページ",
  TopicPages_filtered: "すべてのトピックページが表示されるわけではありません。検索条件を入力してトピックページの量を減らしましょう。",
  TopicPages_deletion_title: "トピックページを削除",
  TopicPages_deletion_tooltip: "トピックページを削除",
  TopicPages_deletion_text: "本当にカスタムトピックページ '{0}' を削除しますか？",
  TopicPages_root_channel_checked_out_title: "エラー",
  TopicPages_root_channel_checked_out_msg: "アクティブサイト '{0}' のメインページコンテンツは別のユーザーがチェックアウト中です。",
  TopicPages_root_channel_not_found_title: "エラー",
  TopicPages_root_channel_not_found_msg: "アクティブサイト '{0}' のメインページコンテンツは解決できませんでした。トピックページのリンクの更新に失敗しました",
  topic_pages_button_tooltip: "トピックページを開く",
  topic_pages_button_no_preferred_site_tooltip: "お気に入りのサイトを選択して、トピックページを開いてください。",
  topic_pages_button_no_topic_page_settings_tooltip: "トピックページ用のルートチャンネルは見つかりませんでした。お気に入りのサイトの「TopicPages」の設定ドキュメントをチェックしてください。",
});
