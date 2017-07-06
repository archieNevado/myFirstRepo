'use strict';

/**
 * Initialize Grunt config and load available configs and tasks
 */
module.exports = (grunt) => {
  grunt.verbose.writeln('Initialize grunt config and load available configs and tasks.');

  const fs = require('fs');
  const loadGruntConfigs = require('load-grunt-configs');
  const loadGruntTasks = require('load-grunt-tasks');

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
    configOptions.themeConfig = JSON.parse(fs.readFileSync('package.json')).theme;

  } else {
    grunt.verbose.writeln('Initialize common grunt config.');

    taskOptions.pattern = [
      'grunt-available-tasks',
      'grunt-jsdoc-to-markdown',
      'grunt-mocha-test',
      'grunt-contrib-jasmine'
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
          grunt.cli.tasks.includes('development') ||
          grunt.cli.tasks.includes('production') ||
          grunt.cli.tasks.includes('test'))
  ) {
    grunt.verbose.writeln('Display grunt tasks with execution time is enabled');
    require('time-grunt')(grunt);
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
