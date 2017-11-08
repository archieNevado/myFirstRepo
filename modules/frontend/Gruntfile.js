'use strict';

module.exports = function (grunt) {

  // load CoreMedia initialization
  require('@coremedia/themeutils')(grunt);

  // --- tasks ---------------------------------------------------------------------------------------------------

  // Generate Markdown
  grunt.registerTask('apidoc', ['jsdoc2md']);

  // Generate Default task
  grunt.registerTask('default', ['apidoc']);
};
