module.exports = function (grunt) {
  'use strict';

  // Force use of Unix newlines
  grunt.util.linefeed = '\n';

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt, {scope: 'devDependencies'});
  require('time-grunt')(grunt);

  //global variables
  var globalConfig = {
    distDir: 'target/resources/themes/corporate',
    imports: {
      bootstrap: 'node_modules/bootstrap-sass/assets',
      bootstrapSwipe: 'node_modules/bootstrap-carousel-swipe'
    },
    vendor: {
      jquery: 'jquery/dist',
      magnificpopup: 'magnific-popup/dist',
      svg4everybody: 'svg4everybody/dist',
      imagesloaded: 'imagesloaded'
    }
  };

  // --- Project configuration ---
  grunt.initConfig({

    // --- Properties ---
    pkg: grunt.file.readJSON('package.json'),
    globalConfig: globalConfig,
    autoprefixerBrowsers: [
      "last 2 versions",
      "Firefox >= 38", //last esr version
      "Explorer >= 9",
      "Safari >= 8"
    ],

    // --- Task configuration ---
    clean: {
      options: {
        force: true
      },
      build: ['<%= globalConfig.distDir %>']
    },
    // generate css files
    sass: {
      options: {
        outputStyle: "expanded",
        sourceMap: true
      },
      build: {
        files: {
          '<%= globalConfig.distDir %>/css/bootstrap.css': 'src/sass/bootstrap.scss',
          '<%= globalConfig.distDir %>/css/corporate.css': 'src/sass/corporate.scss',
          '<%= globalConfig.distDir %>/css/preview.css': 'src/sass/preview.scss'
        }
      }
    },
    // and add browser prefixes (just to own css)
    autoprefixer: {
      options: {
        browsers: '<%= autoprefixerBrowsers %>',
        map: true
      },
      build: {
        src: '<%= globalConfig.distDir %>/css/corporate.css'
      }
    },
    watch: {
      options: {
        livereload: true // default port: 35729
      },
      css: {
        files: 'src/sass/**/*.scss',
        tasks: ['sass', 'autoprefixer']
      },
      js: {
        files: 'src/js/*.js',
        tasks: ['copy:javascripts']
      },
      ftl: {
        files: 'src/main/resources/**/*.ftl'
      },
      images: {
        files: 'src/images/**/*.*',
        tasks: ['copy:images']
      }
    },
    // copy sources to target folder
    copy: {
      fonts: {
        files: [
          {
            expand: true,
            flatten: true,
            src: ['<%= globalConfig.imports.bootstrap %>/fonts/**'],
            dest: '<%= globalConfig.distDir %>/fonts/bootstrap/',
            filter: 'isFile'
          },
          {
            expand: true,
            flatten: true,
            src: ['src/fonts/*'],
            dest: '<%= globalConfig.distDir %>/fonts/',
            filter: 'isFile'
          }
        ]
      },
      javascripts: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: [
          'src/js/*',
          '<%= globalConfig.imports.bootstrap %>/javascripts/bootstrap.js',
          '<%= globalConfig.imports.bootstrapSwipe %>/carousel-swipe.js'
        ],
        dest: '<%= globalConfig.distDir %>/js/'
      },
      images: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: ['src/images/**'],
        dest: '<%= globalConfig.distDir %>/images/'
      },
      vendor: {
        files: [{
          expand: true,
          cwd: 'node_modules',
          src: [
            '<%= globalConfig.vendor.jquery %>/**',
            '<%= globalConfig.vendor.magnificpopup %>/**',
            '<%= globalConfig.vendor.svg4everybody %>/**',
            '<%= globalConfig.vendor.imagesloaded %>/imagesloaded.pkgd.js'
          ],
          dest: '<%= globalConfig.distDir %>/vendor/'
        }]
      }
    },
    // generate template archive
    compress: {
      templates: {
        options: {
          archive: '<%= globalConfig.distDir %>/templates/corporate-templates.jar',
          mode: 'zip'
        },
        expand: true,
        dot: true,
        cwd: 'src/main/resources/',
        src: ['**']
      }
    }
  });

  // --- Tasks ---

  // Full distribution task without templates.
  grunt.registerTask('build', ['clean', 'copy', 'sass', 'autoprefixer']);

  // Full distribution task with templates.
  grunt.registerTask('buildWithTemplates', ['build', 'compress:templates']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
