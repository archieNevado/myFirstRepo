'use strict';

module.exports = function (grunt) {

  // --- theme configuration -------------------------------------------------------------------------------------------

  grunt.initConfig({
    // define development mode
    /*monitor: {
     target: 'local' //default: remote
     },*/
    // load bricks into theme
    bricks: {
      src: [
        'preview',
        'responsive-images',
        'bootstrap',
        'generic-templates',
        'cta',
        'image-maps',
        'elastic-social',
        'fragment-scenario',
        'livecontext',
        'shoppable-video',
        'pdp-augmentation'
      ]
    },
    // copy js and vendor files
    copy: {
      basic: {
        files: [{
          expand: true,
          cwd: '../../lib/js/legacy',
          src: ['*.js'],
          dest: '../../target/resources/themes/<%= themeConfig.name %>/js'
        }]
      },
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
          src: 'jquery.min.js',
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

  // load CoreMedia initialization
  require('@coremedia/grunt-utils')(grunt);

  // --- theme tasks ---------------------------------------------------------------------------------------------------

  // Local Development Task.
  grunt.registerTask('development', ['clean', 'copy', 'sass', 'webpack:jslib']);
  // Full distribution task with templates.
  grunt.registerTask('production', ['development', 'postcss', 'eslint', 'compress']);

  // Default task = distribution.
  grunt.registerTask('default', ['production']);
};
