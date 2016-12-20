/**
 * Coremedia Logger module.
 *
 * The Logger is disabled by default.
 *
 * usage:
 * 1) enable logging:
 * setLevel(LEVEL.ALL);
 *
 * 2) print to log:
 * log("log this");
 * debug("debug this");
 * info("info this");
 * warn("warn this");
 * error("error this");
 *
 * 3) disable logging:
 * setLevel(LEVEL.OFF);
 *
 * @module logger
 */
import { domReady } from '../utils/index';
import * as Logger from './logger.js';

// DOCUMENT READY
domReady(() => {
  // disable logging if developerMode is inactive
  // insure that there´s no logging in production
  if (!document.querySelector('[data-cm-developer-mode]')) {
    Logger.setLevel(Logger.LEVEL.OFF);
  }
});

export default Logger;
