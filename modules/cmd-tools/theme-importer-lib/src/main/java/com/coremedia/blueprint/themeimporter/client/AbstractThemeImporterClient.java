package com.coremedia.blueprint.themeimporter.client;

import com.coremedia.blueprint.themeimporter.ThemeImporter;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.server.legacy.exporter.ServerXmlExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractThemeImporterClient implements CommandLineRunner, ApplicationContextAware {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractThemeImporterClient.class);

  private CapConnection capConnection;
  private ThemeImporter themeImporter;
  private Environment env;


  // --- Spring -----------------------------------------------------

  @Inject
  public void setCapConnection(CapConnection capConnection) {
    this.capConnection = capConnection;
  }

  @Resource(name="themeImporter")
  public void setThemeImporter(ThemeImporter themeImporter) {
    this.themeImporter = themeImporter;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    env = applicationContext.getEnvironment();
  }

  @Override
  public void run(String... strings) {
    try {
      work();
      if (getExitCode().get() != 0) {
        LOG.warn("Done, with errors.");
      } else {
        LOG.info("Done.");
      }
    } catch (Exception e) {
      LOG.error("Failed!", e);
      getExitCode().set(20);
    }
  }

  protected static void runSpringApplication(SpringApplication springApplication, AtomicInteger exitCodeCallback) {
    try (ConfigurableApplicationContext applicationContext = springApplication.run()) {
      LOG.debug("Theme importer success");
    } catch (Exception e) {
      LOG.error("Theme importer failed", e);
      exitCodeCallback.set(20);
    }
  }


  // --- properties -------------------------------------------------

  private String getFolder() {
    return env.getProperty(ThemeImporterInitializer.THEMEIMPORTER_FOLDER);
  }

  private List<String> getThemes() {
    return env.getProperty(ThemeImporterInitializer.THEMEIMPORTER_THEMES, List.class);
  }

  private String getServerExportPath() {
    return env.getProperty(ThemeImporterInitializer.THEMEIMPORTER_SERVEREXPORTPATH);
  }

  private AtomicInteger getExitCode() {
    return env.getProperty(ThemeImporterInitializer.THEMEIMPORTER_EXITCODE, AtomicInteger.class);
  }


  // --- internal ---------------------------------------------------

  private void work() {
    themeImporter.importThemes(getFolder(), collectThemeFiles(getThemes(), getExitCode()));
    String serverExportPath = getServerExportPath();
    if (serverExportPath!=null) {
      Content themeRoot = capConnection.getContentRepository().getChild(getFolder());
      if (themeRoot == null) {
        LOG.warn("Nothing to export at {}, maybe the theme import did not work as expected.", getFolder());
      } else {
        LOG.info("serverexport themes to " + serverExportPath);
        export(capConnection, themeRoot, serverExportPath);
      }
    }
  }

  private static Collection<File> collectThemeFiles(List<String> themes, AtomicInteger exitCode) {
    Collection<File> files = new ArrayList<>();
    for (String theme : themes) {
      File file = new File(theme);  // NOSONAR Yes, as a matter of fact, we do read files, like it or not.
      if (file.exists() && !file.isDirectory() && file.canRead()) {
        files.add(file);
      } else {
        LOG.warn("Cannot read theme {}, skipped.", theme);
        exitCode.set(100);
      }
    }
    return files;
  }

  private static void export(CapConnection capConnection, Content themeRoot, String exportPath) {
    ServerXmlExport export = new ServerXmlExport(capConnection, null);
    export.setContentIds(themeRoot.getId());
    export.setBaseDir(new File(exportPath));  // NOSONAR Yes, as a matter of fact, we do write files, like it or not.
    export.setPrettyPrint(true);
    export.setRecursive(true);
    export.setCutOff(0);
    export.init();
    export.doExport();
  }
}
