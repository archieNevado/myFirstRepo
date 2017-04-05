/**
 * Register grunt task for generating grunt configs for a list of bricks to be copied into a theme.
 */
'use strict';

module.exports = (grunt, {themeConfig}) => {
  grunt.verbose.writeln('Generate grunt configs for a list of bricks to be copied into a theme.');

  const _ = require('lodash');

  const bricks = grunt.config('bricks.src');
  if (!(Array.isArray(bricks)) || bricks.length < 1) {
    grunt.log.errorlns('No Bricks found');
    return;
  }
  const WORKING_DIRECTORY = process.cwd();
  const THEME_CONFIG = themeConfig || {};

  const loadBrickConfig = (brick, brickCompressFiles) => {
    grunt.verbose.writeln(`Load grunt config for brick ${brick}.`);

    const fs = require('fs');
    const path = require('path');

    const BRICK_DIRECTORY = path.join(WORKING_DIRECTORY, '../../lib/bricks/', brick);
    const BRICK_CONFIG_FILE = path.join(WORKING_DIRECTORY, '../../lib/bricks/', brick, 'grunt-config.js');
    const BRICK_TEMPLATES_DEST = path.join(WORKING_DIRECTORY, '../../target/resources/WEB-INF/templates/bricks');

    grunt.verbose.writeln('Load a brick to a theme.');

    // check for config
    if (typeof THEME_CONFIG.name !== 'string' || THEME_CONFIG.name.length === 0) {
      grunt.log.errorlns(`'themeConfig.name' is undefined. Please add it as mandatory theme configuration to your Gruntfile.js or package.json`);
      return;
    }

    //check for brick
    if (typeof brick !== 'string' || brick.length === 0) {
      grunt.log.errorlns(`Brick is undefined. Can't load it.`);
      return;
    }

    // load config files
    if (!fs.existsSync(BRICK_DIRECTORY) || !fs.existsSync(BRICK_CONFIG_FILE)) {
      grunt.log.errorlns(`No Brick found in ${BRICK_DIRECTORY}`);
      return;
    }

    //load brick
    grunt.verbose.writeln(`Looking for brick configs in ${BRICK_DIRECTORY}`);
    grunt.verbose.writeln(`Destination for brick templates ${BRICK_TEMPLATES_DEST}`);
    const options = {
      config: {
        src: BRICK_CONFIG_FILE
      },
      themeConfig: THEME_CONFIG,
      brickDirectory: BRICK_DIRECTORY,
      brickTemplatesDest: BRICK_TEMPLATES_DEST,
      brickCompressFiles: brickCompressFiles
    };
    const config = require('load-grunt-configs')(grunt, options);

    grunt.verbose.writeln(`Finished loading grunt config for brick ${brick}`);

    return config;
  };

  let config = Object.create(null);
  bricks.forEach(brick => {
    const brickCompressFiles = config.compress ? config.compress.brick_templates.files : [];
    const brickConfig = loadBrickConfig(brick, brickCompressFiles);
    config = _.merge(config, brickConfig);
  });

  grunt.verbose.oklns('Finished generating grunt configs for a list of bricks to be copied into a theme.');

  return {
    tasks: config
  };
};
