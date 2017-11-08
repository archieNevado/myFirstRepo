'use strict';

const tinylr = require('tiny-lr');
const path = require('path');
const fs = require('fs');
const selfsigned = require('selfsigned');
const chalk = require('chalk');

// Holds the servers out of scope in case watch is reloaded
let servers = Object.create(null);

class LiveReload {
  init(options, verbose=false) {
    const HOST = `${(options.HOST || 'localhost')}:${options.port}`;
    const WORKING_DIRECTORY = process.cwd();
    const CONFIG_DIRECTORY = path.resolve(WORKING_DIRECTORY, '../../config');
    const CERTFILE = path.join(CONFIG_DIRECTORY, 'livereload.pem');

    let certExists = fs.existsSync(CERTFILE);

    if (certExists) {
      const certStat = fs.statSync(CERTFILE);
      const certExpiration = 1000 * 60 * 60 * 24 * 30;
      const now = new Date();

      // if certificate is older than 30 days, delete it to create a new one.
      if (certStat < now - certExpiration) {
        fs.unlinkSync(CERTFILE);
        certExists = false;
      }
    }

    if (!certExists) {
      try {
        const attributes = [{ name: 'commonName', value: 'localhost' }];
        const pems = selfsigned.generate(attributes, {
          algorithm: 'sha256',
          days: 30,
          keySize: 2048
        });

        if (!fs.existsSync(CONFIG_DIRECTORY)) {
          fs.mkdirSync(CONFIG_DIRECTORY);
        }
        fs.writeFileSync(CERTFILE, pems.private + pems.cert, { encoding: 'utf-8' });
      } catch (e) {
        console.error(chalk.red(`An error occured while trying to store the environment variables: ${e.message}`));
      }
    }

    const cert = fs.readFileSync(CERTFILE);
    options.key = cert;
    options.cert = cert;

    if (servers[HOST]) {
      this.server = servers[HOST];
    } else {
      this.server = tinylr(options);
      this.server.server.removeAllListeners('error');
      this.server.server.on('error', err => {
        if (err.code === 'EADDRINUSE') {
          console.error(chalk.red(`Port ${options.port} is already in use by another process.`));
        } else {
          console.error(chalk.red(err));
        }
      });
      this.server.listen(options.port, options.HOST, err => {
        if (err) {
          return console.error(console.red(err));
        }
        if (verbose) {
          console.log(`Live reload server started on ${HOST}`);
        }
      });
      servers[HOST] = this.server;
    }
  }

  trigger(files=[''], verbose=false) {
    if (!this.server) {
      console.error(chalk.red(`Live reload server has not been started.`));
      return;
    }
    if (verbose) {
      console.log(`Live reloading ${files.join(', ')}...`);
    }
    this.server.changed({body: {files: files}});
  }

  getHost() {
    return Object.keys(servers)[0];
  }
}

const livereload = new LiveReload();

module.exports = livereload;
