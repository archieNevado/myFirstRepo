module.exports = function (grunt) {
  'use strict';

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt);
  require('time-grunt')(grunt);

  // --- Project configuration ---
  grunt.initConfig({

    // --- Properties ---
    pkg: grunt.file.readJSON('package.json'),
    distDir: 'target/resources/themes/aurora',

    // --- Task configuration ---
    clean: {
      options: {
        force: true
      },
      build: ['<%=  distDir %>']
    },
    copy: {
      main: {
        files: [{
          expand: true,
          cwd: 'src/',
          src: ['css/**', 'fonts/**', 'img/**', 'js/**', 'vendor/**'],
          dest: '<%=  distDir %>/'
        }]
      },
      themedescriptor: {
        files: [{
          expand: true,
          src: '*-theme.xml',
          dest: 'target/resources/themes/THEME-METADATA/'
        }]
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
    compress: {
      theme: {
        options: {
          archive: 'target/aurora-theme.zip',
          mode: 'zip'
        },
        expand: true,
        dot: true,
        cwd: 'target/resources/themes',
        src: ['**']
      }
    }
  });

  // --- Tasks ---

  // Full distribution task
  grunt.registerTask('build', ['clean', 'copy', 'compress']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
