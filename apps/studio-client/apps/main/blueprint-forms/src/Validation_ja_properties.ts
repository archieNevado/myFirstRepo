import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import Validation_properties from "./Validation_properties";

/**
 * Overrides of ResourceBundle "Validation" for Locale "ja".
 * @see Validation_properties#INSTANCE
 */
ResourceBundleUtil.override(Validation_properties, {
  Validator_self_referring_text: "このリンクリストには自身へのリンクが含まれています。",
  Validator_channel_loop_text: "ページのヒエラルキーにループが含まれています。",
  Validator_duplicate_segment_text: "このURLセグメントは同じナビゲーション親とともにすでに '{0}' に存在しています。",
  Validator_duplicate_root_segment_text: "このURLのルートセグメントは、すでにサイト '{1} - {2}' の '{0}' で使用されています。",
  Validator_not_in_navigation_text: "このページはナビゲーションの一部ではありません。",
  Validator_LengthValidator_text: "テキストの長さ値が大き過ぎます。",
  Validator_duplicate_referrer_text: "このページは１ページ以上にリンクされています。",
  Validator_NotEmptyMarkupValidator_text: "このフィールドは空ではいけません。",
  ContentValidator_CMChannel_segment_RegExpValidator_text: "このフィールドには、小文字、数字とダッシュしか入力できません。",
  Validator_Abstract_Code_data_URL_property_must_be_set_text: "もしデータが設定されないなら、データURLを設定しなければなりません。",
  Validator_Abstract_Code_code_property_must_be_set_text: "もしデータURLが設定されないなら、データを設定しなければなりません。",
  ContentValidator_CMVideo_data_atLeastOneNotEmpty_text: "以下のフィールドのうち1つを設定しなければなりません。データ、データURL。",
  ContentValidator_CMVideo_data_exactlyOneMustBeSet_text: "以下のフィールドのうち1つだけを設定してください。データ、データURL。",
  Validator_UniqueInSiteStringValidator_text: "このフィールドには、サイトのすべてのコンテンツで一意な値が含まれる必要があります。同じ値を '{0}' で使用しています。",
  Validator_UniqueStringValidator_text: "このフィールドには、すべてのコンテンツで一意な値が含まれる必要があります。同じ値を '{0}' で使用しています。",
  Validator_no_cmpicture_text: "写真のみがこのフィールドでサポートされています。",
  ValidationStatus_not_valid_anymore: "から無効",
  ValidationStatus_will_be_active: "時点で有効",
  Validator_validFrom_is_after_validTo_text: "有効の開始日は終了日の後です。",
  Validator_validFrom_equals_validTo_text: "有効の開始日が有効の終了日と等しいため、ドキュメントは有効になりません。",
  Validator_placement_visibleFrom_is_after_visibleTo_text: "コンテンツアイテム '{0}' の表示開始日が、表示終了日の後になっています。",
  Validator_placement_visibleFrom_equals_visibleTo_text: "表示開始日が表示終了日と等しいため、ドキュメントは表示されません。",
});
