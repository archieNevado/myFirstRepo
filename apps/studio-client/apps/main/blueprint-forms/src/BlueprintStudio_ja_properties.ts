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
  Dropdown_freshness_text: "変更日",
  ChannelSidebar_tooltip: "新しいドキュメントの作成と追加",
  status_loading: "読み込み中...",
  Dashboard_standardConfiguration_lastEdited: "自分が編集中の案件",
  Dashboard_standardConfiguration_editedByOthers: "他者による編集",
  FavoritesToolbarDefaultSearchFolderNames_lastEdited: "最終編集日",
  FavoritesToolbarDefaultSearchFolderNames_articles: "記事",
  FavoritesToolbarDefaultSearchFolderNames_pictures: "画像",
  FavoritesToolbarDefaultSearchFolderNames_pages: "ページ",
  DataView_empty_text: "---",
});
