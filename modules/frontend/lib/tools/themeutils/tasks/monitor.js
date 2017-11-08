'use strict';

const { getFlattenedDependencies, resolveFileDependencies } = require("../configs/common/dependencies");
const { isBrickDependency } = require("../configs/common/workspace");
const closestPackage = require("closest-package");
const path = require("path");

function getLocalPackagesFilePatterns() {
  const pkgPath = closestPackage.sync(process.cwd());
  return resolveFileDependencies(pkgPath)
          .map(dependency => path.join(path.relative(process.cwd(), path.dirname(dependency.getPkgPath())), "src/**"));
}

function getBrickFilePatterns(relativePath = "**") {
  const pkgPath = closestPackage.sync(process.cwd());
  return getFlattenedDependencies(pkgPath, isBrickDependency)
          .map(dependency => path.join(path.relative(process.cwd(), path.dirname(dependency.getPkgPath())), relativePath));
}

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
            tasks: ['webpack']
          },
          css: {
            files: 'src/css/**/*.css',
            tasks: ['copy:main']
          },
          js: {
            files: 'src/js/*.js',
            tasks: ['copy:main', 'webpack']
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
            files: ['src/fonts/**', 'src/img/**', 'src/images/**', 'src/js/**', 'src/vendor/**'],
            tasks: ['copy:main']
          },
          productionCss: {
            options: {
              spawn: true
            },
            files: '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>/css/*.css',
            tasks: []
          },
          bricks_js: {
            files: getBrickFilePatterns('/src/js/**'),
            tasks: ['webpack']
          },
          bricks_templates: {
            files: getBrickFilePatterns('/src/templates/**'),
            tasks: ['webpack', 'compress:brick_templates']
          },
          bricks_sass: {
            options: {
              spawn: true
            },
            files: getBrickFilePatterns('/src/sass/**/*.scss'),
            tasks: ['webpack']
          },
          bricks_img: {
            files: getBrickFilePatterns('/src/img/**'),
            tasks: ['webpack']
          },
          bricks_l10n: {
            files: getBrickFilePatterns('/src/l10n/**'),
            tasks: ['webpack']
          },
          bricks_fonts: {
            files: getBrickFilePatterns('/src/fonts/**'),
            tasks: ['webpack']
          },
          localPackagesChanges: {
            files: getLocalPackagesFilePatterns(),
            tasks: ['sync:localPackages']
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

  grunt.registerTask('openBrowser', 'Open Preview (or Studio as a fallback) in browser.', function() {
    const { openBrowser } = require('./lib/monitor');

    openBrowser();
  });

  // monitor task for uploading changes in target directory to remote CAE
  grunt.registerTask('monitor', 'Watch file changes and update theme on remote CAE.', function() {
    grunt.log.writeln('Watch file changes for remote CAE...');
    grunt.verbose.writeln('Execute remote monitor task.');

    const livereload = require('./lib/livereload');
    const { addWatchEventListener } = require('./lib/monitor');

    livereload.init(livereloadConfig);
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
          tasks: ['webpack']
        },
        ftl: {
          files: 'src/templates/**/*.ftl',
          tasks: ['copy:templates', 'compress:templates']
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
          tasks: ['copy:main', 'webpack']
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
        bricks_js: {
          files: getBrickFilePatterns('/src/js/**'),
          tasks: ['webpack']
        },
        bricks_templates: {
          files: getBrickFilePatterns('/src/templates/**'),
          tasks: ['webpack', 'compress:brick_templates']
        },
        bricks_sass: {
          options: {
            spawn: true
          },
          files: getBrickFilePatterns('/src/sass/**/*.scss'),
          tasks: ['webpack']
        },
        bricks_img: {
          files: getBrickFilePatterns('/src/img/**'),
          tasks: ['webpack']
        },
        bricks_l10n: {
          files: getBrickFilePatterns('/src/l10n/**'),
          tasks: ['webpack']
        },
        bricks_fonts: {
          files: getBrickFilePatterns('/src/fonts/**'),
          tasks: ['webpack']
        },
        localPackagesChanges: {
          files: getLocalPackagesFilePatterns(),
          tasks: ['sync:localPackages']
        },
        targetDescriptor: {
          options: {
            event: ['added', 'changed']
          },
          files: '<%= themeConfig.targetPath %>/resources/THEME-METADATA/<%= themeConfig.name %>-theme.xml',
          tasks: []
        },
        targetChanged: {
          options: {
            event: ['added', 'changed']
          },
          files: '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>/**/*.*',
          tasks: []
        },
        targetDeleted: {
          options: {
            event: ['deleted']
          },
          files: '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>/**/*.*',
          tasks: []
        }
      }
    };
    grunt.config.merge(config);

    const tasklist = ['remoteThemeImporter:uploadTheme', 'openBrowser', 'watch'];

    const themeImporter = require('./lib/themeImporter');

    const done = this.async();

    themeImporter.whoami().then(user => {
      grunt.log.writeln(`You are logged in as user '${user.name}' (id=${user.id}).`);
      grunt.task.run(tasklist);
      done();
    }).catch(e => {
      tasklist.unshift('remoteThemeImporter:login');
      grunt.log.error(e.message);
      grunt.task.run(tasklist);
      done();
    });
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
    const config = getMonitorConfig();
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
