'use strict';

const fs = require('fs');

class HttpError extends Error {
    constructor(code, message) {
        super(message);
        this.code = code;
        Error.captureStackTrace(this, HttpError);
    }
}

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
          let httpError;

          if (response.statusCode === 401) {
            httpError = new HttpError('EUNAUTHORIZED', `${response.statusCode} ${response.statusMessage}: ${/login$/.test(options.url) ? 'Invalid username or password.' : 'Your API key is invalid and has been removed. Please create a new API key.'}`);
          } else if (response.statusCode === 404) {
            httpError = new HttpError('ENOTFOUND', `${response.statusCode} ${response.statusMessage}: The server has not found anything matching the Request-URI. Please check the specified Studio URL.`);
          } else if (response.statusCode < 200 || response.statusCode > 399) {
            let cause;
            try {
              if (body) {
                cause = JSON.parse(body).cause;
              }
            } catch(error) {
              // no cause available
            }
            httpError = new HttpError('EMISC', `${response.statusCode} ${response.statusMessage}: Please contact your system administrator. ${cause && `, cause: ${cause}`}`);
          }
          reject(httpError);
        }
      });
    } catch(e) {
      reject(e);
    }
  });
};

/**
 * Returns an options object to be passed to request function.
 * @param {string} url
 * @param {Object} opts
 * @returns {Object}
 * @private
 */
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

/**
 * Returns a Promise for requesting an API key.
 * @param {string} url
 * @param {string} username
 * @param {string} password
 * @returns {Promise}
 */
const login =  (url, username, password) => {
  return new Promise((resolve, reject) => {
    try {
      const options = getOptions(
        `${url}/api/themeImporter/login`,
        {
          auth: {
            user: username,
            pass: password
          }
        }
      );

      request(options).then(value => {
        resolve(value.body);
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
 * @param {string} url
 * @param {string} apiKey
 * @returns {Promise}
 */
const logout = (url, apiKey) => {
  return new Promise((resolve, reject) => {
    const options = getOptions(
      `${url}/api/themeImporter/logout`,
      {
        apiKey
      }
    );

    request(options).then(() => {
      try {
        resolve();
      } catch (e) {
        reject(new Error('API key couldn´t be deleted on local disk, but has been invalidated on server side.'));
      }
    }).catch(e => {
      reject(e);
    });
  });
};

/**
 * Returns a Promise for requesting a user verification.
 * @param {string} url
 * @param {string} apiKey
 * @returns {Promise}
 */
const whoami = (url, apiKey) => {
  return new Promise((resolve, reject) => {
    const options = getOptions(
      `${url}/api/themeImporter/whoami`,
      {
        apiKey
      }
    );

    request(options).then(value => {
      resolve(JSON.parse(value.body));
    }).catch(e => {
      reject(e);
    });
  });
};

/**
 * Returns a Promise for requesting a theme upload.
 * @param {string} url
 * @param {string} apiKey
 * @param {string} file
 * @param {string} [clean=false]
 * @returns {Promise}
 */
const upload = (url, apiKey, file, clean = 'false') => {
  return new Promise((resolve, reject) => {
    const options = getOptions(
      `${url}/api/themeImporter/upload`,
      {
        apiKey,
        formData: {
          path: '/Themes',
          clean,
          file: fs.createReadStream(file)
        }
      }
    );

    request(options).then(() => {
      resolve();
    }).catch(e => {
      reject(e);
    });
  });
};

/**
 * Returns a Promise for requesting a file delete.
 * @param {string} url
 * @param {string} apiKey
 * @param {string} file
 * @returns {Promise}
 */
const deleteFile = (url, apiKey, file) => {
  return new Promise((resolve, reject) => {
    const options = getOptions(
      `${url}/api/themeImporter/delete`,
      {
        apiKey,
        formData: {
          path: '/Themes',
          file
        }
      }
    );

    request(options).then(() => {
      resolve();
    }).catch(e => {
      reject(e);
    });
  });
};

/**
 * api module
 * @module
 */
module.exports = {
  login,
  logout,
  whoami,
  upload,
  deleteFile
};
