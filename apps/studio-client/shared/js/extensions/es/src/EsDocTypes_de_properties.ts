import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import EsDocTypes_properties from "./EsDocTypes_properties";

/**
 * Overrides of ResourceBundle "EsDocTypes_properties" for locale "de".
 * @see EsDocTypes_properties
 */
ResourceBundleUtil.override(EsDocTypes_properties, {
  CMMail_displayName: "E-Mail Vorlage",
  CMMail_from_displayName: "Von",
  CMMail_from_emptyText: "Geben Sie hier die E-Mail-Adresse des Senders ein.",
  CMMail_subject_displayName: "Betreff",
  CMMail_subject_emptyText: "Geben Sie hier den Betreff der E-Mail an.",
  CMMail_text_displayName: "E-Mail Text",
  CMMail_text_emptyText: " ",
  CMMail_contentType_displayName: "Content-Type (text/plain, text/html)",
  CMMail_contentType_emptyText: "text/plain, text/html",
  ESDynamicList_displayName: "Dynamische Liste aus Elastic Social",
  ESDynamicList_interval_displayName: "Intervall",
  ESDynamicList_aggregationType_displayName: "Art der Liste",
  ESDynamicList_channel_displayName: "Kontext",
});
