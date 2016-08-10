module.exports = function (grunt) {
  'use strict';

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt);
  require('time-grunt')(grunt);

  //global configuration, used in tasks
  var globalConfig = {
    distDir: "target/resources/themes/basic",
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
      imagesloaded: {
        files: [{
          expand: true,
          cwd: 'node_modules/imagesloaded',
          src: 'imagesloaded.pkgd.js',
          dest: '<%= globalConfig.distDir %>/vendor/'
        }]
      },
      normalize: {
        files: [{
          expand: true,
          cwd: 'node_modules/normalize.css',
          src: 'normalize.css',
          dest: '<%= globalConfig.distDir %>/vendor/'
        }]
      },
      jquery: {
        files: [{
          expand: true,
          cwd: 'node_modules/jquery/dist',
          src: 'jquery.js',
          dest: '<%= globalConfig.distDir %>/vendor/'
        }]
      },
      magnificpopup: {
        files: [{
          expand: true,
          cwd: 'node_modules/magnific-popup/dist',
          src: '*',
          dest: '<%= globalConfig.distDir %>/vendor/'
        }]
      },
      mediaelement: {
        files: [{
          expand: true,
          cwd: 'node_modules/mediaelement/build',
          src: '*',
          dest: '<%= globalConfig.distDir %>/vendor/mediaelement'
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
          archive: '<%=  globalConfig.distDir %>/templates/basic-templates.jar',
          mode: 'zip'
        },
        expand: true,
        dot: true,
        cwd: 'src/main/resources/',
        src: ['**']
      },
      theme: {
        options: {
          archive: 'target/basic-theme.zip',
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
        tasks: ['copy']
      }
    },
    jasmine: {
      src : [
        'src/js/jquery.coremedia.utils.js',
        'src/js/jquery.coremedia.smartresize.js',
        'src/js/coremedia.blueprint.nodeDecorationService.js',
        'src/js/coremedia.blueprint.basic.js'
      ],
      options: {
        vendor: [
          'node_modules/jquery/dist/jquery.js',
          'node_modules/jasmine-jquery/lib/jasmine-jquery.js'
        ],
        specs : [
          'test/js/coremedia.blueprint.basicTest.js'
        ]
      }
    }
  });

  // --- Tasks ---

  // Full distribution task with templates.
  grunt.registerTask('build', ['clean', 'copy', 'postcss', 'compress']);

  // Test task
  grunt.registerTask('test', ['jasmine']);

  // Default task = distribution
  grunt.registerTask('default', ['build']);
};
