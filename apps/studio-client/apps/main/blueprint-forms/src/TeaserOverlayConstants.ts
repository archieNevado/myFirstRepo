import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";

/**
 * Constants for Teaser Overlays.
 */
class TeaserOverlayConstants {

  /**
   * Property path from content to teaser overlay configuration.
   */
  static readonly DEFAULT_SETTINGS_PATH: string = ContentPropertyNames.PROPERTIES + ".localSettings.teaserOverlay";

  /**
   * Array of paths to look the style descriptors up.
   *
   * Paths not starting with / are relative to the site folder of the teaser content the overlay is used for.
   * Paths starting with / are absolute (relative from root)
   */
  static readonly DEFAULT_STYLE_DESCRIPTOR_FOLDER_PATHS: Array<any> = [
    "Options/Settings/Teaser Styles/",
    "/Settings/Options/Settings/Teaser Styles/",
  ];

  /**
   * The name of the style descriptor content that is used as a default.
   */
  static readonly DEFAULT_STYLE_NAME: string = "Default";
}

export default TeaserOverlayConstants;
