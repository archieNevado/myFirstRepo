'use strict';

/**
 * Register monitor grunt task to watch file changes and update theme on local CAE.
 * @param grunt
 * @param {{ host: string, port: number, key: string=, cert: string= }} livereloadConfig
 * @private
 */
const registerLocalMonitorTask = (grunt, livereloadConfig) => {
  grunt.verbose.writeln('Generate grunt config for watch task and register alias task monitor.');

  // watch task monitor:local task for local CAE preview
  grunt.registerTask('monitor', 'Watch file changes and update theme on local CAE.', () => {
      grunt.log.writeln('Watch file changes for local CAE...');
      grunt.verbose.writeln('Execute monitor task.');

      const config = {
        watch: {
          options: {
            spawn: false,
            livereload: livereloadConfig
          },
          sass: {
            options: {
              spawn: true,
              livereload: false
            },
            files: 'src/sass/**/*.scss',
            tasks: ['sass']
          },
          css: {
            files: 'src/css/**/*.css',
            tasks: ['copy:main']
          },
          js: {
            files: 'src/js/*.js',
            tasks: ['copy:main']
          },
          ftl: {
            files: 'src/templates/**/*.ftl',
            tasks: ['copy:templates']
          },
          bundles: {
            files: 'src/l10n/**/*.properties',
            tasks: ['copy:main']
          },
          others: {
            files: ['fonts/**', 'img/**', 'images/**', 'js/**', 'vendor/**'],
            tasks: ['copy:main']
          },
          productionCss: {
            options: {
              spawn: true
            },
            files: '../../target/resources/themes/<%= themeConfig.name %>/css/*.css',
            tasks: []
          }
        }
      };
      grunt.config.merge(config);
      grunt.task.run('watch');
    }
  );
};

/**
 * Register monitor grunt task to watch file changes and update theme on remote CAE.
 * @param grunt
 * @param {{ host: string, port: number, key: string=, cert: string= }} livereloadConfig
 * @private
 */
const registerRemoteMonitorTask = (grunt, livereloadConfig) => {
  grunt.verbose.writeln('Generate grunt config for watch task and register alias task monitor.');

  // monitor task for uploading changes in target directory to remote CAE
  grunt.registerTask('monitor', 'Watch file changes and update theme on remote CAE.', function() {
    grunt.log.writeln('Watch file changes for remote CAE...');
    grunt.verbose.writeln('Execute remote monitor task.');

    const { livereload } = require('@coremedia/common');
    const { addWatchEventListener } = require('./lib/monitor');

    const logger = {
      log: grunt.verbose.writeln,
      info: grunt.log.writeln,
      error: grunt.log.error
    };

    livereload.init(livereloadConfig, logger);
    addWatchEventListener(grunt, livereload);

    const config = {
      watch: {
        options: {
          livereload: false,
          dateFormat: function() {
            //hide default done message
          }
        },
        sass: {
          files: 'src/sass/**/*.scss',
          tasks: ['sass']
        },
        ftl: {
          files: 'src/templates/**/*.ftl',
          tasks: ['compress:templates']
        },
        themeDescriptor: {
          files: '*-theme.xml',
          tasks: ['copy:themedescriptor']
        },
        themeChanged: {
          options: {
            event: ['added', 'changed']
          },
          files: [
            'src/css/**/*.css',
            'src/js/**/*.js',
            'src/l10n/**/*.properties',
            'src/fonts/**',
            'src/img/**'
          ],
          tasks: ['copy:main']
        },
        themeDeleted: {
          options: {
            event: ['deleted']
          },
          files: [
            'src/css/**/*.css',
            'src/js/**/*.js',
            'src/l10n/**/*.properties',
            'src/fonts/**',
            'src/img/**'
          ],
          tasks: ['clean:filelist']
        },
        targetDescriptor: {
          options: {
            event: ['added', 'changed']
          },
          files: '../../target/resources/THEME-METADATA/<%= themeConfig.name %>-theme.xml',
          tasks: []
        },
        targetChanged: {
          options: {
            event: ['added', 'changed']
          },
          files: '../../target/resources/themes/<%= themeConfig.name %>/**/*.*',
          tasks: []
        },
        targetDeleted: {
          options: {
            event: ['deleted']
          },
          files: '../../target/resources/themes/<%= themeConfig.name %>/**/*.*',
          tasks: []
        }
      }
    };
    grunt.config.merge(config);

    const tasklist = ['remoteThemeImporter:uploadTheme', 'watch'];

    try {
      const { remoteThemeImporter } = require('@coremedia/common');
      const { apiKey } = remoteThemeImporter.getCMConfig();
      if (typeof apiKey !== 'string' || apiKey.length === 0) {
        throw new Error();
      }

      const done = this.async();
      remoteThemeImporter.whoami(
        logger
      ).then(msg => {
        grunt.log.writeln(msg);
        grunt.task.run(tasklist);
        done();
      }).catch(() => {
        tasklist.unshift('remoteThemeImporter:login');
        grunt.log.error('API key has been expired. Please enter your credentials to generate a new API key.');
        grunt.task.run(tasklist);
        done();
      });
    } catch (e) {
      tasklist.unshift('remoteThemeImporter:login');
      grunt.log.error('API key file doesn´t exist. Please enter your credentials to generate your API key.');
      grunt.task.run(tasklist);
    }
  });
};

/**
 * Generates grunt config and task for watch grunt task.
 */
module.exports = grunt => {
  grunt.verbose.writeln('Generate grunt config and tasks for watch grunt task.');

  const { getMonitorConfig } = require('./lib/monitor');

  const monitorConfig = {};
  try {
    const config = getMonitorConfig(grunt);
    Object.assign(monitorConfig, config);
  } catch (e) {
    grunt.fail.warn(e);
  }

  if (monitorConfig.target === 'local') {
    registerLocalMonitorTask(grunt, monitorConfig.livereload);
  } else {
    registerRemoteMonitorTask(grunt, monitorConfig.livereload);
  }

  grunt.verbose.oklns('Generated grunt config and tasks for watch grunt task.');
};
