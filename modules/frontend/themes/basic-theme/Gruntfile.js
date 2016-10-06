'use strict';

// import coremedia utils to simply reuse all available grunt tasks.
var utils = require('@coremedia/utils');

module.exports = function (grunt) {

  // init configuration
  grunt.config.init({
    // theme specific parameters, "name" is mandatory
    themeConfig:  grunt.file.readJSON('package.json').theme
  });

  // load all available tasks
  utils.loadGruntTasks(grunt);

  // load all available default configs
  utils.loadGruntConfigs(grunt);

  // --- theme configuration -------------------------------------------------------------------------------------------

  grunt.config.merge({
    jasmine: {
      src: [
        'src/js/jquery.coremedia.utils.js',
        'src/js/jquery.coremedia.smartresize.js',
        'src/js/coremedia.blueprint.nodeDecorationService.js',
        'src/js/coremedia.blueprint.basic.js'
      ],
      options: {
        vendor: [
          '../../node_modules/jquery/dist/jquery.js',
          '../../node_modules/jasmine-jquery/lib/jasmine-jquery.js'
        ],
        specs: [
          'test/js/coremedia.blueprint.basicTest.js'
        ]
      }
    }
  });

  // --- theme tasks ---------------------------------------------------------------------------------------------------

  // Full distribution task with templates.
  grunt.registerTask('build', ['clean', 'copy:main', 'postcss', 'compress:theme']);

  // Test task
  grunt.registerTask('test', ['jasmine']);

  // Default task = distribution + test
  grunt.registerTask('default', ['build', 'test']);
};
