/**
 * Generates grunt config for mocha grunt task for all ES2015 modules in the lib/js directory.
 */
'use strict';

module.exports = grunt => {
  grunt.verbose.writeln('Generate mocha test grunt configs for ES2015 modules in lib/js directory.');

  const fs = require('fs');
  const path = require('path');

  const JS_LIBS_DIRECTORY = path.join(process.cwd(), 'lib/js');

  const config = {
    tasks: {
      mochaTest: {
        test: {
          options: {
            reporter: 'spec',
            require: 'babel-register'
          },
          src: []
        }
      }
    }
  };

  const generateJSLibMochaConfig = subdir => {
    const JS_LIB_DIRECTORY = path.join(JS_LIBS_DIRECTORY, subdir);

    grunt.verbose.writeln(`Generate grunt config for mocha tests of JS library ${subdir}.`);

    // check for subdir
    if (typeof subdir !== 'string' || subdir.length === 0) {
      grunt.log.errorlns('Subdir is undefined. Can´t load it.');
      return;
    }

    // check directory
    if (!fs.existsSync(JS_LIB_DIRECTORY)) {
      grunt.log.errorlns(`No JS library found in ${JS_LIB_DIRECTORY}`);
      return;
    }

    // check directory
    if (!fs.existsSync(path.join(JS_LIB_DIRECTORY, 'test'))) {
      grunt.log.errorlns(`No tests found in ${JS_LIB_DIRECTORY}`);
      return;
    }

    // create config
    config.tasks.mochaTest.test.src.push(path.join(JS_LIB_DIRECTORY, 'test/*_spec.js'));

    grunt.verbose.writeln(`Finished generating mocha test grunt config for JS library ${subdir}.`);
  };

  // browse through all bricks - delegate to generateJSLibWebpackConfig()
  fs.readdirSync(JS_LIBS_DIRECTORY).forEach(file => {
    // No Tests inside utils and legacy directory
    if (file !== '.' && file !== '..' && file !== 'utils' && file !== 'legacy') {
      // check, if brick includes JavaScript
      if (fs.statSync(path.join(JS_LIBS_DIRECTORY, file)).isDirectory()) {
        // generate grunt config
        generateJSLibMochaConfig(file);
      }
    }
  });

  grunt.verbose.oklns('Finished generating mocha test grunt configs for ES2015 modules in lib/js directory.');

  return config;
};
