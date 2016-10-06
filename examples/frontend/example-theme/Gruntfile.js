'use strict';

// import coremedia utils to simply reuse all available grunt tasks.
var utils = require('@coremedia/utils');

module.exports = function (grunt) {

  // init configuration
  grunt.config.init({
    // theme specific parameters, "name" is mandatory
    themeConfig: grunt.file.readJSON('package.json').theme
  });

  // load all available tasks
  utils.loadGruntTasks(grunt);

  // load all available default configs
  utils.loadGruntConfigs(grunt);

  // --- theme configuration -------------------------------------------------------------------------------------------

  // add your example tasks and configs here

  // --- theme tasks ---------------------------------------------------------------------------------------------------

  // Full distribution task.
  grunt.registerTask('build', ['clean', 'copy', 'postcss', 'compress']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
