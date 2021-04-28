package com.coremedia.blueprint.assets;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "assets")
public class AssetManagementConfigurationProperties {

  private String settingsDocument = "AssetManagement";

  public String getSettingsDocument() {
    return settingsDocument;
  }

  public void setSettingsDocument(String settingsDocument) {
    this.settingsDocument = settingsDocument;
  }

}
