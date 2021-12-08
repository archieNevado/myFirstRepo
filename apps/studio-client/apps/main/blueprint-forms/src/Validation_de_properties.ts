import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import Validation_properties from "./Validation_properties";

/**
 * Overrides of ResourceBundle "Validation" for Locale "de".
 * @see Validation_properties#INSTANCE
 */
ResourceBundleUtil.override(Validation_properties, {
  Validator_self_referring_text: "Die Linkliste enthält einen Verweis auf sich selbst.",
  Validator_channel_loop_text: "Die Seitenhierarchie enthält eine Schleife.",
  Validator_duplicate_segment_text: "Dieses URL-Segment ist unterhalb des gleichen Navigationknotens bereits vergeben in '{0}'.",
  Validator_duplicate_root_segment_text: "Dieses URL-Segment ist bereits vergeben in '{0}' der Site '{1} - {2}'.",
  Validator_not_in_navigation_text: "Diese Seite ist nicht Bestandteil der Navigation.",
  Validator_LengthValidator_text: "Die maximale Länge des Textes wurde überschritten.",
  Validator_duplicate_referrer_text: "Die Seite wird von mehr als einer Seite verlinkt.",
  Validator_NotEmptyMarkupValidator_text: "Dieses Feld darf nicht leer sein.",
  ContentValidator_CMChannel_segment_RegExpValidator_text: "Dieses Feld darf nur kleine Buchstaben, Ziffern und Bindestriche enthalten.",
  Validator_Abstract_Code_data_URL_property_must_be_set_text: "Wenn das Feld \"Daten\" nicht gesetzt ist, muss das Feld \"Daten-URL\" gesetzt sein.",
  Validator_Abstract_Code_code_property_must_be_set_text: "Wenn das Feld \"Daten-URL\" nicht gesetzt ist, muss das Feld \"Daten\" gesetzt sein.",
  ContentValidator_CMVideo_data_atLeastOneNotEmpty_text: "Eines der folgenden Felder muss einen Wert enthalten: Data, Video-URL.",
  ContentValidator_CMVideo_data_exactlyOneMustBeSet_text: "Nur eines der folgenden Felder sollte einen Wert enthalten: Data, Video-URL.",
  Validator_UniqueInSiteStringValidator_text: "Der Wert dieses Feldes muss eindeutig innerhalb der Site sein. Der gleiche Wert wird bereits benutzt in '{0}'.",
  Validator_UniqueStringValidator_text: "Der Wert dieses Feldes muss eindeutig sein. Der gleiche Wert wird bereits benutzt in '{0}'.",
  Validator_no_cmpicture_text: "In diesem Feld werden nur Bilder unterstützt.",
  CMNavigation_hidden_text: "Dieses Element ist ausgeblendet und daher auch nicht auf der Website sichtbar.",
  ValidationStatus_not_valid_anymore: "Ungültig seit",
  ValidationStatus_will_be_active: "Wird gültig am",
  Validator_validFrom_is_after_validTo_text: "Der Gültigkeitsbeginn liegt nach dem Gültigkeitsende.",
  Validator_validFrom_equals_validTo_text: "Das Dokument kann niemals gültig sein, weil der Gültigkeitsbeginn und das Gültigkeitsende identisch sind.",
  Validator_placement_visibleFrom_is_after_visibleTo_text: "Der Sichtbarkeitsbeginn für das Dokument '{0}' liegt nach dem Sichtbarkeitsende.",
  Validator_placement_visibleFrom_equals_visibleTo_text: "Das Dokument kann niemals sichtbar sein, weil der Sichtbarkeitsbeginn und das Sichtbarkeitsende identisch sind.",
});
