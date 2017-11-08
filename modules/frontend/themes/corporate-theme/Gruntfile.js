'use strict';

const themeutils = require('@coremedia/themeutils');

module.exports = function (grunt) {

  // load CoreMedia initialization
  themeutils(grunt);

  // --- theme tasks ---------------------------------------------------------------------------------------------------

  // use task "monitor" for development. will be created by @coremedia/themeutils

  // deprecated, use "build" instead
  grunt.registerTask('production', ['build']);

  // build the theme. theme will be stored as zip file in <%= themeConfig.targetPath %>/themes
  grunt.registerTask('build', ['clean', 'copy', 'sync', 'webpack', 'compress']);

  // Default task = build
  grunt.registerTask('default', ['build']);
};
