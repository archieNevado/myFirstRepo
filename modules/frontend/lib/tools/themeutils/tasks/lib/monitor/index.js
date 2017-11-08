'use strict';

const chalk = require('chalk');

/**
 * Return config for monitor task.
 * @returns {{ target: string, livereload: { host: string, port: number, key: string=, cert: string= } }}
 * @private
 */
const getMonitorConfig = () => {
  const { getEnv } = require('../environment');

  const monitorConfig = {
    livereload: {}
  };
  const defaultMonitorConfig = {
    target: 'remote',
    livereload: {
      host: 'localhost',
      port: 35729
    }
  };

  let customMonitorConfig;
  try {
    customMonitorConfig = getEnv().monitor;
  } catch (e) {
    // no custom monitor config, use default config
  }

  if (typeof customMonitorConfig !== 'object') {
    Object.assign(monitorConfig, defaultMonitorConfig);
  } else {
    // Check property target
    if (typeof customMonitorConfig.target !== 'string') {
      Object.assign(monitorConfig, {
        target: defaultMonitorConfig.target
      });
    } else if (customMonitorConfig.target !== 'local' && customMonitorConfig.target !== 'remote') {
      throw new Error('Property monitor.target must be either "remote" or "local". Please check env.json in config directory in root of frontend workspace.');
    } else {
      Object.assign(monitorConfig, {
        target: customMonitorConfig.target
      });
    }

    // Check property livereload
    if (typeof customMonitorConfig.livereload !== 'object') {
      Object.assign(monitorConfig, {
        livereload: defaultMonitorConfig.livereload
      });
    } else {
      // Check property livereload.host
      if (typeof customMonitorConfig.livereload.host !== 'string') {
        Object.assign(monitorConfig.livereload, {
          host: defaultMonitorConfig.livereload.host
        });
      } else {
        Object.assign(monitorConfig.livereload, {
          host: customMonitorConfig.livereload.host
        });
      }

      // Check property livereload.port
      if (typeof customMonitorConfig.livereload.port !== 'number') {
        Object.assign(monitorConfig.livereload, {
          port: defaultMonitorConfig.livereload.port
        });
      } else if (customMonitorConfig.livereload.port < 1024 || customMonitorConfig.livereload.port > 49151) {
        throw new Error('Property monitor.livereload.port must be a number between 1024 and 49151. Please check env.json in config directory in root of frontend workspace.')
      } else {
        Object.assign(monitorConfig.livereload, {
          port: customMonitorConfig.livereload.port
        });
      }
    }
  }
  return monitorConfig;
};

/**
 * Add event listener for watch event.
 * @param grunt
 * @private
 */
const addWatchEventListener = (grunt, livereload) => {
  const { debounce } = require('lodash');
  const path = require('path');
  const themeImporter = require('../themeImporter');

  const COPY_MAIN_CWD = path.normalize(grunt.config('copy.main.cwd'));
  const COPY_TEMPLATES_CWD = path.normalize(grunt.config('copy.templates.cwd'));
  const CLEAN_FILELIST_CWD = path.normalize(grunt.config('clean.options.cwd'));

  let busy = false;
  let watchedFiles = Object.create(null);

  const done = () => {
    busy = false;
    const date = new Date;
    const hours = (date.getHours() < 10 ? '0' : '') + date.getHours();
    const minutes = (date.getMinutes() < 10 ? '0' : '') + date.getMinutes();
    const seconds = (date.getSeconds() < 10 ? '0' : '') + date.getSeconds();

    livereload.trigger();
    console.log();
    console.log(chalk.yellow.bold(`Finished @ ${hours}:${minutes}:${seconds}. Waiting...`));
    console.log();

    onChange();
  };

  const handleThemeChanged = () => {
    const files = Array.from(watchedFiles.themeChanged).map(f => f.replace(COPY_MAIN_CWD, ''));
    delete watchedFiles.themeChanged;
    grunt.config('copy.main.src', files);
  };

  const handleThemeDeleted = () => {
    const files = Array.from(watchedFiles.themeDeleted).map(f => f.replace('src', CLEAN_FILELIST_CWD));
    delete watchedFiles.themeDeleted;
    grunt.config('clean.filelist.src', files);
  };

  const handleTemplates = () => {
    const files = Array.from(watchedFiles.ftl).map(f => f.replace(COPY_TEMPLATES_CWD, ''));
    delete watchedFiles.ftl;
    grunt.config('copy.templates.src', files);
  };

  const handleTargetDescriptor = done => {
    try {
      const { targetDescriptor } = watchedFiles;
      delete watchedFiles.targetDescriptor;
      const uploadDescriptorFiles = Array.from(targetDescriptor);

      const THEME_NAME = grunt.config('themeConfig').name;
      const TARGET_PATH = grunt.config('themeConfig').targetPath;
      if (!themeImporter.validateThemeName(THEME_NAME)) {
        throw new Error('The name of the theme is missing in package.json.')
      }

      themeImporter.uploadDescriptor(
        THEME_NAME,
        TARGET_PATH,
        uploadDescriptorFiles
      ).then(count => {
        console.log(chalk.bold(`${count} ${(count === 1 ? 'file has' : 'files have')} successfully been uploaded.`));
        done();
      }).catch(e => {
        console.error(chalk.red(e));
        done();
      });
    } catch (e) {
      console.error(chalk.red(e.message));
    }
  };

  const handleTargetChanged = done => {
    try {
      const { targetChanged } = watchedFiles;
      delete watchedFiles.targetChanged;
      const uploadFiles = Array.from(targetChanged);

      const THEME_NAME = grunt.config('themeConfig').name;
      const TARGET_PATH = grunt.config('themeConfig').targetPath;
      if (!themeImporter.validateThemeName(THEME_NAME)) {
        throw new Error('The name of the theme is missing in package.json.')
      }

      themeImporter.uploadFile(
        THEME_NAME,
        TARGET_PATH,
        uploadFiles
      ).then(count => {
        console.log(chalk.bold(`${count} ${(count === 1 ? 'file has' : 'files have')} successfully been uploaded.`));
        done();
      }).catch(e => {
        console.error(chalk.red(e));
        done();
      });
    } catch (e) {
      console.error(chalk.red(e.message));
    }
  };

  const handleTargetDeleted = done => {
    const { targetDeleted } = watchedFiles;
    delete watchedFiles.targetDeleted;
    const deleteFiles = Array.from(targetDeleted);
    const deletions = deleteFiles.map(filepath =>
      themeImporter.deleteFile(
        grunt.config('themeConfig').name,
        grunt.config('themeConfig').targetPath,
        filepath
      )
    );
    /*global Promise*/
    Promise.all(deletions).then(results => {
      results.forEach(result => {
        if (result.type === 'SUCCESS') {
          console.log(chalk.bold(`File ${path.basename(result.file)} has successfully been deleted.`));
        } else {
          console.error(chalk.red(`File ${path.basename(result.file)} has not been deleted due to the following error: ${result.error}`));
        }
      });
      done();
    });
  };

  const onChange = debounce(() => {
    if (watchedFiles.themeChanged) {
      handleThemeChanged();
    }
    if (watchedFiles.themeDeleted) {
      handleThemeDeleted();
    }
    if (watchedFiles.ftl) {
      handleTemplates();
    }

    if (busy) {
      return;
    }

    if (watchedFiles.targetDescriptor) {
      busy = true;
      handleTargetDescriptor(done);
      return;
    }
    if (watchedFiles.targetChanged) {
      busy = true;
      handleTargetChanged(done);
      return;
    }
    if (watchedFiles.targetDeleted) {
      busy = true;
      handleTargetDeleted(done);
      return;
    }

  }, 2000);

  // Add watch event listener to set the file to be uploaded
  grunt.event.on('watch', (action, filepath, target) => {
    if (!watchedFiles[target]) {
      watchedFiles[target] = new Set();
    }
    watchedFiles[target].add(path.normalize(filepath));
    onChange();
  });
};

const clearConsole = () => {
  process.stdout.write(
    process.platform === 'win32' ? '\x1Bc' : '\x1B[2J\x1B[3J\x1B[H'
  );
};

const openBrowser = () => {
  const opn = require('opn');

  const livereload = require('../livereload');
  const { getEnv } = require('../environment');

  const livereloadURL = livereload.getHost();

  clearConsole();

  console.log(chalk.green.bold('Theme has been syncronized with remote CAE!'));
  console.log();
  console.log(chalk.bold('Subsequent changes will automatically be transfered to the remote CAE'));
  console.log(chalk.bold('and be available in preview using the developer mode.'));
  console.log('To instantly reload your changes in the browser, you may need to accept');
  console.log(`the certificate for the local LiveReload server (https://${livereloadURL}) first.`);
  console.log();

  try {
    const { studioUrl, previewUrl } = getEnv();
    const url = previewUrl || studioUrl;

    console.log(`Studio URL: ${studioUrl}`);
    if (previewUrl) {
      console.log(`Preview URL: ${previewUrl}`);
    }

    opn(url).catch(() => {
      console.error(chalk.red('Error: Unable to open browser.'));
    });
  } catch (e) {
    // env file does not exist yet.
  }
};

module.exports = {
  getMonitorConfig,
  addWatchEventListener,
  openBrowser
};
