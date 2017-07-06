'use strict';

const tinylr = require('tiny-lr');

// Holds the servers out of scope in case watch is reloaded
let servers = Object.create(null);

class LiveReload {
  init(options, logger) {
    const host = `${(options.host || '*')}:${options.port}`;

    if (servers[host]) {
      this.server = servers[host];
    } else {
      this.server = tinylr(options);
      this.server.server.removeAllListeners('error');
      this.server.server.on('error', err => {
        if (err.code === 'EADDRINUSE') {
          logger.error(`Port ${options.port} is already in use by another process.`);
        } else {
          logger.error(err);
        }
      });
      this.server.listen(options.port, options.host, err => {
        if (err) {
          return logger.error(err);
        }
        logger.log(`Live reload server started on ${host}`);
      });
      servers[host] = this.server;
    }
  }

  trigger(logger, files=['']) {
    if (!this.server) {
      logger.error(`Live reload server has not been started.`);
      return;
    }
    logger.log(`Live reloading ${files.join(', ')}...`);
    this.server.changed({body: {files: files}});
  };
}

const livereload = new LiveReload();

module.exports = livereload;
