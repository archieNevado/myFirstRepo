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

  // load bricks to extend theme
  utils.loadBricks(grunt, ["preview", "responsive-images", "bootstrap", "cta", "generic-templates", "image-maps", "elastic-social"]);


  // --- theme configuration -------------------------------------------------------------------------------------------

  grunt.config.merge({
    // generate css files
    sass: {
      options: {
        outputStyle: "expanded",
        sourceMap: true,
        sourceMapRoot: 'file://' + process.cwd()+'target/resources/themes/../<%= themeConfig.name %>/css'
      },
      build: {
        files: {
          '../../target/resources/themes/<%= themeConfig.name %>/css/bootstrap.css': 'src/sass/bootstrap.scss',
          '../../target/resources/themes/<%= themeConfig.name %>/css/corporate.css': 'src/sass/corporate.scss',
          '../../target/resources/themes/<%= themeConfig.name %>/css/preview.css': 'src/sass/preview.scss'
        }
      }
    },
    // copy sources to target folder
    copy: {
      vendor: {
        files: [{
          expand: true,
          cwd: 'node_modules',
          src: [
            'jquery/dist/**',
            'magnific-popup/dist/jquery.magnific-popup.js',
            'svg4everybody/dist/**',
            'imagesloaded/imagesloaded.pkgd.js'
          ],
          dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
        }]
      }
    }
  });

  // --- theme tasks ---------------------------------------------------------------------------------------------------

  // Full distribution task with templates.
  grunt.registerTask('build', ['clean', 'copy', 'sass', 'postcss', 'jshint', 'compress']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
