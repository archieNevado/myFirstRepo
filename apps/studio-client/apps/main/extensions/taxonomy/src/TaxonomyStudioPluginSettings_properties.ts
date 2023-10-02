
/**
 *##############################################################
 * Default settings of the taxonomy manager and link lists
 *##############################################################
 * @see TaxonomyStudioPluginSettings_properties#INSTANCE
 */
interface TaxonomyStudioPluginSettings_properties {

/**
 * The content property that is used for displaying the name in the administration column
 */
  taxonomy_display_property: string;
/**
 * The document sub type that is used for map integration
 */
  taxonomy_location_doctype: string;
/**
 * Location lat and long property (comma separated value expected)
 */
  taxonomy_location_latLong_property_name: string;
/**
 * The default value used for a new location based taxonomy
 */
  taxonomy_location_default_value: string;
/**
 * The default name that is used when a new tag is created
 */
  taxonomy_default_name: string;
/**
 * Properties used to find referrers
 */
  taxonomy_referrer_doctype: string;
  taxonomy_referrer_properties: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "TaxonomyStudioPluginSettings".
 * @see TaxonomyStudioPluginSettings_properties
 */
const TaxonomyStudioPluginSettings_properties: TaxonomyStudioPluginSettings_properties = {
  taxonomy_display_property: "value",
  taxonomy_location_doctype: "CMLocTaxonomy",
  taxonomy_location_latLong_property_name: "properties.latitudeLongitude",
  taxonomy_location_default_value: "53.5492,9.9803",
  taxonomy_default_name: "new tag",
  taxonomy_referrer_doctype: "CMLinkable",
  taxonomy_referrer_properties: "subjectTaxonomy,locationTaxonomy",
};

export default TaxonomyStudioPluginSettings_properties;
