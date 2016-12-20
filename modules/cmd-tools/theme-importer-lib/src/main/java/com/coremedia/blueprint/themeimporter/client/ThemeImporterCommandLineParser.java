package com.coremedia.blueprint.themeimporter.client;

import com.coremedia.cmdline.CommandLineClient;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;

class ThemeImporterCommandLineParser extends CommandLineClient {
  private static final String FOLDER_PARAMETER = "f";

  String folder = ThemeImporterInitializer.REPOSITORY_FOLDER;
  String[] themes;

  @SuppressWarnings({
        /* squid:S2209/AccessStaticViaInstance:
         * static vs. instance: Unless updating commons-cli there is no other way to use the OptionBuilder.
         */
          "AccessStaticViaInstance",
          "squid:S2209"
  })
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
  protected boolean understandsVerbose() {
    return false;
  }
}
