import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import BlueprintStudio_properties from "./BlueprintStudio_properties";

/**
 * Overrides of ResourceBundle "BlueprintStudio" for Locale "ja".
 * @see BlueprintStudio_properties#INSTANCE
 */
ResourceBundleUtil.override(BlueprintStudio_properties, {
  btn_fav1_txt: "画像",
  btn_fav2_txt: "記事",
  btn_fav3_txt: "ページ",
  SpacerTitle_navigation: "ナビゲーション",
  SpacerTitle_versions: "バージョン",
  SpacerTitle_layout: "レイアウト",
  Dropdown_default_text: "---",
  Dropdown_freshness_text: "修正日",
  ChannelSidebar_tooltip: "新しいコンテンツアイテムを作成し追加する",
  status_loading: "ロード中…",
  Dashboard_standardConfiguration_lastEdited: "自分が編集中の案件",
  Dashboard_standardConfiguration_editedByOthers: "他人が編集中の案件",
  FavoritesToolbarDefaultSearchFolderNames_lastEdited: "最新の編集",
  FavoritesToolbarDefaultSearchFolderNames_articles: "記事",
  FavoritesToolbarDefaultSearchFolderNames_pictures: "写真",
  FavoritesToolbarDefaultSearchFolderNames_pages: "ページ",
  DataView_empty_text: "メタデータがありません",
});
