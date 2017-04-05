module.exports = function (grunt) {
  'use strict';

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
        'cta',
        'generic-templates',
        'image-maps',
        'elastic-social',
        'fragment-scenario',
        'download-portal'
      ]
    },
    // copy js and vendor files
    copy: {
      basic: {
        files: [{
          expand: true,
          cwd: '../../lib/js/legacy',
          src: [
            'jquery.coremedia.utils.js',
            'jquery.coremedia.smartresize.js',
            'jquery.coremedia.spinner.js',
            'coremedia.blueprint.nodeDecorationService.js',
            'coremedia.blueprint.basic.js',
            'coremedia.blueprint.hashBasedFragment.js'
          ],
          dest: '../../target/resources/themes/<%= themeConfig.name %>/js'
        }]
      },
      vendor: {
        files: [{
          expand: true,
          cwd: 'node_modules',
          src: [
            'magnific-popup/dist/jquery.magnific-popup.js',
            'svg4everybody/dist/**',
          ],
          dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
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
