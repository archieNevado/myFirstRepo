'use strict';

module.exports = function (grunt) {

  // --- grunt initialization -------------------------------------------------------------------------------------------
  grunt.initConfig({
    jasmine: {
      src: [
        'lib/js/legacy/jquery.coremedia.utils.js',
        'lib/js/legacy/jquery.coremedia.smartresize.js',
        'lib/js/legacy/coremedia.blueprint.nodeDecorationService.js',
        'lib/js/legacy/coremedia.blueprint.basic.js',
        'lib/bricks/download-portal/js/coremedia.blueprint.am.downloadCollection.js'
      ],
      options: {
        vendor: [
          'node_modules/jquery/dist/jquery.js',
          'node_modules/jasmine-jquery/lib/jasmine-jquery.js'
        ],
        specs: [
          'lib/js/legacy/test/basic_test.js',
          'lib/bricks/download-portal/test/coremedia.blueprint.am.downloadCollectionTest.js'
        ]
      }
    }
  });

  // load CoreMedia initialization
  require('@coremedia/grunt-utils')(grunt);


  // --- tasks ---------------------------------------------------------------------------------------------------

  // Generate Markdown
  grunt.registerTask('apidoc', ['jsdoc2md']);

  // Test task
  grunt.registerTask('test', ['mochaTest', 'jasmine']);

  // Generate Default task
  grunt.registerTask('default', ['apidoc']);
};
