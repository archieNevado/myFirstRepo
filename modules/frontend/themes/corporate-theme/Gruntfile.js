// import coremedia utils to simply reuse all available grunt tasks.
var utils = require('@coremedia/utils');

module.exports = function (grunt) {
  'use strict';

  // init configuration
  grunt.config.init({
    // theme specific parameters, "name" is mandatory
    themeConfig: grunt.file.readJSON('package.json').theme
  });

  // load all available tasks
  utils.loadGruntTasks(grunt);

  // load all available default configs
  utils.loadGruntConfigs(grunt);

  // generate webpack configs for JavaScript modules in lib/js directory
  utils.generateJSLibWebpackConfigs(grunt);

  // load bricks to extend theme
  utils.loadBricks(grunt, [
    "preview",
    "responsive-images",
    "bootstrap",
    "cta",
    "generic-templates",
    "image-maps",
    "elastic-social",
    "fragment-scenario"
  ]);


  // --- theme configuration -------------------------------------------------------------------------------------------

  grunt.config.merge({
    // generate css files
    sass: {
      options: {
        outputStyle: "expanded",
        sourceMap: true,
        sourceMapRoot: 'file://' + process.cwd() + 'target/resources/themes/../<%= themeConfig.name %>/css'
      },
      build: {
        files: {
          '../../target/resources/themes/<%= themeConfig.name %>/css/corporate.css': 'src/sass/corporate.scss',
          '../../target/resources/themes/<%= themeConfig.name %>/css/preview.css': 'src/sass/preview.scss'
        }
      }
    },
    // copy js and vendor files
    copy: {
      basic: {
        files: [{
          expand: true,
          cwd: '../../lib/js/legacy',
          src: [
            'jquery.coremedia.utils.js',
            'jquery.coremedia.smartresize.js',
            'jquery.coremedia.spinner.js',
            'coremedia.blueprint.nodeDecorationService.js',
            'coremedia.blueprint.basic.js',
            'coremedia.blueprint.hashBasedFragment.js'
          ],
          dest: '../../target/resources/themes/<%= themeConfig.name %>/js'
        }]
      },
      vendor: {
        files: [{
          expand: true,
          cwd: 'node_modules',
          src: [
            'magnific-popup/dist/jquery.magnific-popup.js',
            'svg4everybody/dist/**',
          ],
          dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
        }]
      }
    }
  });

  // --- theme tasks ---------------------------------------------------------------------------------------------------

  // Local Development Task.
  grunt.registerTask('development', ['clean', 'copy', 'sass', 'webpack:jslib']);
  // Full distribution task with templates.
  grunt.registerTask('production', ['development', 'postcss', 'eslint', 'compress']);

// Default task = distribution.
  grunt.registerTask('default', ['production']);
};
