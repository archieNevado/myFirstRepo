import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import TaxonomyStudioPluginSettings_properties from "./TaxonomyStudioPluginSettings_properties";

/**
 *##############################################################
 * Default settings of the taxonomy manager and link lists
 *##############################################################
 * @see TaxonomyStudioPluginSettings_properties#INSTANCE
 */
ResourceBundleUtil.override(TaxonomyStudioPluginSettings_properties, {
  taxonomy_display_property: "properties.value",
  taxonomy_location_doctype: "CMLocTaxonomy",
  taxonomy_location_latLong_property_name: "properties.latitudeLongitude",
  taxonomy_location_default_value: "53.5492,9.9803",
  taxonomy_default_name: "新しいキーワード",
});
