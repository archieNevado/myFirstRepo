const remoteThemeImporter = require('./lib/remoteThemeImporter');
const livereload = require('./lib/livereload');

/**
 * CoreMedia common module
 * @module
 */
module.exports = {
  /** Common remoteThemeImporter functionality for use in grunt, gulp, node scripts, etc. */
  remoteThemeImporter: remoteThemeImporter(),
  /** Common livereload functionality for use in grunt, gulp, node scripts, etc. */
  livereload: livereload
};
