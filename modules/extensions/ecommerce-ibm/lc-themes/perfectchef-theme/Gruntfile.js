module.exports = function (grunt) {
  'use strict';

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt, {scope: 'devDependencies'});
  require('time-grunt')(grunt);

  //global configuration, used in tasks
  var globalConfig = {
    distDir: 'target/resources/themes/perfectchef',
    supportedBrowsers: [
      "last 2 versions", //see https://github.com/ai/browserslist#major-browsers
      "Firefox ESR",
      "Explorer >= 9"
    ]
  };

  // --- Project configuration ---
  grunt.initConfig({

    // --- Properties ---
    pkg: grunt.file.readJSON('package.json'),
    globalConfig: globalConfig,
    
    // --- Task configuration ---
    clean: {
      options: {
        force: true
      },
      build: ['<%=  globalConfig.distDir %>']
    },
    copy: {
      main: {
        files: [{
          expand: true,
          cwd: 'src/',
          src: ['css/**', 'fonts/**', 'img/**', 'js/**', 'vendor/**'],
          dest: '<%=  globalConfig.distDir %>/'
        }]
      },
      masonry: {
        files: [{
          expand: true,
          cwd: 'node_modules/masonry-layout/dist',
          src: '*.js',
          dest: '<%= globalConfig.distDir %>/vendor/'
        }]
      }
    },
    // and add browser prefixes (just to own css)
    postcss: {
      options: {
        map: true,
        processors: [
          require('autoprefixer')({browsers: globalConfig.supportedBrowsers})
        ]
      },
      dist: {
        src: [
          '<%= globalConfig.distDir %>/css/*.css'
        ]
      }
    },
    compress: {
      templates: {
        options: {
          archive: '<%=  globalConfig.distDir %>/templates/perfectchef-templates.jar',
          mode: 'zip'
        },
        expand: true,
        dot: true,
        cwd: 'src/main/resources/',
        src: ['**']
      }
    },
    watch: {
      options: {
        livereload: true // default port: 35729
      },
      sources: {
        files: 'src/**/*.*',
        tasks: ['copy', 'autoprefixer']
      }
    }
  });

  // --- Tasks ---

  // Full distribution task without templates.
  grunt.registerTask('build', ['clean', 'copy', 'postcss']);

  // Full distribution task with templates.
  grunt.registerTask('buildWithTemplates', ['build', 'compress:templates']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
