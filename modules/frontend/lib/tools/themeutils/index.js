'use strict';

const fs = require('fs');
const loadGruntConfigs = require('load-grunt-configs');
const loadGruntTasks = require('load-grunt-tasks');
const timeGrunt = require('time-grunt');

/**
 * Initialize Grunt config and load available configs and tasks
 */
module.exports = (grunt) => {
  grunt.verbose.writeln('Initialize grunt config and load available configs and tasks.');

  // check node version
  const currentNodeVersion = process.versions.node;
  const semver = currentNodeVersion.split('.');
  const major = semver[0];
  if (major < 6) {
    grunt.fail.fatal(
            'You are running Node ' + currentNodeVersion + '.\n' +
            'CoreMedia Frontend Workspace requires Node 6 or higher. \n' +
            'Please update your version of Node.'
    );
  }

  const DEFAULT_TARGET_PATH = '../../target/';

  const isThemeContext = /themes/.test(process.cwd());
  const taskOptions = {};
  const configOptions = {};


  if (isThemeContext) {
    grunt.verbose.writeln('Initialize theme grunt config.');

    taskOptions.config = `${__dirname}/package.json`;
    taskOptions.requireResolution = true;

    configOptions.config = {
      src: `${__dirname}/configs/themes/*.js`
    };

    const themeConfigGrunt = grunt.config("themeConfig");

    if (typeof themeConfigGrunt !== 'undefined' && typeof themeConfigGrunt.name === 'string') {
      configOptions.themeConfig = themeConfigGrunt;
    } else {
      configOptions.themeConfig = JSON.parse(fs.readFileSync('package.json')).theme;
    }

    // check target folder
    if (typeof configOptions.themeConfig.targetPath !== 'undefined' && typeof configOptions.themeConfig.targetPath !== 'string') {
      grunt.fail.warn('Wrong target path.');
    } else if (typeof configOptions.themeConfig.targetPath === 'undefined' || configOptions.themeConfig.targetPath === '') {
      configOptions.themeConfig.targetPath = DEFAULT_TARGET_PATH;
    }

  } else {
    grunt.verbose.writeln('Initialize common grunt config.');

    taskOptions.pattern = [
      'grunt-available-tasks',
      'grunt-jsdoc-to-markdown'
    ];
    taskOptions.config = `${__dirname}/package.json`;
    taskOptions.requireResolution = true;

    configOptions.config = {
      src: `${__dirname}/configs/common/*.js`
    };
  }

  grunt.verbose.writeln('Load available grunt tasks.');

  loadGruntTasks(grunt, taskOptions);

  // default: undefined => true, display execution time
  if (grunt.config('displayExecutionTime') !== false && (
          grunt.cli.tasks.includes('build') ||
          grunt.cli.tasks.includes('test'))
  ) {
    grunt.verbose.writeln('Display grunt tasks with execution time is enabled');
    timeGrunt(grunt);
  }

  grunt.verbose.writeln('Load available predefined grunt configs.');

  const configs = loadGruntConfigs(grunt, configOptions);
  grunt.config.merge(configs);

  if (isThemeContext) {
    grunt.verbose.writeln('Load available theme grunt tasks.');

    grunt.loadTasks(`${__dirname}/tasks`);
  }

  grunt.verbose.writeln('Finished initializing grunt config and load available configs and tasks.');
};
