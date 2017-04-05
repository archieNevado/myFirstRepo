/**
 * Generates grunt config for jsdoc2md grunt task for all ES2015 modules in the lib/js directory
 */
'use strict';

module.exports = grunt => {
  grunt.verbose.oklns('Generate jsdoc2md grunt configs for ES2015 modules in lib/js directory.');

  const fs = require('fs');
  const path = require('path');

  const JS_LIBS_DIRECTORY = path.join(process.cwd(), 'lib/js');

  const config = {
    tasks: {
      jsdoc2md: {}
    }
  };

  const generateJSLibJsdocConfig = subdir => {
    const JS_LIB_DIRECTORY = path.join(JS_LIBS_DIRECTORY, subdir);

    const camelCase = s => s.toLowerCase().replace(
      /-(.)/g,
      (match, group1) => group1.toUpperCase()
    );

    grunt.verbose.writeln(`Generate grunt config for jsdoc2md of JS library ${subdir}.`);

    // check for subdir
    if (typeof subdir !== 'string' || subdir.length === 0) {
      grunt.log.errorlns(`Subdir is undefined. Can't load it.`);
      return;
    }

    // check directory
    if (!fs.existsSync(JS_LIB_DIRECTORY)) {
      grunt.log.errorlns(`No JS library found in ${JS_LIB_DIRECTORY}`);
      return;
    }

    // create config
    config.tasks.jsdoc2md[`jslib_${camelCase(subdir)}`] = {
      src: `${JS_LIB_DIRECTORY}/**/*.js`,
      dest: `${JS_LIB_DIRECTORY}/API.md`
    };

    grunt.verbose.writeln(`Finished generating jsdoc2md grunt config for JS library ${subdir}.`);
  };

  // browse through all bricks - delegate to generateBrickJsdocConfig()
  fs.readdirSync(JS_LIBS_DIRECTORY).forEach(file => {
    if (file !== '.' && file !== '..' && file !== 'legacy') {
      // check, if brick includes JavaScript
      if (fs.statSync(path.join(JS_LIBS_DIRECTORY, file)).isDirectory()) {
        // generate grunt config
        generateJSLibJsdocConfig(file);
      }
    }
  });

  grunt.verbose.oklns('Finished generating jsdoc2md grunt configs for ES2015 modules in lib/js directory.');

  return config;
};
