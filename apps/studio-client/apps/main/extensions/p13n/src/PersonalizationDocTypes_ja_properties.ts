import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import PersonalizationDocTypes_properties from "./PersonalizationDocTypes_properties";

/**
 * Overrides of ResourceBundle "PersonalizationDocTypes" for Locale "ja".
 * @see PersonalizationDocTypes_properties#INSTANCE
 */
ResourceBundleUtil.override(PersonalizationDocTypes_properties, {
  CMSelectionRules_text: "カスタマイズコンテンツ",
  CMSelectionRules_toolTip: "カスタマイズした条件に基づき選択されたコンテンツアイテムのリスト",
  CMSelectionRules_title_text: "タイトル",
  CMSelectionRules_title_emptyText: "タイトルをこちらに入力。",
  CMSelectionRules_title_toolTip: "カスタマイズしたコンテンツのタイトル",
  CMSelectionRules_text_text: "説明テキスト",
  CMSelectionRules__toolTip: "説明テキスト",
  CMSelectionRules_rules_text: "ルール",
  CMSelectionRules_rules_toolTip: "該当する条件に基づき評価されたコンテンツアイテムのリスト。最初の条件に合致したコンテンツアイテムを使用します。",
  CMSelectionRules_defaultContent_text: "デフォルトコンテンツ",
  CMSelectionRules_defaultContent_emptyText: "こちらのライブラリからドラッグして、デフォルトコンテンツを追加します。",
  CMSelectionRules_defaultContent_toolTip: "どの条件やルールにも合致しない場合、またはルールに間違いがある場合、デフォルトのコンテンツを使用します。",
  CMSegment_text: "顧客セグメント",
  CMSegment_toolTip: "ウェブサイトの顧客を名前の付いたセグメントにグループ化する条件を､セグメントが定義します。",
  CMSegment_description_text: "セグメント説明",
  CMSegment_description_emptyText: "エディトリアルで使用する説明を入力。",
  CMSegment_description_toolTip: "それぞれのセグメントの目的を説明する記述",
  CMSegment_conditions_text: "セグメント条件",
  CMSegment_conditions_toolTip: "このセグメントに入るために、顧客プロファイルが合致しないといけない条件",
  CMUserProfile_text: "顧客ペルソナ",
  CMUserProfile_toolTip: "仮想的なウェブサイト利用者で、あらかじめ定義したプロファイル設定を持つ､カスタマイズした行動をシミュレーションするのに用いることができる顧客ペルソナ。",
  CMUserProfile_profileSettings_text: "コンテキストデータ",
  CMUserProfile_profileSettings_emptyText: "あなたのコンテキストデータをこちらに入力してください。例： 興味.スポーツ=真。(interest.sports=true)",
  CMUserProfile_profileSettings_toolTip: "このペルソナのコンテキストで使用する、重要で価値のあるコンテキストデータリスト",
  CMUserProfile_favlabel: "顧客ペルソナ",
  CMP13NSearch_text: "カスタマイズした検索",
  CMP13NSearch_toolTip: "コンテキスト情報やその他の拡張機能で検索クエリを強化するためにカスタマイズした検索を用います。",
  CMP13NSearch_documentType_text: "コンテンツタイプ",
  CMP13NSearch_documentType_toolTip: "コンテンツタイプを提供",
  CMP13NSearch_documentType_emptyText: "コンテンツタイプを入力してください。",
  CMP13NSearch_searchQuery_text: "検索クエリ",
  CMP13NSearch_searchQuery_toolTip: "カスタマイズした検索クエリを入力してください",
  CMP13NSearch_searchQuery_emptyText: "カスタマイズした検索クエリを入力してください、例 名前:Offer* AND ユーザーキーワード(limit:-1, field:keywords, threshold:0.6, context:myContext)",
  CMP13NSearch_maxLength_text: "結果の最大数",
  CMP13NSearch_maxLength_toolTip: "必要な結果の最大数を入力",
  CMP13NSearch_maxLength_emptyText: "結果の最大数",
  CMP13NSearch_defaultContent_text: "デフォルトコンテンツ",
  CMP13NSearch_defaultContent_emptyText: "こちらのライブラリからドラッグして、デフォルトコンテンツを追加します。",
  CMP13NSearch_searchContext_text: "サイトレベルから始める検索リンクリスト",
  CMP13NSearch_searchContext_emptyText: "１つ以上のナビゲーションコンテキストアイテムを加える。",
});
