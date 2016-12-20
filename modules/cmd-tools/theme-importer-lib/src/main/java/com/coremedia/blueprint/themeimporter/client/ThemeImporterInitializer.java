package com.coremedia.blueprint.themeimporter.client;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ThemeImporterInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  static final String THEMEIMPORTER_FOLDER = "com.coremedia.blueprint.themeimporter.folder";
  static final String THEMEIMPORTER_THEMES = "com.coremedia.blueprint.themeimporter.themes";
  static final String THEMEIMPORTER_SERVEREXPORTPATH = "com.coremedia.blueprint.themeimporter.serverexportpath";
  static final String THEMEIMPORTER_EXITCODE = "com.coremedia.blueprint.themeimporter.exitcode";
  static final String REPOSITORY_FOLDER = "/Themes";

  // Repository folder
  private String folder = REPOSITORY_FOLDER;
  // Absolute zip file paths
  private List<String> themes;
  // Target file path for serverexport
  private String serverExportPath;
  // Callback for a proposed System.exit code
  private AtomicInteger exitCode;

  ThemeImporterInitializer(String folder, String[] themes, String serverExportPath, AtomicInteger exitCode) {
    this(folder, Arrays.asList(themes), serverExportPath, exitCode);
  }

  ThemeImporterInitializer(String folder, List<String> themes, String serverExportPath, AtomicInteger exitCode) {
    this.folder = folder;
    this.themes = themes;
    this.serverExportPath = serverExportPath;
    this.exitCode = exitCode;
  }

  @Override
  public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
    HashMap<String, Object> config = new HashMap<>();
    config.put(THEMEIMPORTER_FOLDER, folder);
    config.put(THEMEIMPORTER_THEMES, themes);
    config.put(THEMEIMPORTER_EXITCODE, exitCode);
    if (serverExportPath != null) {
      config.put(THEMEIMPORTER_SERVEREXPORTPATH, serverExportPath);
    }
    MapPropertySource propertySource = new MapPropertySource("themeimporter", config);
    configurableApplicationContext.getEnvironment().getPropertySources().addLast(propertySource);
  }
}
