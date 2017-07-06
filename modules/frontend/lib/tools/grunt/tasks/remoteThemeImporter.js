'use strict';

/**
 * Register grunt task for managing remote file operations of themes
 */
module.exports = grunt => {
  grunt.verbose.writeln('Register grunt task for managing remote file operations of themes.');

  grunt.registerTask('remoteThemeImporter', 'Grunt Task for remote file operations of themes. (login|logout|whoami|uploadTheme)', function(subtask) {
    if (!subtask) {
      grunt.log.errorlns(`No target for task ${this.name} provided.`);
      return;
    }

    const { remoteThemeImporter } = require('@coremedia/common');

    const defaultStudioUrl = grunt.config('themeConfig.studioUrl');

    const logger = {
      log: grunt.verbose.writeln,
      info: grunt.log.writeln
    };
    const self = this;
    runTask(subtask);

    function runTask(task) {
      const tasks = {
        login: () => {
          grunt.config.merge({
            prompt: {
              credentials: {
                options: {
                  questions: [
                    {
                      config: 'cm.remoteThemeImporter.studioUrl',
                      type: 'input',
                      message: 'Studio URL: ',
                      default: defaultStudioUrl,
                      validate: function (value) {
                        if (value.length === 0) {
                          return 'Please enter the URL of the Studio.';
                        }
                        if (!remoteThemeImporter.validateUrl(value)) {
                          return 'The entered URL is invalid. Please recheck your input.';
                        }
                        return true;
                      }
                    },
                    {
                      config: 'cm.remoteThemeImporter.username',
                      type: 'input',
                      message: 'Username: ',
                      validate: function (value) {
                        return value.length > 0 || 'Please enter a username.';
                      }
                    },
                    {
                      config: 'cm.remoteThemeImporter.password',
                      type: 'password',
                      message: 'Password: ',
                      validate: function (value) {
                        return value.length > 0 || 'Please enter a password.';
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
          const { studioUrl, username, password } = grunt.config('cm.remoteThemeImporter');
          remoteThemeImporter.login(
            studioUrl,
            username,
            password,
            logger
          ).then(msg => {
            grunt.log.oklns(msg);
            done();
          }).catch(e => {
            done(e);
          });
        },
        logout: () => {
          const done = self.async();
          remoteThemeImporter.logout(
            logger
          ).then(msg => {
            grunt.log.oklns(msg);
            done();
          }).catch(e => {
            done(e);
          });
        },
        whoami: () => {
          const done = self.async();
          remoteThemeImporter.whoami(
            logger
          ).then(msg => {
            grunt.log.oklns(msg);
            done();
          }).catch(e => {
            done(e);
          });
        },
        uploadTheme: () => {
          // Check if API key is existing, otherwise run login task first
          try {
            const { apiKey } = remoteThemeImporter.getCMConfig();
            if (typeof apiKey !== 'string' || apiKey.length === 0) {
              throw new Error();
            }

            grunt.registerTask('uploadTheme', 'Subtask of uploadTheme task.', function() {
              const done = this.async();
              remoteThemeImporter.uploadTheme(
                grunt.config('themeConfig').name,
                logger
              ).then(msg => {
                grunt.log.oklns(msg);
                done();
              }).catch(e => {
                done(e);
              });
            });

            grunt.task.run(['production', 'uploadTheme']);
          } catch (e) {
            grunt.log.errorlns('API key file doesn´t exist. Please enter your credentials to generate your API key.');
            grunt.task.run(['remoteThemeImporter:login', 'remoteThemeImporter:uploadTheme']);
          }
        },
        // deprecated
        uploadDescriptor: () => {
          if (grunt.config('cm.uploadDescriptor.filepaths')) {
            const done = self.async();
            remoteThemeImporter.uploadDescriptor(
              grunt.config('themeConfig').name,
              grunt.config('cm.uploadDescriptor.filepaths'),
              logger
            ).then(msg => {
              grunt.log.oklns(msg);
              done();
            }).catch(e => {
              done(e);
            });
          } else {
            grunt.fail.warn('No files to upload have been specified.')
          }
        },
        // deprecated
        uploadFile: () => {
          if (grunt.config('cm.uploadFile.filepaths')) {
            const done = self.async();
            remoteThemeImporter.uploadFile(
              grunt.config('themeConfig').name,
              grunt.config('cm.uploadFile.filepaths'),
              logger
            ).then(msg => {
              grunt.log.oklns(msg);
              done();
            }).catch(e => {
              done(e);
            });
          } else {
            grunt.fail.warn('No files to upload have been specified.')
          }
        },
        // deprecated
        deleteFile: () => {
          const filepaths = grunt.config('cm.deleteFile.filepaths');
          if (filepaths) {
            const done = self.async();
            const deletions = filepaths.map(filepath => {
              return remoteThemeImporter.deleteFile(
                grunt.config('themeConfig').name,
                filepath,
                logger
              );
            });
            /*global Promise*/
            Promise.all(deletions).then(msgs => {
              msgs.forEach(msg => {
                if (msg.type === 'SUCCESS') {
                  grunt.log.oklns(msg.text);
                } else {
                  grunt.log.errorlns(msg.text);
                }
              });
              done();
            });
          } else {
            grunt.fail.warn('No files to delete have been specified.')
          }
        },
        default: function () {
          grunt.log.errorlns(`Unknown target ${subtask} for task ${self.name}.`);
        }
      };
      (tasks[task] || tasks['default'])();
    }
  });
};
