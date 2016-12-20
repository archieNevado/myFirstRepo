'use strict';

// import coremedia utils to simply reuse all available grunt tasks.
var utils = require('@coremedia/utils');

module.exports = function (grunt) {

  // init configuration
  grunt.config.init({});

  // load all available tasks
  utils.loadGruntTasks(grunt);

  // generate jsdoc configs for bricks
  utils.generateBrickJsdocConfigs(grunt);

  // generate jsdoc configs for js libs
  utils.generateJSLibJsdocConfigs(grunt);

  // generate mocha configs for js libs
  utils.generateJSLibMochaConfigs(grunt);

  // legacy jasmine tests
  grunt.config.merge({
    jasmine: {
      src: [
        'lib/js/legacy/jquery.coremedia.utils.js',
        'lib/js/legacy/jquery.coremedia.smartresize.js',
        'lib/js/legacy/coremedia.blueprint.nodeDecorationService.js',
        'lib/js/legacy/coremedia.blueprint.basic.js'
      ],
      options: {
        vendor: [
          'node_modules/jquery/dist/jquery.js',
          'node_modules/jasmine-jquery/lib/jasmine-jquery.js'
        ],
        specs: [
          'lib/js/legacy/test/basic_test.js'
        ]
      }
    }
  });

  // --- tasks ---------------------------------------------------------------------------------------------------

  // Generate Markdown
  grunt.registerTask('apidoc', ['jsdoc2md']);

  // Test task
  grunt.registerTask('test', ['mochaTest', 'jasmine']);

  // Generate Default task
  grunt.registerTask('default', ['apidoc']);
};
