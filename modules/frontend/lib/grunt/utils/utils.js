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
    var themeImporterConfig = grunt.config.get("themeImporterConfig") || {};
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
      themeConfig: themeConfig,
      themeImporterConfig: themeImporterConfig
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
  },

  /**
   * Generates a grunt config of a brick for jsdoc2md
   *
   * @param grunt
   * @param {string} brick
   */
  generateBrickJsdocConfig: function (grunt, brick) {
    'use strict';

    var workingDirectory = process.cwd();
    var brickDirectory = path.join(workingDirectory, "lib/bricks/", brick);
    var brickJsDirectory = path.join(brickDirectory, "js");

    function camelCase(s) {
      return s.toLowerCase().replace(/-(.)/g, function(match, group1) {
        return group1.toUpperCase();
      });
    }

    grunt.verbose.writeln("Generate grunt config for jsdoc2md of brick " + brick + " via CoreMedia utils.");

    // check for brick
    if (brick === undefined || brick === '') {
      grunt.log.errorlns("Brick is undefined. Can't load it.");
      return;
    }

    // check brick directory
    if (!fs.existsSync(brickDirectory)) {
      grunt.log.errorlns("No Brick found in " + brickDirectory);
      return;
    }

    // check brick JS directory
    if (!fs.existsSync(brickJsDirectory)) {
      grunt.log.errorlns("No Brick JS found in " + brickJsDirectory);
      return;
    }

    // create config
    var config = {
      jsdoc2md: {}
    };
    config.jsdoc2md['brick_' + camelCase(brick)] = {
      src: brickDirectory + '/js/*.js',
      dest: brickDirectory + '/API.md'
    };
    grunt.config.merge(config);

    grunt.log.oklns("Generated jsdoc2md grunt config for brick " + brick + ".");
  },

  /**
   * Generates grunt config for jsdoc2md grunt task for all bricks which include JavaScript
   *
   * @param grunt
   */
  generateBrickJsdocConfigs: function (grunt) {
    'use strict';

    var workingDirectory = process.cwd();
    var bricksRoot = path.join(workingDirectory, "lib/bricks/");

    // browse through all bricks - delegate to generateBrickJsdocConfig()
    fs.readdirSync(bricksRoot).forEach(function (file) {
      if (file !== '.' && file !== '..') {
        // check, if brick includes JavaScript
        if (fs.statSync(bricksRoot + file).isDirectory() && fs.existsSync(path.join(bricksRoot, file, "js"))) {
          // generate grunt config
          self.generateBrickJsdocConfig(grunt, file);
        }
      }
    });
  },

  /**
   * Generates a grunt config of a brick for jsdoc2md
   *
   * @param grunt
   * @param {string} brick
   */
  generateJSLibJsdocConfig: function (grunt, subdir) {
    'use strict';

    var workingDirectory = process.cwd();
    var jslibDirectory = path.join(workingDirectory, "lib/js/", subdir);

    function camelCase(s) {
      return s.toLowerCase().replace(/-(.)/g, function(match, group1) {
        return group1.toUpperCase();
      });
    }

    grunt.verbose.writeln("Generate grunt config for jsdoc2md of JS library " + subdir + " via CoreMedia utils.");

    // check for subdir
    if (subdir === undefined || subdir === '') {
      grunt.log.errorlns("Subdir is undefined. Can't load it.");
      return;
    }

    // check directory
    if (!fs.existsSync(jslibDirectory)) {
      grunt.log.errorlns("No JS library found in " + jslibDirectory);
      return;
    }

    // create config
    var config = {
      jsdoc2md: {}
    };
    config.jsdoc2md['jslib_' + camelCase(subdir)] = {
      src: jslibDirectory + '/**/*.js',
      dest: jslibDirectory + '/API.md'
    };
    grunt.config.merge(config);

    grunt.log.oklns("Generated jsdoc2md grunt config for JS library " + subdir + ".");
  },

  /**
   * Generates grunt config for jsdoc2md grunt task for all ES2015 modules in the lib/js directory
   *
   * @param grunt
   */
  generateJSLibJsdocConfigs: function (grunt) {
    'use strict';

    var workingDirectory = process.cwd();
    var jslibRoot = path.join(workingDirectory, "lib/js/");

    // browse through all bricks - delegate to generateBrickJsdocConfig()
    fs.readdirSync(jslibRoot).forEach(function (file) {
      if (file !== '.' && file !== '..') {
        // check, if brick includes JavaScript
        if (fs.statSync(jslibRoot + file).isDirectory()) {
          // generate grunt config
          self.generateJSLibJsdocConfig(grunt, file);
        }
      }
    });
  },

  /**
   * Generates a grunt config of a subdir of the lib/js directory for webpack
   *
   * @param grunt
   * @param {string} subdir
   */
  generateJSLibWebpackConfig: function (grunt, subdir) {
    'use strict';

    var workingDirectory = process.cwd();
    var jslibDirectory = path.join(workingDirectory, '../../lib/js/');
    var themeTargetJsLibDirectory = path.join(workingDirectory, '../../target/resources/themes/<%= themeConfig.name %>/js/lib');

    /*function capitalize(s) {
     return s.charAt(0).toUpperCase() + s.slice(1);
     }*/

    grunt.verbose.writeln("Generate grunt config for webpack of JS library " + subdir + " via CoreMedia utils.");

    // check for subdir
    if (subdir === undefined || subdir === '') {
      grunt.log.errorlns("Subdir is undefined. Can't load it.");
      return;
    }

    // check directory
    if (!fs.existsSync(path.join(jslibDirectory, subdir, 'index.js'))) {
      grunt.log.errorlns("No JS library found in " + path.join(jslibDirectory, subdir));
      return;
    }

    // create config
    var config = {
      webpack: {
        jslib: {}
      }
    };
    config.webpack.jslib = {
      entry: {
        [subdir]: path.join(jslibDirectory, subdir, 'index.js')
      },
      output: {
        path: themeTargetJsLibDirectory,
        filename: '[name].js',
        library: ['coremedia', 'blueprint', '[name]'],
        libraryTarget: 'umd'
      },
      progress: false,
      module: {
        preLoaders: [
          {
            loader: 'eslint-loader',
            test: /\.js$/,
            include: [
              jslibDirectory
            ]
          }
        ],
        loaders: [
          {
            loader: 'babel-loader',
            test: /\.js$/,
            include: [
              jslibDirectory
            ],
            query: {
              cacheDirectory: true,
              comments: false,
              plugins: ['add-module-exports', 'transform-runtime']
            }
          }
        ]
      }
    };
    grunt.config.merge(config);

    grunt.log.oklns("Generated webpack grunt config for JS library " + subdir + ".");
  },

  /**
   * Generates grunt config for webpack grunt task for all ES2015 modules in the lib/js directory
   *
   * @param grunt
   */
  generateJSLibWebpackConfigs: function (grunt) {
    'use strict';

    var workingDirectory = process.cwd();
    var jslibRoot = path.join(workingDirectory, "../../lib/js/");

    // browse through all bricks - delegate to generateJSLibWebpackConfig()
    fs.readdirSync(jslibRoot).forEach(function (file) {
      if (file !== '.' && file !== '..' && file !== 'utils') {
        // check, if brick includes JavaScript
        if (fs.statSync(jslibRoot + file).isDirectory()) {
          // generate grunt config
          self.generateJSLibWebpackConfig(grunt, file);
        }
      }
    });
  },

  /**
   * Generates a grunt config of a jslib for mocha
   *
   * @param grunt
   * @param {string} subdir
   */
  generateJSLibMochaConfig: function(grunt, subdir) {
    'use strict';

    var workingDirectory = process.cwd();
    var jslibDirectory = path.join(workingDirectory, 'lib/js/');

    /*function capitalize(s) {
     return s.charAt(0).toUpperCase() + s.slice(1);
     }*/

    grunt.verbose.writeln("Generate grunt config for mocha tests of JS library " + subdir + " via CoreMedia utils.");

    // check for subdir
    if (subdir === undefined || subdir === '') {
      grunt.log.errorlns("Subdir is undefined. Can't load it.");
      return;
    }

    // check directory
    if (!fs.existsSync(path.join(jslibDirectory, subdir))) {
      grunt.log.errorlns("No JS library found in " + path.join(jslibDirectory, subdir));
      return;
    }

    // create config
    var config = {
      mochaTest: {
        test: {}
      }
    };
    config.mochaTest.test = {
      options: {
        reporter: 'spec',
        require: 'babel-register'
      },
      src: [path.join(jslibDirectory, subdir, 'test/*_spec.js')]
    };
    grunt.config.merge(config);

    grunt.log.oklns("Generated mocha test grunt config for JS library " + subdir + ".");
  },

  /**
   * Generates grunt config for mocha grunt task for all ES2015 modules in the lib/js directory
   *
   * @param grunt
   */
  generateJSLibMochaConfigs: function(grunt) {
    'use strict';

    var workingDirectory = process.cwd();
    var jslibRoot = path.join(workingDirectory, 'lib/js/');

    // browse through all bricks - delegate to generateJSLibWebpackConfig()
    fs.readdirSync(jslibRoot).forEach(function (file) {
      if (file !== '.' && file !== '..' && file !== 'utils') {
        // check, if brick includes JavaScript
        if (fs.statSync(jslibRoot + file).isDirectory()) {
          // generate grunt config
          self.generateJSLibMochaConfig(grunt, file);
        }
      }
    });
  }
};
