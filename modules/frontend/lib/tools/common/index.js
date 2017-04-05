const remoteThemeImporter = require('./lib/remoteThemeImporter');

/**
 * CoreMedia common module
 * @module
 */
module.exports = {
  /** Common remoteThemeImporter functionality for use in grunt, gulp, node scripts, etc. */
  remoteThemeImporter: remoteThemeImporter()
};
