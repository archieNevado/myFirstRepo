package com.coremedia.blueprint.studio.rest;

/**
 * Configuration for the UploadStudioPlugin
 */
public class UploadConfiguration {
  private String developerGroups;

  public void setDeveloperGroups(String developerGroups) {
    this.developerGroups = developerGroups;
  }

  /**
   * Useful to enable a feature (namely the theme upload) only for specific
   * developer groups.
   */
  public String getDeveloperGroups() {  // NOSONAR used via reflection
    return developerGroups;
  }
}
