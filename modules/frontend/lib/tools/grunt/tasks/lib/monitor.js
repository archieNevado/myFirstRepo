'use strict';

/**
 * Return configuration for monitor task.
 * @param grunt
 * @returns {{ target: string, livereload: { host: string, port: number, key: string=, cert: string= } }}
 * @private
 */
const getMonitorConfig = grunt => {
  const config = {
    livereload: {}
  };
  const defaultMonitorConfig = {
    target: 'remote',
    livereload: {
      host: 'localhost',
      port: 35729
    }
  };
  const customMonitorConfig = grunt.config('monitor');

  if (typeof customMonitorConfig !== 'object') {
    Object.assign(config, defaultMonitorConfig);
  } else {
    // Check property target
    if (typeof customMonitorConfig.target !== 'string') {
      Object.assign(config, {
        target: defaultMonitorConfig.target
      });
    } else if (customMonitorConfig.target !== 'local' && customMonitorConfig.target !== 'remote') {
      throw new Error('Property target of grunt config for monitor task must be either "remote" or "local". Please check your Gruntfile.js in current directory.');
    } else {
      Object.assign(config, {
        target: customMonitorConfig.target
      });
    }

    // Check property livereload
    if (typeof customMonitorConfig.livereload !== 'object') {
      Object.assign(config, {
        livereload: defaultMonitorConfig.livereload
      });
    } else {
      // Check property livereload.host
      if (typeof customMonitorConfig.livereload.host !== 'string') {
        Object.assign(config.livereload, {
          host: defaultMonitorConfig.livereload.host
        });
      } else {
        Object.assign(config.livereload, {
          host: customMonitorConfig.livereload.host
        });
      }

      // Check property livereload.port
      if (typeof customMonitorConfig.livereload.port !== 'number') {
        Object.assign(config.livereload, {
          port: defaultMonitorConfig.livereload.port
        });
      } else if (customMonitorConfig.livereload.port < 1024 || customMonitorConfig.livereload.port > 49151) {
        throw new Error('Property livereload.port of grunt config for monitor task must be a number between 1024 and 49151. Please check your Gruntfile.js in current directory.')
      } else {
        Object.assign(config.livereload, {
          port: customMonitorConfig.livereload.port
        });
      }

      // Check if to livereload over ssl (https)
      if (!customMonitorConfig.livereload.hasOwnProperty('key') && customMonitorConfig.livereload.hasOwnProperty('cert')) {
        throw new Error('Property livereload.key of grunt config for monitor task is missing. If you want to livereload over ssl (https), you must specify properties livereload.key as well as livereload.cert. Please check your Gruntfile.js in current directory.')
      } else if (customMonitorConfig.livereload.hasOwnProperty('key') && !customMonitorConfig.livereload.hasOwnProperty('cert')) {
        throw new Error('Property livereload.cert of grunt config for monitor task is missing. If you want to livereload over ssl (https), you must specify properties livereload.key as well as livereload.cert. Please check your Gruntfile.js in current directory.')
      } else if (customMonitorConfig.livereload.hasOwnProperty('key') && customMonitorConfig.livereload.hasOwnProperty('cert')) {
        // Check properties livereload.key and livereload.cert
        if (typeof customMonitorConfig.livereload.key !== 'string') {
          throw new Error('Property livereload.key of grunt config for monitor task must contain your .key file. Please check your Gruntfile.js in current directory.')
        } else if (typeof customMonitorConfig.livereload.cert !== 'string') {
          throw new Error('Property livereload.cert of grunt config for monitor task must contain your .crt file. Please check your Gruntfile.js in current directory.')
        } else {
          Object.assign(config.livereload, {
            key: customMonitorConfig.livereload.key,
            cert: customMonitorConfig.livereload.cert
          });
        }
      }
    }
  }
  return config;
};

/**
 * Add event listener for watch event.
 * @param grunt
 * @private
 */
const addWatchEventListener = (grunt, livereload) => {
  const { debounce } = require('lodash');
  const path = require('path');
  const { remoteThemeImporter } = require('@coremedia/common');

  const COPY_MAIN_CWD = path.normalize(grunt.config('copy.main.cwd'));
  const COPY_TEMPLATES_CWD = path.normalize(grunt.config('copy.templates.cwd'));
  const CLEAN_FILELIST_CWD = path.normalize(grunt.config('clean.options.cwd'));

  const logger = {
    log: grunt.verbose.writeln,
    info: grunt.log.writeln,
    error: grunt.log.error
  };

  let busy = false;
  let watchedFiles = Object.create(null);

  const done = () => {
    busy = false;
    livereload.trigger(logger);
    grunt.log.writeln('');
    grunt.log.writeln('Done. Waiting...');
    grunt.log.writeln('');
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

  const handleTargetDescriptor = callback => {
    const { targetDescriptor } = watchedFiles;
    delete watchedFiles.targetDescriptor;
    const uploadDescriptorFiles = Array.from(targetDescriptor);
    remoteThemeImporter.uploadDescriptor(
      grunt.config('themeConfig').name,
      uploadDescriptorFiles,
      logger
    ).then(msg => {
      grunt.log.oklns(msg);
      callback();
    }).catch(e => {
      grunt.log.errorlns(e);
      callback();
    });
  };

  const handleTargetChanged = callback => {
    const { targetChanged } = watchedFiles;
    delete watchedFiles.targetChanged;
    const uploadFiles = Array.from(targetChanged);
    remoteThemeImporter.uploadFile(
      grunt.config('themeConfig').name,
      uploadFiles,
      logger
    ).then(msg => {
      grunt.log.oklns(msg);
      callback();
    }).catch(e => {
      grunt.log.errorlns(e);
      callback();
    });
  };

  const handleTargetDeleted = callback => {
    const { targetDeleted } = watchedFiles;
    delete watchedFiles.targetDeleted;
    const deleteFiles = Array.from(targetDeleted);
    const deletions = deleteFiles.map(filepath =>
      remoteThemeImporter.deleteFile(
        grunt.config('themeConfig').name,
        filepath,
        logger
      )
    );
    /*global Promise*/
    Promise.all(deletions).then(msgs => {
      msgs.forEach(msg => {
        if (msg.type === 'SUCCESS') {
          grunt.log.oklns(msg.text);
        } else {
          grunt.log.errorlns(msg.text);
        }
      });
      callback();
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

module.exports = {
  getMonitorConfig,
  addWatchEventListener
};
