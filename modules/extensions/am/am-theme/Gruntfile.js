module.exports = function (grunt) {
  'use strict';

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt);
  require('time-grunt')(grunt);

  // --- Project configuration ---
  grunt.initConfig({

    // --- Properties ---
    pkg: grunt.file.readJSON('package.json'),
    distDir: 'target/resources/themes/am',
    autoprefixerBrowsers: [
      "last 2 versions",
      "Firefox >= 45",
      "Explorer >= 9",
      "Safari >= 8"
    ],

    // --- Task configuration ---
    clean: {
      options: {
        force: true
      },
      build: ['<%=  distDir %>']
    },
    sass: {
      options: {
        sourceMap: true,
        outputStyle: "expanded"
      },
      build: {
        files: {
          '<%=  distDir %>/css/am.css': 'src/sass/am.scss'
        }
      }
    },
    watch: {
      scss: {
        files: 'src/sass/**/*.scss',
        tasks: ['sass', 'autoprefixer'],
        options: {
          livereload: true // default port: 35729
        }
      },
      css: {
        files: 'src/css/*.css',
        tasks: ['copy:css'],
        options: {
          livereload: true
        }
      },
      js: {
        files: 'src/js/*.js',
        tasks: ['copy:javascripts'],
        options: {
          livereload: true
        }
      },
      ftl: {
        files: 'src/main/resources/**/*.ftl',
        options: {
          livereload: true
        }
      },
      images: {
        files: 'src/images/**/*.*',
        tasks: ['copy:images'],
        options: {
          livereload: true
        }
      }
    },
    copy: {
      css: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: [
          'src/css/*'
        ],
        dest: '<%=  distDir %>/css/'
      },
      javascripts: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: [
          'src/js/*'
        ],
        dest: '<%=  distDir %>/js/'
      },
      images: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: ['src/images/**'],
        dest: '<%=  distDir %>/images/'
      }
    },
    autoprefixer: {
      options: {
        browsers: '<%= autoprefixerBrowsers %>',
        map: true
      },
      build: {
        src: '<%= distDir %>/css/am.css'
      }
    },
    compress: {
      templates: {
        options: {
          archive: '<%=  distDir %>/templates/am-templates.jar',
          mode: 'zip'
        },
        expand: true,
        dot: true,
        cwd: 'src/main/resources/',
        src: ['**']
      }
    },
    styledocco: {
      build: {
        options: {
          cmd: "./node_modules/.bin/coremedia-styledocco",
          name: 'Coremedia Corporation'
        },
        files: {
          'docs/styleguide': '<%= distDir %>/css/am.css'
        }
      }
    },
    jasmine: {
      src: [
        'src/js/coremedia.blueprint.am.downloadCollection.js'
      ],
      options: {
        vendor: [
          'node_modules/jquery/dist/jquery.js',
          'node_modules/jasmine-jquery/lib/jasmine-jquery.js'
        ],
        specs : [
          'test/js/coremedia.blueprint.am.downloadCollectionTest.js'
        ]
      }
    }
  });

  // --- Tasks ---

  // Full distribution task with templates.
  grunt.registerTask('build', ['clean', 'sass', 'copy', 'autoprefixer', 'compress:templates']);

  // Test task
  grunt.registerTask('test', ['jasmine']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
