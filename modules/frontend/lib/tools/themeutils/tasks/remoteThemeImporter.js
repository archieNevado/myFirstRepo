'use strict';

/**
 * Register grunt task for managing remote file operations of themes
 */
module.exports = grunt => {
  grunt.verbose.writeln('Register grunt task for managing remote file operations of themes.');

  grunt.registerTask('remoteThemeImporter', 'Grunt Task for remote file operations of themes. (login|logout|whoami|uploadTheme)', function(subtask) {
    if (!subtask) {
      grunt.fail.fatal(`No target for task ${this.name} provided.`);
    }

    const path = require('path');

    const { getEnv } = require('./lib/environment');
    const themeImporter = require('./lib/themeImporter');

    const self = this;

    runTask(subtask);

    function runTask(task) {
      const tasks = {
        login: () => {
          let defaults = {};
          try {
            const env = getEnv();
            defaults = Object.assign(defaults, env);
          } catch (e) {
            // env file does not exist yet.
          }
          grunt.config.merge({
            prompt: {
              credentials: {
                options: {
                  questions: [
                    {
                      config: 'cm.remoteThemeImporter.studioUrl',
                      type: 'input',
                      message: 'Studio URL: ',
                      default: defaults.studioUrl,
                      validate: function (value) {
                        if (!themeImporter.validateUrl(value)) {
                          return 'Please enter a valid URL.';
                        }
                        return true;
                      }
                    },
                    {
                      config: 'cm.remoteThemeImporter.previewUrl',
                      type: 'input',
                      message: 'Preview URL (optional): ',
                      default: defaults.previewUrl,
                      validate: function (value) {
                        if (value.length > 0 && !themeImporter.validateUrl(value)) {
                          return 'Please enter a valid URL.';
                        }
                        return true;
                      }
                    },
                    {
                      config: 'cm.remoteThemeImporter.username',
                      type: 'input',
                      message: 'Username: ',
                      validate: function (value) {
                        if (!themeImporter.validateUsername(value)) {
                          return 'Please enter a username.';
                        }
                        return true;
                      }
                    },
                    {
                      config: 'cm.remoteThemeImporter.password',
                      type: 'password',
                      message: 'Password: ',
                      validate: function (value) {
                        if (!themeImporter.validatePassword(value)) {
                          return 'Please enter a password.';
                        }
                        return true;
                      }
                    }
                  ],
                  then: function () {
                    grunt.task.run('remoteThemeImporter:generateApiKey');
                  }
                }
              }
            }
          });
          grunt.task.run('prompt:credentials');
        },
        generateApiKey: () => {
          const done = self.async();
          const { studioUrl, previewUrl, username, password } = grunt.config('cm.remoteThemeImporter');

          themeImporter.login(
            studioUrl,
            previewUrl,
            username,
            password
          ).then(msg => {
            grunt.log.oklns(msg);
            done();
          }).catch(e => {
            done(e);
          });
        },
        logout: () => {
          const done = self.async();

          themeImporter.logout().then(msg => {
            grunt.log.oklns(msg);
            done();
          }).catch(e => {
            done(e);
          });
        },
        whoami: () => {
          const done = self.async();

          themeImporter.whoami().then(user => {
            grunt.log.oklns(`You are logged in as user '${user.name}' (id=${user.id}).`);
            done();
          }).catch(e => {
            done(e);
          });
        },
        uploadTheme: () => {
          if (!grunt.task.exists('build')) {
            grunt.fail.fatal('The task remoteThemeImporter:uploadTheme relies on task "build", but it hasn´t been registered.');
          }

          const done = self.async();
          const tasklist = ['build', 'uploadTheme'];

          grunt.registerTask('uploadTheme', 'Subtask of uploadTheme task.', function() {
            try {
              const done = this.async();
              const THEME_NAME = grunt.config('themeConfig').name;
              const TARGET_PATH = grunt.config('themeConfig').targetPath;

              if (!themeImporter.validateThemeName(THEME_NAME)) {
                throw new Error('The name of the theme is missing in package.json.')
              }

              themeImporter.uploadTheme(
                THEME_NAME,
                TARGET_PATH
              ).then(zipFile => {
                grunt.log.oklns(`Theme has successfully been uploaded (file ${path.basename(zipFile)}).`);
                done();
              }).catch(e => {
                done(e);
              });
            } catch (e) {
              grunt.log.errorlns(e.message);
            }
          });

          themeImporter.whoami().then(() => {
            grunt.task.run(tasklist);
            done();
          }).catch(e => {
            grunt.log.errorlns(e.message);
            tasklist.unshift('remoteThemeImporter:login');
            grunt.task.run(tasklist);
            done();
          });
        },
        default: function () {
          grunt.log.errorlns(`Unknown target ${subtask} for task ${self.name}.`);
        }
      };
      (tasks[task] || tasks['default'])();
    }
  });
};
