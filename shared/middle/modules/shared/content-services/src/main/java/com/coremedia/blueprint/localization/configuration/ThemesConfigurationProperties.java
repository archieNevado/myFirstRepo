package com.coremedia.blueprint.localization.configuration;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@DefaultAnnotation(NonNull.class)
@ConfigurationProperties(prefix = "themes")
public class ThemesConfigurationProperties {

  /**
   * Set the file path to the local blueprint workspace, excl. the modules
   * directory.
   * <p>
   * Required if property delivery.local-resources is true.
   */
  @Value("${coremedia.blueprint.project.directory:}")
  private String projectDirectory = "";

  public String getProjectDirectory() {
    return projectDirectory;
  }

  public void setProjectDirectory(String projectDirectory) {
    this.projectDirectory = projectDirectory;
  }
}
