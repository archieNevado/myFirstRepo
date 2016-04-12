module.exports = function (grunt) {
  'use strict';

  // Force use of Unix newlines
  grunt.util.linefeed = '\n';

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt, {scope: 'devDependencies'});
  require('time-grunt')(grunt);

  //global variables
  var globalConfig = {
    distDir: "target/resources/themes/basic"
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
        'src/js/noconflict.js',
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

  // Full distribution task without templates.
  grunt.registerTask('build', ['clean', 'copy']);

  // Full distribution task with templates.
  grunt.registerTask('buildWithTemplates', ['build', 'compress:templates']);

  // Test task (currently only alias for jasmine maybe other frameworks also want to be used)
  grunt.registerTask('test', ['jasmine']);

  // Default task = distribution + test.
  grunt.registerTask('default', ['build', 'test']);
};
