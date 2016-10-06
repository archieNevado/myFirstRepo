package com.coremedia.blueprint.themeimporter.client;

import com.coremedia.blueprint.themeimporter.ThemeImporter;
import com.coremedia.cmdline.AbstractUAPIClient;
import com.coremedia.cmdline.base.Client;
import com.coremedia.mimetype.DefaultMimeTypeService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Imports Themes into the ContentRepository.
 * <p>
 * To import, start the theme-importer application which has the following
 * synopsis:<br>
 * <pre>{@code
 * cm import-themes -u &lt;user&gt; [other options] &lt;theme.zip&gt; ...
 * }</pre>
 * <p/>
 * <b>Options:</b>
 * <table>
 * <tr><td>-f, --folder</td><td>Folder within CoreMedia where themes are stored. Defaults to /Themes</td></tr>
 * <tr><td>-v, --verbose</td><td>verbose output</td></tr>
 * <tr><td>-u, --user &lt;user name&gt;</td><td>the name of the CoreMedia user</td></tr>
 * <tr><td>-d, --domain &lt;domain&gt;</td><td>the domain of the user</td></tr>
 * <tr><td>-p, --password &lt;password&gt;</td><td>the users password</td></tr>
 * <tr><td>-url &lt;ior url&gt;</td><td>Content Server IOR URL to connect to</td></tr>
 * </table>
 */
public class ThemeImporterClient extends AbstractUAPIClient {
  private static final Logger LOG = LoggerFactory.getLogger(ThemeImporterClient.class);

  private static final String FOLDER_PARAMETER = "f";
  private String folder = "/Themes";
  private String[] themes;
  private Boolean warnings = false;

  @Override
  protected void fillInOptions(Options options) {
    options.addOption(OptionBuilder.hasArg().withDescription("Folder within CoreMedia where themes are stored. Defaults to /Themes")
            .withLongOpt("folder")
            .create(FOLDER_PARAMETER));
  }

  @Override
  @Nonnull
  protected String getUsage() {
    return "cm import-themes -u <user> [other options] <theme.zip> ...";
  }

  @Override
  protected boolean parseCommandLine(CommandLine commandLine) {
    if (commandLine.hasOption(FOLDER_PARAMETER)) {
      folder = commandLine.getOptionValue(FOLDER_PARAMETER);
    }
    themes = commandLine.getArgs();
    if (themes == null || themes.length == 0) {
      getLogger().trace("Wrong argument for parameter t. Command line parsing marked as failure.");
      return false;
    }
    return true;
  }

  @Override
  protected void run() {
    ThemeImporter themeImporter = new ThemeImporter(connection, new DefaultMimeTypeService(true));
    themeImporter.importThemes(folder, collectThemeFiles());
    if (warnings) {
      LOG.warn("Done, with errors.");
    } else {
      LOG.info("Done.");
    }
  }

  private Collection<File> collectThemeFiles() {
    Collection<File> files = new ArrayList<>();
    for (String theme : themes) {
      File file = new File(theme);
      if (file.exists() && !file.isDirectory() && file.canRead()) {
        files.add(file);
      } else {
        LOG.warn("Cannot read theme {}, skipped.", theme);
        warnings = true;
      }
    }
    return files;
  }


  public static void main(String[] args) {
    Client.main(new ThemeImporterClient(), args);
  }
}
