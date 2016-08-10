module.exports = function (grunt) {
  'use strict';

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt);
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
      themedescriptor: {
        files: [{
          expand: true,
          src: '*-theme.xml',
          dest: 'target/resources/themes/THEME-METADATA/'
        }]
      },
      masonry: {
        files: [{
          expand: true,
          cwd: 'node_modules/masonry-layout/dist',
          src: '*.js',
          dest: '<%= globalConfig.distDir %>/vendor/'
        }]
      },
      localizations:{
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: ['src/*.properties'],
        dest: '<%= globalConfig.distDir %>/'
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
      },
      theme: {
        options: {
          archive: 'target/perfectchef-theme.zip',
          mode: 'zip'
        },
        expand: true,
        dot: true,
        cwd: 'target/resources/themes',
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

  // Full distribution task with templates.
  grunt.registerTask('build', ['clean', 'copy', 'postcss', 'compress']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
