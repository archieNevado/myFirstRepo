import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import LiveContextStudioPluginValidator_properties from "./LiveContextStudioPluginValidator_properties";

/**
 * Overrides of ResourceBundle "LiveContextStudioPluginValidator" for Locale "ja".
 * @see LiveContextStudioPluginValidator_properties#INSTANCE
 */
ResourceBundleUtil.override(LiveContextStudioPluginValidator_properties, {
  Validator_catalogError_text: "カタログをロードできませんでした。想定外のカタログエラーが発生しました。",
  Validator_CMProductTeaser_EmptyExternalId_text: "商品がリンクしていません。ティーザーには商品への参照が必要です。",
  Validator_CMProductTeaser_InvalidId_text: "コード \"{0}\" のついた商品は、カタログ \"{1}\" にはありません。",
  Validator_CMProductTeaser_ValidInAWorkspace_text: "コード \"{0}\" のついた商品は、カタログ \"{1}\" のワークスペース \"{2}\" にしかありません。",
  Validator_CMProductTeaser_InvalidStoreContext_text: "商品をロードできませんでした。カタログ設定が無効です。",
  Validator_CMProductTeaser_StoreContextNotFound_text: "現在のコンテンツのカタログ設定が見つかりませんでした。",
  Validator_CMProductTeaser_CatalogNotFoundError_text: "カタログ \"{0}\" は、ID \"{1}\"の商品については見つかりませんでした。",
  Validator_CMExternalChannel_EmptyCategory_text: "外部IDが空です。ページにはカテゴリーへの参照が必要です。",
  Validator_CMExternalChannel_InvalidId_text: "ID \"{0}\" のカテゴリーは、カタログ \"{1}\" にはありません。",
  Validator_CMExternalChannel_ValidInAWorkspace_text: "ID \"{0}\" の商品は、カタログ \"{1}\" のワークスペース \"{2}\" にしかありません。",
  Validator_CMExternalChannel_InvalidStoreContext_text: "カタログをロードできませんでした。カタログ設定がないか無効です。",
  Validator_CMExternalChannel_CatalogNotFoundError_text: "カタログ \"{0}\" は、ID \"{1}\"のカテゴリーについては見つかりませんでした。",
  Validator_CMExternalProduct_EmptyProduct_text: "外部IDが空です。ページには商品への参照が必要です。",
  Validator_CMExternalProduct_InvalidId_text: "ID \"{0}\" の商品は、カタログ \"{1}\" にはありません。",
  Validator_CMExternalProduct_ValidInAWorkspace_text: "ID \"{0}\" の商品は、カタログ \"{1}\" のワークスペース \"{2}\" にしかありません。",
  Validator_CMExternalProduct_InvalidStoreContext_text: "商品をロードできませんでした。カタログ設定がないか無効です。",
  Validator_CMExternalProduct_CatalogNotFoundError_text: "商品 \"{0}\" は、ID \"{1}\"の商品については見つかりませんでした。",
  Validator_CMExternalPage_EmptyExternalPageId_text: "外部ページIDが空です。ページには、外部ページへの参照が必要です。",
  Validator_CMMarketingSpot_EmptyExternalId_text: "eマーケティングスポットがリンクしていません。ティーザーには、eマーケティングスポットへの参照が必要です。",
  Validator_CMMarketingSpot_InvalidId_text: "コード \"{0}\" のついたeマーケティングスポットは、カタログ \"{1}\" にはありません。",
  Validator_CMMarketingSpot_ValidInAWorkspace_text: "コード \"{0}\" のついたeマーケティングスポットは、カタログ \"{1}\" のワークスペース \"{2}\" にしかありません。",
  Validator_CMMarketingSpot_InvalidStoreContext_text: "eマーケティングスポットをロードできませんでした。カタログ設定が無効です。",
  Validator_CMMarketingSpot_StoreContextNotFound_text: "現在のコンテンツのカタログ設定が見つかりませんでした。",
  Validator_CMProductList_InvalidId_text: "コード \"{0}\" のついたカテゴリーは、カタログ \"{1}\" にはありません。",
  Validator_CMProductList_ValidInAWorkspace_text: "コード \"{0}\" のついたカテゴリーは、カタログ \"{1}\" のワークスペース \"{2}\" にしかありません。",
  Validator_CMProductList_InvalidStoreContext_text: "カテゴリーをロードできませんでした。カタログ設定が無効です。",
  Validator_CMProductList_StoreContextNotFound_text: "現在のコンテンツのカタログ設定が見つかりませんでした。",
  Validator_CMProductList_DocTypeNotSupported_text: "ドキュメントの種類「商品リスト」はこのサイトではサポートされていないので、使用しないでください。",
  Validator_CMProductList_invalid_legacy_name_text: "フィルター \"{0}\" は現在は無効です。新たな値を選択するか、ドキュメントフォームで複数の選択を有効にしてください。",
  Validator_CMProductList_invalid_legacy_query_text: "フィルター \"{1}\" の値 \"{0}\" は、現在は無効です。新たな値を選択するか、ドキュメントフォームで複数の選択を有効にしてください。",
  Validator_CMProductList_invalid_multi_facet_text: "フィルターの設定が無効です。修復するには、ドキュメントフォームで「すべての無効なフィルターを削除」を選択してください。",
  Validator_CMProductList_invalid_multi_facet_query_text: "フィルター \"{1}\" の値 \"{0}\" は、現在は無効です。新たな値を選択してください。",
  Validator_CMChannel_SegmentReservedCharsFound_text: "セグメントには、内部分離記号として使用される \"{0}\" が含まれます。",
  Validator_CMChannel_SegmentReservedPrefix_text: "セグメントでは、先頭に \"{0}\" を使用することは認められていません。",
  Validator_CMChannel_SegmentReservedSuffix_text: "セグメントでは、最後に \"{0}\" を使用することは認められていません。",
  Validator_CMChannel_FallbackSegmentReservedCharsFound_text: "セグメントはタイトルから引き継がれ、\"{1}\"となりますが、内部分離記号として使用される \"{0}\" が含まれます。",
  Validator_CMChannel_FallbackSegmentReservedPrefix_text: "セグメントはタイトルから引き継がれ、\"{1}\"となりますが、最初に \"{0}\" を使用することは認められていません。",
  Validator_CMChannel_FallbackSegmentReservedSuffix_text: "セグメントはタイトルから引き継がれ、\"{1}\"となりますが、最後に \"{0}\" を使用することは認められていません。",
});
