'use strict';

const path = require('path');
const fs = require('fs');

const { createEnvFile, getEnv } = require('../environment');
const api = require('./api');
const utils = require('./utils');

const WORKING_DIRECTORY = process.cwd();

/**
 * Validate url.
 * @param {string} url
 */
const validateUrl = utils.validateUrl;

/**
 * Validate username.
 * @param {string} username
 */
const validateUsername = utils.validateUsername;

/**
 * Validate password.
 * @param {string} password
 */
const validatePassword = utils.validatePassword;

/**
 * Validate parameter theme.
 * @param {string} themeName
 */
const validateThemeName = utils.validateThemeName;

/**
 * Validate parameter fileList.
 * @param {string[]} fileList
 */
const validateFileList = utils.validateFileList;

/**
 * Validate parameter file.
 * @param {string} file
 */
const validateFile = utils.validateFile;

/**
 * Returns a Promise for requesting an API key.
 * @param {string} studioUrl
 * @param {string} previewUrl
 * @param {string} username
 * @param {string} password
 * @returns {Promise}
 */
const login = (studioUrl, previewUrl, username, password) => {
  return new Promise((resolve, reject) => {
    const trimmedStudioUrl = studioUrl.replace(/\/$/, '');

    console.log(`Using ${trimmedStudioUrl}`);

    api.login(
      trimmedStudioUrl,
      username,
      password
    ).then(apiKey => {
      utils.createApiKeyFile(apiKey);
      return api.whoami(trimmedStudioUrl, apiKey);
    }).then(user => {
      let previewUrlDevMode = utils.getPreviewUrlDevMode(previewUrl, user.id);
      createEnvFile({
        studioUrl: trimmedStudioUrl,
        previewUrl: previewUrlDevMode
      });
      resolve('API key has successfully been generated.');
    }).catch(e => {
      reject(e);
    });
  });
};

/**
 * Returns a Promise for requesting a logout.
 * @returns {Promise}
 */
const logout = () => {
  return new Promise((resolve, reject) => {
    try {
      const { studioUrl } = getEnv();
      const apiKey = utils.getApiKey();

      console.log(`Using ${studioUrl}`);

      api.logout(
        studioUrl,
        apiKey
      ).then(() => {
        utils.removeApiKeyFile();
        resolve('You have successfully been logged out.');
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
 * @returns {Promise}
 */
const whoami = () => {
  return new Promise((resolve, reject) => {
    try {
      const { studioUrl } = getEnv();
      const apiKey = utils.getApiKey();

      console.log(`Using ${studioUrl}`);

      api.whoami(
        studioUrl,
        apiKey
      ).then(user => {
        resolve(user);
      }).catch(e => {
        if (e.name === 'EUNAUTHORIZED') {
          utils.removeApiKeyFile();
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
 * @param {string} themeName
 * @param {string} targetPath
 * @returns {Promise}
 */
const uploadTheme = (themeName, targetPath) => {
  return new Promise((resolve, reject) => {
    try {
      const { studioUrl } = getEnv();
      const apiKey = utils.getApiKey();
      const THEME_ZIPFILE = path.join(WORKING_DIRECTORY, targetPath, 'themes', `${themeName}-theme.zip`);

      if (!fs.existsSync(THEME_ZIPFILE)) {
        reject(new Error(`${THEME_ZIPFILE} doesn´t exist.`));
      } else {
        console.log(`Using ${studioUrl}`);
        console.log('Upload theme to remote server.');

        api.upload(
          studioUrl,
          apiKey,
          THEME_ZIPFILE,
          'true'
        ).then(() => {
          resolve(THEME_ZIPFILE);
        }).catch(e => {
          if (e.code === 'EUNAUTHORIZED') {
            utils.removeApiKeyFile();
          }
          reject(e);
        });
      }
    } catch (e) {
      reject(e);
    }
  });
};

/**
 * Returns a Promise for requesting a theme descriptor upload.
 * @param {string} themeName
 * @param {string} targetPath
 * @param {string[]} fileList
 * @returns {Promise}
 */
const uploadDescriptor = (themeName, targetPath, fileList, verbose=false) => {
  return new Promise((resolve, reject) => {
    try {
      console.log(`Upload descriptor to remote server.`);

      if (!utils.validateFileList(fileList)) {
        throw new Error('No files were provided.')
      }

      const { studioUrl } = getEnv();
      const apiKey = utils.getApiKey();
      const CWD = path.normalize(path.join(targetPath, 'resources/'));
      const ZIP_FILE = path.join(WORKING_DIRECTORY, targetPath,  `themes/${themeName}-update.zip`);

      utils.createZipArchive(ZIP_FILE, CWD, fileList, null, verbose)
      .then(count => {
        console.log(`Using ${studioUrl}`);
        if (verbose) {
          console.log(`Prepare upload of file: ${path.basename(ZIP_FILE)}`);
        }

        api.upload(
          studioUrl,
          apiKey,
          ZIP_FILE
        ).then(() => {
          resolve(count);
        }).catch(e => {
          reject(e);
        });
      }).catch(e => {
        reject(new Error(`An error occured while preparing for upload: ${e.message}`));
      });
    } catch(e) {
      reject(e);
    }
  });
};

/**
 * Returns a Promise for requesting a file upload.
 * @param {string} themeName
 * @param {string} targetPath
 * @param {string[]} fileList
 * @returns {Promise}
 */
const uploadFile = (themeName, targetPath, fileList, verbose=false) => {
  return new Promise((resolve, reject) => {
    try {
      console.log(`Upload changed files to remote server.`);

      if (!utils.validateFileList(fileList)) {
        throw new Error('No files were provided.')
      }

      const { studioUrl } = getEnv();
      const apiKey = utils.getApiKey();
      const CWD = path.normalize(path.join(targetPath, `resources/themes/${themeName}/`));
      const ZIP_FILE = path.join(WORKING_DIRECTORY, targetPath, `themes/${themeName}-update.zip`);

      utils.createZipArchive(ZIP_FILE, CWD, fileList, themeName, verbose)
      .then(count => {
        console.log(`Using ${studioUrl}`);
        if (verbose) {
          console.log(`Prepare upload of file: ${path.basename(ZIP_FILE)}`);
        }

        api.upload(
          studioUrl,
          apiKey,
          ZIP_FILE
        ).then(() => {
          resolve(count);
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
 * @param {string} themeName
 * @param {string} targetPath
 * @param {string} file
 * @returns {Promise}
 */
const deleteFile = (themeName, targetPath, file) => {
  return new Promise((resolve) => {
    try {
      console.log(`Delete file ${file} on remote server.`);

      if (!utils.validateFile(file)) {
        throw new Error('No file was provided.')
      }

      const { studioUrl } = getEnv();
      const apiKey = utils.getApiKey();
      const CWD = path.normalize(path.join(targetPath, `resources/themes/${themeName}/`));
      const FILE_PATH = `${themeName}/${file.replace(CWD, '')}`;

      console.log(`Using ${studioUrl}`);

      api.deleteFile(
        studioUrl,
        apiKey,
        FILE_PATH
      ).then(() => {
        resolve({
          type: 'SUCCESS',
          file: path.basename(file)
        });
      }).catch(e => {
        resolve({
          type: 'ERROR',
          file: path.basename(file),
          error: e.message
        });
      });
    } catch (e) {
      resolve({
        type: 'ERROR',
        file: path.basename(file),
        error: e.message
      });
    }
  });
};

/**
 * remoteThemeImporter module
 * @module
 */
module.exports = {
  validateUrl,
  validateUsername,
  validatePassword,
  validateThemeName,
  validateFileList,
  validateFile,
  login,
  logout,
  whoami,
  uploadTheme,
  uploadDescriptor,
  uploadFile,
  deleteFile
};
