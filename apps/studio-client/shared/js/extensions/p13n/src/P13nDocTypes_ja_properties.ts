import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import P13nDocTypes_properties from "./P13nDocTypes_properties";

/**
 * Overrides of ResourceBundle "P13nDocTypes_properties" for locale "ja".
 * @see P13nDocTypes_properties
 */
ResourceBundleUtil.override(P13nDocTypes_properties, {
  CMSelectionRules_displayName: "カスタマイズされたコンテンツ",
  CMSelectionRules_description: "カスタマイズされた条件に基づき選択されるコンテンツアイテムのリスト",
  CMSelectionRules_title_displayName: "敬称",
  CMSelectionRules_title_description: "カスタマイズされたコンテンツのタイトル",
  CMSelectionRules_title_emptyText: "ここにタイトルを挿入します",
  CMSelectionRules_text_displayName: "説明テキスト",
  CMSelectionRules_text_description: "説明テキスト",
  CMSelectionRules_rules_displayName: "ルール",
  CMSelectionRules_rules_description: "該当する条件に対して評価されるコンテンツのリスト。最初に条件一致するコンテンツが使用されます。",
  CMSelectionRules_defaultContent_displayName: "デフォルトコンテンツ",
  CMSelectionRules_defaultContent_description: "条件/ルールと一致するものがない、あるいはルールにエラーが含まれる場合に、デフォルトコンテンツが使用されます",
  CMSelectionRules_defaultContent_emptyText: "ライブラリからデフォルトコンテンツをドラッグして追加します",
  CMSegment_displayName: "ユーザーセグメント（カスタマイズ）",
  CMSegment_description: "セグメントは、ウェブサイトユーザーを名前が付けられたセグメントにグループ化するための条件を定義します",
  CMSegment_description_displayName: "セグメントの説明",
  CMSegment_description_description: "セグメントの目的を説明する記述",
  CMSegment_description_emptyText: "編集用に説明を入力します",
  CMSegment_conditions_displayName: "セグメントの条件",
  CMSegment_conditions_description: "セグメントの一部として見なされるために、ユーザープロファイルと一致しなければならない条件",
  CMUserProfile_displayName: "ペルソナ（カスタマイズ）",
  CMUserProfile_description: "仮想ウェブサイトユーザーのあらかじめ定義されたプロファイル設定で、個人用にカスタマイズされた行動を装うためにペルソナを使用できます",
  CMUserProfile_profileSettings_displayName: "コンテキストデータ",
  CMUserProfile_profileSettings_description: "このペルソナコンテキストで使用するキー/値リスト",
  CMUserProfile_profileSettings_emptyText: "ここにコンテキストデータを入力します。例：interest.sports=true",
  CMP13NSearch_displayName: "カスタマイズ検索",
  CMP13NSearch_description: "カスタマイズ検索は、コンテキスト情報やその他の拡張機能で検索クエリを向上するために使用します",
  CMP13NSearch_documentType_displayName: "ドキュメントの種類",
  CMP13NSearch_documentType_description: "ドキュメントの種類を指定してください",
  CMP13NSearch_documentType_emptyText: "ドキュメントの種類を入力します",
  CMP13NSearch_searchQuery_displayName: "検索クエリ",
  CMP13NSearch_searchQuery_description: "カスタマイズされた検索クエリを入力してください",
  CMP13NSearch_searchQuery_emptyText: "カスタマイズされた検索クエリを入力します。例：'name:Offer* AND userKeywords(limit:-1, field:keywords, threshold:0.6, context:myContext)'",
  CMP13NSearch_maxLength_displayName: "結果の最大件数",
  CMP13NSearch_maxLength_description: "表示する結果の最大件数を入力します",
  CMP13NSearch_maxLength_emptyText: "結果の最大件数",
  CMP13NSearch_defaultContent_displayName: "デフォルトコンテンツ",
  CMP13NSearch_defaultContent_emptyText: "デフォルトコンテンツを追加します",
  CMP13NSearch_searchContext_displayName: "サイトレベルから検索を開始する",
  CMP13NSearch_searchContext_emptyText: "1つ以上のナビゲーションコンテキストアイテムを追加します",
});
