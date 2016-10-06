// imports
var fs = require('fs');
var path = require('path');
var gruntTasks = require('load-grunt-tasks');

/**
 * Util function for grunt to load configs, tasks and bricks
 *
 * @type {{loadGruntTasks: module.exports.loadGruntTasks, loadGruntConfigs: module.exports.loadGruntConfigs, loadBrick: module.exports.loadBrick, loadBricks: module.exports.loadBricks}}
 */
var self = module.exports = {

  /**
   * Load all available grunt tasks in theme and frontend folder
   *
   * @param grunt
   * @param opts
   */
  loadGruntTasks: function (grunt, opts) {
    'use strict';

    opts = opts || {};
    var displayExecutionTime = opts.displayExecutionTime || true;

    grunt.verbose.writeln("Load grunt tasks via CoreMedia utils.");
    grunt.verbose.writeln("Display ExecutionTime of grunt in this theme: " + displayExecutionTime);

    // helper function to detect all parent folders with a package.json and installed node_modules folder.
    function findModuleDirectories(workingDirectory) {
      var result = [];
      var currentDirectory = workingDirectory;
      var deadLockProtection = 10;

      while (deadLockProtection > 0 && fs.existsSync(currentDirectory)) {
        var packageJsonFilePath = path.join(currentDirectory, "package.json");
        var nodeModulesFolderPath = path.join(currentDirectory, "node_modules");
        if (fs.existsSync(packageJsonFilePath) && fs.existsSync(nodeModulesFolderPath)) {
          grunt.verbose.writeln("Found possible grunt tasks in " + currentDirectory);
          result.push(currentDirectory);
        }
        currentDirectory = path.resolve(currentDirectory, "..");
        deadLockProtection--;
      }

      return result;
    }

    var workingDirectory = process.cwd();
    var moduleDirectories = findModuleDirectories(workingDirectory);

    // load grunt tasks in every folder via 'load-grunt-tasks' module
    for (var i = 0; i < moduleDirectories.length; i++) {
      var moduleDirectory = moduleDirectories[i];
      gruntTasks(grunt, {config: path.join(moduleDirectory, "package.json"), requireResolution: true});
    }

    // enable 'time-grunt' module display the elapsed execution time of grunt tasks
    if (displayExecutionTime === true) {
      require('time-grunt')(grunt);
    }
    grunt.log.oklns("Load global grunt tasks");
  },

  /**
   * Load all available predefined grunt configs for themes with given theme configuration
   *
   * @param grunt
   */
  loadGruntConfigs: function (grunt) {
    'use strict';

    var themeConfig = grunt.config.get("themeConfig") || {};
    var workingDirectory = process.cwd();
    var configDirectory = path.join(workingDirectory, "../../lib/grunt/configs");

    grunt.verbose.writeln("Load grunt configs via CoreMedia utils.");
    grunt.verbose.writeln("Used theme config: " + JSON.stringify(themeConfig));

    // check for config
    if (themeConfig.name === undefined || themeConfig.name === '') {
      grunt.log.errorlns("'themeConfig.name' is undefined. Please add it as mandatory theme configuration to your Gruntfile.js or package.json");
      return;
    }

    // check config files
    if (!fs.existsSync(configDirectory)) {
      grunt.log.errorlns("The global config files are not available in " + configDirectory);
      return;
    }

    grunt.verbose.writeln("Looking for configs in " + configDirectory);
    var options = {
      config: {
        src: configDirectory + "/*.js"
      },
      themeConfig: themeConfig
    };
    var configs = require('load-grunt-configs')(grunt, options);
    grunt.config.merge(configs);

    grunt.log.oklns("Load global grunt configs");
  },

  /**
   * Loads a brick to a theme
   *
   * @param grunt
   * @param {string} brick
   */
  loadBrick: function (grunt, brick) {
    'use strict';

    var workingDirectory = process.cwd();
    var brickDirectory = path.join(workingDirectory, "../../lib/bricks/", brick);
    var brickConfigFile = path.join(workingDirectory, "../../lib/bricks/", brick, "grunt-config.js");
    var brickTemplatesDestination = path.join(workingDirectory, "../../target/resources/WEB-INF/templates/bricks");
    var themeConfig = grunt.config.get("themeConfig") || {};

    grunt.verbose.writeln("Load brick via CoreMedia utils.");

    // check for config
    if (themeConfig.name === undefined || themeConfig.name === '') {
      grunt.log.errorlns("'themeConfig.name' is undefined. Please add it as mandatory theme configuration to your Gruntfile.js or package.json");
      return;
    }

    //check for brick
    if (brick === undefined || brick === '') {
      grunt.log.errorlns("Brick is undefined. Can't load it.");
      return;
    }

    // load config files
    if (!fs.existsSync(brickDirectory) || !fs.existsSync(brickConfigFile)) {
      grunt.log.errorlns("No Brick found in " + brickDirectory);
      return;
    }

    //load brick
    grunt.verbose.writeln("Looking for brick configs in " + brickDirectory);
    grunt.verbose.writeln("Destination for brick templates " + brickTemplatesDestination);
    var options = {
      config: {
        src: brickConfigFile
      },
      themeConfig: themeConfig,
      brickDirectory: brickDirectory,
      brickTemplatesDest: brickTemplatesDestination
    };
    var config = require('load-grunt-configs')(grunt, options);
    grunt.config.merge(config);

    grunt.log.oklns("Load grunt config for brick " + brick);
  },

  /**
   * Loads a list of bricks to a theme
   *
   * @param grunt
   * @param {string[]} bricks
   */
  loadBricks: function (grunt, bricks) {
    'use strict';

    //check for bricks
    if (!(Array.isArray(bricks)) || bricks.length < 1) {
      grunt.log.errorlns("No Bricks found");
      return;
    }

    //load all bricks - delegate to loadBrick()
    bricks.forEach(function (brick) {
      self.loadBrick(grunt, brick);
    });
  }
};
