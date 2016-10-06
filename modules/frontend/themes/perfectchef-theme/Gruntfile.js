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
  utils.loadBricks(grunt, ["preview", "responsive-images", "bootstrap", "generic-templates", "cta", "image-maps", "elastic-social", "fragment-scenario", "livecontext"]);

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
          '../../target/resources/themes/<%= themeConfig.name %>/css/perfectchef.css': 'src/sass/perfectchef.scss',
          '../../target/resources/themes/<%= themeConfig.name %>/css/fall.css': 'src/sass/fall.scss',
          '../../target/resources/themes/<%= themeConfig.name %>/css/spring.css': 'src/sass/spring.scss',
          '../../target/resources/themes/<%= themeConfig.name %>/css/preview.css': 'src/sass/preview.scss'
        }
      }
    },
    copy: {
      imagesloaded: {
        files: [{
          expand: true,
          cwd: 'node_modules/imagesloaded',
          src: 'imagesloaded.pkgd.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
        }]
      },
      jquery: {
        files: [{
          expand: true,
          cwd: 'node_modules/jquery/dist',
          src: 'jquery.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
        }]
      },
      magnificpopup: {
        files: [{
          expand: true,
          cwd: 'node_modules/magnific-popup/dist',
          src: 'jquery.magnific-popup.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
        }]
      },
      mediaelement: {
        files: [
          {
            expand: true,
            cwd: 'node_modules/mediaelement/build',
            src: ['flashmediaelement.swf', 'mediaelement-and-player.js'],
            dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
          },
          {
            expand: true,
            cwd: 'node_modules/mediaelement/build',
            src: ['*.png', '*.svg', '*.gif'],
            dest: '../../target/resources/themes/<%= themeConfig.name %>/css/'
          }]
      }
    }
  });

// --- theme tasks ---------------------------------------------------------------------------------------------------

// Full distribution task with templates.
  grunt.registerTask('build', ['clean', 'copy', 'sass', 'postcss', 'compress']);

// Default task = distribution.
  grunt.registerTask('default', ['build']);
}
;
