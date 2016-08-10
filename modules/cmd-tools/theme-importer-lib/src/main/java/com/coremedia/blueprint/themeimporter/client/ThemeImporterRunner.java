package com.coremedia.blueprint.themeimporter.client;

import com.coremedia.blueprint.themeimporter.ThemeImporter;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.server.legacy.exporter.ServerXmlExport;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cap.xmlrepo.XmlCapConnectionFactory;
import com.coremedia.mimetype.DefaultMimeTypeService;
import com.coremedia.mimetype.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;

/**
 * The ThemeImportRunner is being used by the workspace to create serverimportable content from theme resources.
 * <p>
 * <p>
 * How to use: Add the following plugin do your workspace and configure the two system properties themes_folder and export_path
 * <plugin>
 * <groupId>org.codehaus.mojo</groupId>
 * <artifactId>exec-maven-plugin</artifactId>
 * <version>1.5.0</version>
 * <configuration>
 * <executable>java</executable>
 * <arguments>
 * <argument>-Dthemes_folder=${project.build.directory}/themes</argument>
 * <argument>-Dexport_path=${project.build.directory}/content</argument>
 * <argument>-classpath</argument>
 * <classpath/>
 * <argument>com.coremedia.blueprint.themeimporter.ThemeImporterRunner</argument>
 * </arguments>
 * <includePluginDependencies>true</includePluginDependencies>
 * </configuration>
 * </plugin>
 */
public class ThemeImporterRunner {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThemeImporterRunner.class);

  private String folder = "/Themes";
  private String themeFolder;
  private String exportPath;


  private boolean parseParameters() {

    if (StringUtils.hasText(System.getProperties().getProperty("import_folder"))) {
      folder = System.getProperties().getProperty("import_folder").trim();
    }
    themeFolder = System.getProperties().getProperty("themes_folder").trim();

    if (StringUtils.isEmpty(themeFolder)) {
      LOGGER.warn("Wrong argument for parameter themes_folder.");
      return false;
    }
    exportPath = System.getProperties().getProperty("export_path").trim();
    if (StringUtils.isEmpty(exportPath)) {
      LOGGER.trace("Wrong argument for parameter export_path.");
      return false;
    }
    return true;
  }

  public void run() {
    try {
      if (parseParameters()) {
        CapConnection capConnection = ThemeImporterRunner.getCapConnection();
        MimeTypeService mimeTypeService = new DefaultMimeTypeService(true);
        ThemeImporter themeImporter = new ThemeImporter(capConnection, mimeTypeService);

        FilenameFilter zipFilter = new FilenameFilter() {
          public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".zip");
          }
        };

        File themeFolderAsFile = new File(themeFolder.trim());
        File[] zipFiles = themeFolderAsFile.listFiles(zipFilter);
        if (themeFolderAsFile.isDirectory() && zipFiles != null) {
          themeImporter.importThemes(folder, zipFiles);
        }
        export(capConnection, folder);
      }

    } catch (Exception e) {
      LOGGER.error("Something went wrong", e);
    }
  }

  private void export(CapConnection capConnection, String themeRoot) {
    Content contentThemeRoot = capConnection.getContentRepository().getChild(themeRoot);
    if (contentThemeRoot == null) {
      throw new IllegalStateException("Can not find theme for export: " + themeRoot);
    }
    ServerXmlExport export = new ServerXmlExport(capConnection, null);
    export.setContentIds(contentThemeRoot.getId());
    export.setBaseDir(new File(exportPath.trim()));
    export.setPrettyPrint(true);
    export.setRecursive(true);
    export.setLog(LOGGER);
    export.setCutOff(0);
    export.init();
    export.doExport();
  }


  private static CapConnection getCapConnection() {
    XmlCapConnectionFactory factory = new XmlCapConnectionFactory();
    Map<String, String> parameterMap = XmlUapiConfig.builder().withContentTypes("classpath:framework/doctypes/blueprint/blueprint-doctypes.xml").build().getParameterMap();
    CapConnection xmlRepositoryConnection = factory.prepare(parameterMap);
    xmlRepositoryConnection.open();
    return xmlRepositoryConnection;
  }

  public static void main(String[] args) {
    ThemeImporterRunner themeImporterRunner = new ThemeImporterRunner();
    themeImporterRunner.run();
    LOGGER.info("Done importing and exporting themeFolder");
    System.exit(0);
  }
}
