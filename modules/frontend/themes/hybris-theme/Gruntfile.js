'use strict';

const themeutils = require('@coremedia/themeutils');

module.exports = function (grunt) {

  // --- theme configuration -------------------------------------------------------------------------------------------

  // jquery and bootstrap are part of Hybris default shop, as it will be attached to jQuery use it as export
  const webpackTheme = require("@coremedia/themeutils/webpack.config.js");
  webpackTheme.externals = webpackTheme.externals || {};
  webpackTheme.externals["jquery"] = "jQuery";
  webpackTheme.externals["bootstrap-sass"] = "jQuery";

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
