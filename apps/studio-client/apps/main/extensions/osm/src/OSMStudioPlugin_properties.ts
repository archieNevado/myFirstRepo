
/**
 * Interface values for ResourceBundle "OSMStudioPlugin".
 * @see OSMStudioPlugin_properties#INSTANCE
 */
interface OSMStudioPlugin_properties {

/*
 * the default group id used to load the marker icon from the resources
 */
  "osm.groupId": string;

/*
 * an alternative URL for the marker icon, leave the osm.groupId empty if you want to use this
 */
  "osm.marker": string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "OSMStudioPlugin".
 * @see OSMStudioPlugin_properties
 */
const OSMStudioPlugin_properties: OSMStudioPlugin_properties = {
  "osm.groupId": "com.coremedia.blueprint__osm-studio",
  "osm.marker": "http://dev.openlayers.org/img/marker.png",
};

export default OSMStudioPlugin_properties;
