var fs = require('fs');
var path = require('path');

module.exports = function (grunt, options) {
  'use strict';

  var ext = "cm.exe";

  if (process.platform === "win32" && process.arch === "x64") {
    ext = "cm64.exe";
  } else {
    ext = "cm";
  }

  var command = {};
  var toolDirectory = path.resolve(process.cwd(), "../../../cmd-tools/theme-importer-application/target/theme-importer/bin/" + ext);
  var themeDirectory = path.resolve(process.cwd(), "../../target/themes/" + options.themeConfig.name + "-theme.zip");

  if (fs.existsSync(toolDirectory) && fs.existsSync(themeDirectory)) {
    var importerUser = " -u " + options.themeImporterConfig.themeImporterUser || "admin";
    var importerPassword = " -p " + options.themeImporterConfig.themeImporterPassword || "admin";
    var importerIor = "";
    if(options.themeImporterConfig.themeImporterIor){
      importerIor = " -url " + options.themeImporterConfig.themeImporterIor;
    }
    command = toolDirectory + ' import-themes' + importerUser + importerPassword + importerIor + ' ' + themeDirectory;
  } else if (!fs.existsSync(toolDirectory)) {
    grunt.log.errorlns("Cannot run theme importer. Directory for importer cannot be found: " + toolDirectory);
  }

  return {
    import_theme: {
      command: command
    }
  };
};
