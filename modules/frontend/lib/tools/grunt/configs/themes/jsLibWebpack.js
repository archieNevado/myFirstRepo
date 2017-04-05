/**
 * Generates grunt config for webpack grunt task for all ES2015 modules in the lib/js directory.
 */
'use strict';

module.exports = (grunt, {themeConfig: {name: themeName}}) => {
  grunt.verbose.writeln('Generate webpack grunt configs for ES2015 modules in lib/js directory.');

  const fs = require('fs');
  const path = require('path');

  const WORKING_DIRECTORY = process.cwd();
  const JS_LIBS_DIRECTORY = path.join(WORKING_DIRECTORY, '../../lib/js');
  const THEME_TARGET_JS_LIB_DIRECTORY = path.join(WORKING_DIRECTORY, '../../target/resources/themes', themeName, 'js/lib');

  const config = {
    tasks: {
      webpack: {
        jslib: {
          entry: {},
          output: {
            path: THEME_TARGET_JS_LIB_DIRECTORY,
            filename: '[name].js',
            library: ['coremedia', 'blueprint', '[name]'],
            libraryTarget: 'this'
          },
          progress: false,
          module: {
            preLoaders: [
              {
                loader: 'eslint-loader',
                test: /\.js$/,
                include: [
                  JS_LIBS_DIRECTORY
                ]
              }
            ],
            loaders: [
              {
                loader: 'babel-loader',
                test: /\.js$/,
                include: [
                  JS_LIBS_DIRECTORY
                ],
                query: {
                  cacheDirectory: true,
                  comments: false,
                  plugins: ['add-module-exports', 'transform-runtime']
                }
              }
            ]
          }
        }
      }
    }
  };

  const generateJSLibWebpackConfig = subdir => {
    const JS_LIB_DIRECTORY = path.join(JS_LIBS_DIRECTORY, subdir);
    const JS_LIB_INDEX = path.join(JS_LIB_DIRECTORY, 'index.js');

    grunt.verbose.writeln(`Generate grunt config for webpack of JS library ${subdir}.`);

    // check for subdir
    if (typeof subdir !== 'string' || subdir.length === 0) {
      grunt.log.errorlns("Subdir is undefined. Can't load it.");
      return;
    }

    // check directory
    if (!fs.existsSync(JS_LIB_INDEX)) {
      grunt.log.errorlns(`No JS library found in ${JS_LIB_DIRECTORY}`);
      return;
    }

    // create config
    config.tasks.webpack.jslib.entry[subdir] = JS_LIB_INDEX;

    grunt.verbose.writeln(`Finished generating webpack grunt config for JS library ${subdir}.`);
  };

  // browse through all bricks - delegate to generateJSLibWebpackConfig()
  fs.readdirSync(JS_LIBS_DIRECTORY).forEach(file => {
    // Don´t transpile JS-files inside utils and legacy directory
    if (file !== '.' && file !== '..' && file !== 'utils' && file !== 'legacy') {
      // check, if brick includes JavaScript
      if (fs.statSync(path.join(JS_LIBS_DIRECTORY, file)).isDirectory()) {
        // generate grunt config
        generateJSLibWebpackConfig(file);
      }
    }
  });

  grunt.verbose.oklns('Finished generating webpack grunt configs for ES2015 modules in lib/js directory.');

  return config;
};
