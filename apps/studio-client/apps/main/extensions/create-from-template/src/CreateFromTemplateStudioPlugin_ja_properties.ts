import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import CreateFromTemplateStudioPlugin_properties from "./CreateFromTemplateStudioPlugin_properties";

/**
 * Overrides of ResourceBundle "CreateFromTemplateStudioPlugin" for Locale "ja".
 * @see CreateFromTemplateStudioPlugin_properties#INSTANCE
 */
ResourceBundleUtil.override(CreateFromTemplateStudioPlugin_properties, {
  text: "テンプレートからのページ",
  folders_text: "フォルダ",
  channel_folder_text: "ページ用基本フォルダ",
  editorial_folder_text: "コンテンツ用基本フォルダ",
  choose_template_text: "ページ用テンプレート",
  template_create_missing_value: "このフィールドは空ではいけません。",
  name_not_valid_value: "名前が有効ではありません。",
  page_folder_combo_validation_message: "このフィールドを空にしてはならず、またこのフォルダは存在してはなりません。",
  no_parent_page_selected_warning: "これは、ナビゲーション構造体の一部ではない新しいページを作成することになります。本当に実行しますか？",
  no_parent_page_selected_warning_buttonText: "作成する",
  editor_folder_could_not_create_message: "エディトリアルコンテンツのためのフォルダは作成できませんでした。",
  page_folder_could_not_create_message: "ナビゲーションのためのフォルダは作成できませんでした。",
  name_label: "名前",
  name_text: "この名前の新規フォルダが、基本フォルダの下に作成されます。",
  template_chooser_empty_text: "テンプレートを選択してください。",
  parent_label: "ナビゲーション親",
  dialog_title: "新規の{0}",
  quick_create_tooltip: "新しいコンテンツアイテムを作成",
});
