'use strict';

/**
 * remoteThemeImporter module
 * @module
 */
module.exports = () => {
  const path = require('path');
  const fs = require('fs');

  const WORKING_DIRECTORY = process.cwd();
  const CMCONFIGFILE = path.join(WORKING_DIRECTORY, '../../.cmconfig.json');

  /**
   * @callback loggerCallback
   * @param {...Object} obj
   * @private
   */
  /**
   * Validate parameter logger.
   * @param {loggerCallback} logger
   * @private
   */
  const validateLoggerParam = logger => {
    if (typeof logger !== 'object') {
      throw new Error('No valid logger object was specified.')
    }
    if (typeof logger === 'object' && typeof logger['log'] !== 'function') {
      throw new Error('No valid property log of object logger was specified.')
    }
    if (typeof logger === 'object' && typeof logger['info'] !== 'function') {
      throw new Error('No valid property info of object logger was specified.')
    }
  };

  /**
   * Validate url.
   * @param {string} url
   * @return {boolean}
   * @private
   */
  const validateUrl = url => {
    // urls should at least begin with http
    return /^https?:\/\/(.*)$/i.test(url);
  };

  /**
   * Validate parameter url.
   * @param {string} url
   * @private
   */
  const validateUrlParam = url => {
    if (typeof url !== 'string' || url.length === 0 || !validateUrl(url)) {
      throw new Error('No valid host was specified.')
    }
  };

  /**
   * Validate authentication parameters.
   * @param {string} username
   * @param {string} password
   * @private
   */
  const validateAuthParams = (username, password) => {
    if (typeof username !== 'string' || username.length === 0) {
      throw new Error('No valid username was specified.')
    }
    if (typeof password !== 'string' || password.length === 0) {
      throw new Error('No valid password was specified.')
    }
  };

  /**
   * Validate parameter theme.
   * @param {string} theme
   * @private
   */
  const validateThemeParam = theme => {
    if (typeof theme !== 'string' || theme.length === 0) {
      throw new Error('No valid theme was specified.')
    }
  };

  /**
   * Validate parameter filepaths.
   * @param {string[]} filepaths
   * @private
   */
  const validateFilepathsParam = filepaths => {
    if (!filepaths || !Array.isArray(filepaths) || filepaths.length < 1) {
      throw new Error('No filepaths were provided.');
    }
  };

  /**
   * Validate parameter file.
   * @param {string} file
   * @private
   */
  const validateFileParam = file => {
    if (typeof file !== 'string' || file.length === 0) {
      throw new Error('No valid filepath was specified.')
    }
  };

  /**
   * Write file .cmconfig.json.
   * @param {string} url
   * @param {string} apiKey
   * @private
   */
  const createCMConfigFile = (url, apiKey) => {
    const content = JSON.stringify({url, apiKey}, null, 2);
    fs.writeFileSync(
      CMCONFIGFILE,
      content,
      {
        mode: 0o600
      }
    );
  };

  /**
   * remove file .cmconfig.json
   * @private
   */
  const removeCMConfigFile = () => {
    fs.unlinkSync(CMCONFIGFILE);
  };

  /**
   * Returns content of .cmconfig.json parsed as JSON
   * @return {{ url: string, apiKey: string }}
   * @private
   */
  const getCMConfig = () => {
    if (!fs.existsSync(CMCONFIGFILE)) {
      throw new Error('No API key found. Please login.');
    }
    return JSON.parse(
      fs.readFileSync(CMCONFIGFILE, 'utf8')
    );
  };

  /**
   * Returns a Promise for creating a zip archive.
   * @param {string} archivepath
   * @param {string} cwd
   * @param {string[]} files
   * @param {string} theme
   * @param {loggerCallback} log
   * @returns {Promise}
   * @private
   */
  const createZipArchive = (archivepath, cwd, files, theme, log) => {
    const archiver = require('archiver');

    return new Promise((resolve, reject) => {
      try {
        if (typeof archivepath !== 'string' || archivepath.length === 0) {
          throw new Error('Unable to create zip archive. No valid archive file was specified.');
        }
        const output = fs.createWriteStream(archivepath);
        const archive = archiver('zip');
        const sourcePaths = {};

        output.on('close', () => {
          resolve(files.length);
        });

        archive.on('entry', file => {
          const sp = sourcePaths[path.normalize(file.name)] || 'unknown';
          log(`Archived ${sp} ${archivepath}${path.sep}${path.normalize(file.name)}`);
        });

        archive.on('error', err => {
          log('Unable to write zip archive:', err);
          throw err;
        });

        archive.pipe(output);

        files.forEach(function (file) {
          const fstat = fileStatSync(file);
          if (!fstat) {
            log(`Unable to stat file ${file}`);
            return;
          }
          if (!fstat.isFile()) {
            log(`File ${file} should be a valid file.`);
            return;
          }

          const internalFileName = `${theme ? theme+path.sep : ''}${path.normalize(file).replace(cwd, '')}`;
          archive.file(file, {
            name: internalFileName,
            stats: fstat
          });
          sourcePaths[internalFileName] = file;
        });

        archive.finalize();

      } catch(e) {
        reject(e);
      }
    });

    function fileStatSync(file) {
      if (fs.existsSync(file)) {
        return fs.statSync(file);
      }
      return false;
    }
  };

  /**
   * Returns a Promise for executing a request.
   * @param {Object} options
   * @returns {Promise}
   * @private
   */
  const request = (options) => {
    const request = require('request');
    return new Promise((resolve, reject) => {
      try {
        request(options, (error, response, body) => {
          const result = {error, response, body};

          if (error) {
            reject(error);
          } else if (response.statusCode >= 200 && response.statusCode < 400) {
            resolve(result);
          } else {
            class HttpError extends Error {
              constructor(name, message) {
                super();
                this.name = name;
                this.message = message;
                Error.captureStackTrace(this, HttpError);
              }
            }

            let error;

            if (response.statusCode === 401) {
              error = new HttpError(`${response.statusCode}`, `${response.statusCode} ${response.statusMessage}: ${/login$/.test(options.uri) ? 'Invalid username or password.' : 'Your API key is invalid and has been removed. Please create a new API key.'}`);
            } else if (response.statusCode === 404) {
              error = new HttpError(`${response.statusCode}`, `${response.statusCode} ${response.statusMessage}: The server has not found anything matching the Request-URI. Please check the specified Studio URL.`);
            } else if (response.statusCode < 200 || response.statusCode > 399) {
              error = new HttpError(`${response.statusCode}`, `${response.statusCode} ${response.statusMessage}: Please contact your system administrator. ${body ? ', cause:' + JSON.parse(body).cause : null}`);
            }
            reject(error);
          }
        });
      } catch(e) {
        reject(e);
      }
    });
  };

  const getOptions = (url, opts) => {
    const options = {
      url,
      strictSSL: false, // disable strictSSL for own certificates
      followAllRedirects: true,
      followOriginalHttpMethod: true,
      method: 'POST'
    };
    if (opts && typeof opts === 'object') {
      if (opts.auth && typeof opts.auth === 'object') {
        Object.assign(options, {
          auth: opts.auth
        });
      }
      if (opts.apiKey && typeof opts.apiKey === 'string') {
        Object.assign(options, {
          headers: {
            'Authorization': `CMAPIKey ${opts.apiKey}`
          }
        });
      }
      if (opts.formData && typeof opts.formData === 'object') {
        Object.assign(options, {
          formData: opts.formData
        });
      }
    }
    return options;
  };

  const exports = {};

  exports.getCMConfig = getCMConfig;

  exports.validateUrl = validateUrl;

  /**
   * Returns a Promise for requesting an API key.
   * @param {string} url
   * @param {string} username
   * @param {string} password
   * @param {loggerCallback} logger
   * @returns {Promise}
   */
  exports.login =  (url, username, password, logger) => {
    return new Promise((resolve, reject) => {
      try {
        validateUrlParam(url);
        validateAuthParams(username, password);
        validateLoggerParam(logger);

        // remove possible trailing "/"
        const trimmedUrl = url.replace(/\/$/, '');
        const options = getOptions(
          `${trimmedUrl}/api/themeImporter/login`,
          {
            auth: {
              user: username,
              pass: password
            }
          }
        );

        logger.info(`Using ${trimmedUrl}`);

        request(options).then(value => {
          try {
            createCMConfigFile(trimmedUrl, value.body);
            resolve('API key has successfully been generated.');
          } catch (e) {
            reject(new Error(`API key couldn´t be stored: ${e.message}`));
          }
        }).catch(e => {
          reject(e);
        });
      } catch(e) {
        reject(e);
      }
    });
  };

  /**
   * Returns a Promise for requesting a logout.
   * @param {loggerCallback} logger
   * @returns {Promise}
   */
  exports.logout = logger => {
    return new Promise((resolve, reject) => {
      try {
        validateLoggerParam(logger);

        const { apiKey, url } = getCMConfig();

        const options = getOptions(
          `${url}/api/themeImporter/logout`,
          {
            apiKey
          }
        );

        logger.info(`Using ${url}`);

        request(options).then(() => {
          try {
            removeCMConfigFile();
            resolve('You have successfully been logged out.');
          } catch (e) {
            reject(new Error('API key couldn´t be deleted on local disk, but has been invalidated on server side.'));
          }
        }).catch(e => {
          reject(e);
        });
      } catch (e) {
        reject(e);
      }
    });
  };

  /**
   * Returns a Promise for requesting a user verification.
   * @param {loggerCallback} logger
   * @returns {Promise}
   */
  exports.whoami = logger => {
    return new Promise((resolve, reject) => {
      try {
        validateLoggerParam(logger);

        const { apiKey, url } = getCMConfig();

        const options = getOptions(
          `${url}/api/themeImporter/whoami`,
          {
            apiKey
          }
        );

        logger.info(`Using ${url}`);

        request(options).then(value => {
          const parsedBody = JSON.parse(value.body);
          resolve(`You are logged in as user '${parsedBody.name}' (id=${parsedBody.id}).`);
        }).catch(e => {
          if (e.name === '401') {
            try {
              removeCMConfigFile();
            } catch(e) {
              // .cmconfig.json couldn´t be deleted
            }
          }
          reject(e);
        });
      } catch (e) {
        reject(e);
      }
    });
  };

  /**
   * Returns a Promise for requesting a theme upload.
   * @param {string} theme
   * @param {loggerCallback} logger
   * @returns {Promise}
   */
  exports.uploadTheme = (theme, logger) => {
    return new Promise((resolve, reject) => {
      try {
        validateThemeParam(theme);
        validateLoggerParam(logger);

        const { apiKey, url } = getCMConfig();

        const THEMEZIPFILE = path.join(WORKING_DIRECTORY, '../../target', 'themes', `${theme}-theme.zip`);
        if (!fs.existsSync(THEMEZIPFILE)) {
          throw new Error(`${THEMEZIPFILE} doesn´t exist.`);
        }

        const options = getOptions(
          `${url}/api/themeImporter/upload`,
          {
            apiKey,
            formData: {
              path: '/Themes',
              clean: 'true',
              file: fs.createReadStream(THEMEZIPFILE)
            }
          }
        );

        logger.info(`Using ${url}`);

        request(options).then(() => {
          resolve(`Theme has successfully been uploaded (file ${path.basename(THEMEZIPFILE)}).`);
        }).catch(e => {
          if (e.name === '401') {
            try {
              removeCMConfigFile();
            } catch(e) {
              // .cmconfig.json couldn´t be deleted
            }
          }
          reject(e);
        });
      } catch (e) {
        reject(e);
      }
    });
  };

  /**
   * Returns a Promise for requesting a theme descriptor upload.
   * @param {string} theme
   * @param {string[]} filepaths
   * @param {loggerCallback} logger
   * @returns {Promise}
   */
  exports.uploadDescriptor = (theme, filepaths, logger) => {
    return new Promise((resolve, reject) => {
      try {
        validateThemeParam(theme);
        validateFilepathsParam(filepaths);
        validateLoggerParam(logger);

        const { apiKey, url } = getCMConfig();

        const ARCHIVE = path.normalize(`../../target/themes/${theme}-update.zip`);
        const CWD = path.normalize(`../../target/resources/`);
        const FILE = path.join(WORKING_DIRECTORY, ARCHIVE);

        createZipArchive(FILE, CWD, filepaths, null, logger.log)
        .then(count => {
          logger.log(`Compressed ${count} files.`);
          logger.log(`Prepare upload of file: ${path.basename(FILE)}`);

          const options = getOptions(
            `${url}/api/themeImporter/upload`,
            {
              apiKey,
              formData: {
                path: '/Themes',
                clean: 'false',
                file: fs.createReadStream(FILE)
              }
            }
          );

          logger.info(`Using ${url}`);

          request(options).then(() => {
            resolve(`${count} ${(count === 1 ? 'file has' : 'files have')} successfully been uploaded.`);
          }).catch(e => {
            reject(e);
          });
        })
        .catch(e => {
          reject(new Error(`An error occured during preparing upload: ${e.message}`));
        });
      } catch(e) {
        reject(e);
      }
    });
  };

  /**
   * Returns a Promise for requesting a file upload.
   * @param {string} theme
   * @param {string[]} filepaths
   * @param {loggerCallback} logger
   * @returns {Promise}
   */
  exports.uploadFile = (theme, filepaths, logger) => {
    return new Promise((resolve, reject) => {
      try {
        validateThemeParam(theme);
        validateFilepathsParam(filepaths);
        validateLoggerParam(logger);

        const { apiKey, url } = getCMConfig();

        const ARCHIVE = path.normalize(`../../target/themes/${theme}-update.zip`);
        const CWD = path.normalize(`../../target/resources/themes/${theme}/`);
        const FILE = path.join(WORKING_DIRECTORY, ARCHIVE);

        createZipArchive(FILE, CWD, filepaths, theme, logger.log)
        .then(count => {
          logger.log(`Compressed ${count} files.`);
          logger.log(`Prepare upload of file: ${path.basename(FILE)}`);

          const options = getOptions(
            `${url}/api/themeImporter/upload`,
            {
              apiKey,
              formData: {
                path: '/Themes',
                clean: 'false',
                file: fs.createReadStream(FILE)
              }
            }
          );

          logger.info(`Using ${url}`);

          request(options).then(() => {
            resolve(`${count} ${(count === 1 ? 'file has' : 'files have')} successfully been uploaded.`);
          }).catch(e => {
            reject(e);
          });
        })
        .catch(e => {
          reject(new Error(`An error occured during preparing upload: ${e.message}`));
        });
      } catch(e) {
        reject(e);
      }
    });
  };

  /**
   * Returns a Promise for requesting a file delete.
   * @param {string} theme
   * @param {string} file
   * @param {loggerCallback} logger
   * @returns {Promise}
   */
  exports.deleteFile = (theme, file, logger) => {
    return new Promise((resolve) => {
      try {
        validateThemeParam(theme);
        validateFileParam(file);
        validateLoggerParam(logger);

        const { apiKey, url } = getCMConfig();

        const CWD = `../../target/resources/themes/${theme}/`;
        const filePath = `${theme}/${file.replace(CWD, '')}`;

        const options = getOptions(
          `${url}/api/themeImporter/delete`,
          {
            apiKey,
            formData: {
              path: '/Themes',
              filePath
            }
          }
        );

        logger.log(`Delete file ${filePath} on remote server.`);
        logger.info(`Using ${url}`);

        request(options).then(() => {
          resolve({
            type: 'SUCCESS',
            text: `File ${path.basename(file)} has successfully been deleted.`
          });
        }).catch(e => {
          resolve({
            type: 'ERROR',
            text: e.message
          });
        });
      } catch (e) {
        resolve({
          type: 'ERROR',
          text: `File ${path.basename(file)} has not been deleted due to the following error: ${e.message}`
        });
      }
    });
  };

  return exports;
};
