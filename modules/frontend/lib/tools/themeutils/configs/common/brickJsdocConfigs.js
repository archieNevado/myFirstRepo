/**
 * Generates grunt config for jsdoc2md grunt task for all bricks which include JavaScript
 */
'use strict';

module.exports = grunt => {
  grunt.verbose.writeln('Generate grunt configs for jsdoc2md grunt task for all bricks which include JavaScript.');

  const fs = require('fs');
  const path = require('path');

  const BRICKS_DIRECTORY = path.join(process.cwd(), 'lib/bricks');

  const config = {
    tasks: {
      jsdoc2md: {}
    }
  };

  const generateBrickJsdocConfig = brick => {
    grunt.verbose.writeln(`Generate grunt config for jsdoc2md of brick ${brick}.`);

    const BRICK_DIRECTORY = path.join(BRICKS_DIRECTORY, brick);
    const BRICK_JS_DIRECTORY = path.join(BRICK_DIRECTORY, 'src', 'js');

    const camelCase = s => s.toLowerCase().replace(
      /-(.)/g,
      (match, group1) => group1.toUpperCase()
    );

    // check for brick
    if (typeof brick !== 'string' || brick.length === 0) {
      grunt.fail.fatal(`Brick is undefined. Can't load it.`);
    }

    // check brick directory
    if (!fs.existsSync(BRICK_DIRECTORY)) {
      grunt.fail.fatal(`No Brick found in ${BRICK_DIRECTORY}`);
    }

    // check brick JS directory
    if (!fs.existsSync(BRICK_JS_DIRECTORY)) {
      grunt.fail.fatal(`No Brick JS found in ${BRICK_JS_DIRECTORY}`);
    }

    // create config
    config.tasks.jsdoc2md[`brick_${camelCase(brick)}`] = {
      src: `${BRICK_JS_DIRECTORY}/*.js`,
      dest: `${BRICK_DIRECTORY}/API.md`
    };

    grunt.verbose.writeln(`Finished generating jsdoc2md grunt config for brick ${brick}.`);
  };

  // browse through all bricks - delegate to generateBrickJsdocConfig()
  fs.readdirSync(BRICKS_DIRECTORY).forEach(file => {
    if (file !== '.' && file !== '..') {
      // check, if brick includes JavaScript
      if (fs.statSync(path.join(BRICKS_DIRECTORY, file)).isDirectory() && fs.existsSync(path.join(BRICKS_DIRECTORY, file, "src", "js"))) {
        // generate grunt config
        generateBrickJsdocConfig(file);
      }
    }
  });

  grunt.verbose.oklns('Finished generating grunt config for jsdoc2md grunt task for all bricks which include JavaScript.');

  return config;
};
