'use strict';

const path = require('path');
const fs = require('fs');

const WORKING_DIRECTORY = process.cwd();
const CONFIG_DIRECTORY = path.resolve(WORKING_DIRECTORY, '../../config');
const APIKEY_FILE = path.join(CONFIG_DIRECTORY, 'apikey.txt');

/**
 * Extended Error Class for errors handling apikey.txt file.
 */
class ApiKeyFileError extends Error {
    constructor(...args) {
        super(...args);
        Error.captureStackTrace(this, ApiKeyFileError);
        this.code = 'EAPIKEYFILE';
    }
}

/**
 * Extended Error Class for errors handling env.json or apikey.txt files.
 */
class ZipFileError extends Error {
    constructor(...args) {
        super(...args);
        Error.captureStackTrace(this, ZipFileError);
        this.code = 'EZIPFILE';
    }
}

/**
 * Validate url.
 * @param {string} url
 * @return {boolean}
 */
const validateUrl = url =>
  (typeof url === 'string' && url.length > 0 && /^https?:\/\/(.*)$/i.test(url));

  /**
   * Validate username.
   * @param {string} username
   * @return {boolean}
   */
const validateUsername = username =>
  (typeof username === 'string' && username.length > 0);

  /**
   * Validate password.
   * @param {string} password
   * @return {boolean}
   */
const validatePassword = password =>
  (typeof password === 'string' && password.length > 0);

/**
 * Validate parameter theme.
 * @param {string} themeName
 * @return {boolean}
 */
const validateThemeName = themeName => (typeof themeName === 'string' && themeName.length > 0);

/**
 * Validate parameter fileList.
 * @param {string[]} fileList
 * @return {boolean}
 */
const validateFileList = fileList => (fileList && Array.isArray(fileList) && fileList.length > 0);

/**
 * Validate parameter file.
 * @param {string} file
 * @return {boolean}
 */
const validateFile = file => (typeof file === 'string' && file.length > 0);

/**
 * Write file apikey.txt
 * @param {string} apiKey
 */
const createApiKeyFile = apiKey => {
  try {
    if (!fs.existsSync(CONFIG_DIRECTORY)) {
      fs.mkdirSync(CONFIG_DIRECTORY);
    }
    fs.writeFileSync(
      APIKEY_FILE,
      apiKey,
      {
        encoding: 'utf8',
        mode: 0o600
      }
    );
  } catch (e) {
    throw new ApiKeyFileError(`An error occured while trying to store the API key: ${e.message}`);
  }
};

/**
 * remove file apikey.txt
 */
const removeApiKeyFile = () => {
  try {
    fs.unlinkSync(APIKEY_FILE);
  } catch (e) {
    // apikey.txt couldn´t be deleted
  }
};

/**
 * Returns content of apikey.txt
 * @return {string} apiKey
 */
const getApiKey = () => {
  if (!fs.existsSync(APIKEY_FILE)) {
    throw new ApiKeyFileError('No API key found. Please login.');
  }
  const apiKey = fs.readFileSync(APIKEY_FILE, 'utf8');
  if (typeof apiKey !== 'string' || apiKey.length === 0) {
    throw new ApiKeyFileError('No API key found. Please login.');
  }
  return apiKey;
};

/**
 * Returns a Promise for creating a zip archive.
 * @param {string} archivepath
 * @param {string} cwd
 * @param {string[]} files
 * @param {string} theme
 * @returns {Promise}
 */
const createZipArchive = (archivepath, cwd, files, theme, verbose=false) => {
  const archiver = require('archiver');

  return new Promise((resolve, reject) => {
    try {
      if (typeof archivepath !== 'string' || archivepath.length === 0) {
        throw new ZipFileError('Unable to create zip archive. No valid archive file was specified.');
      }
      const output = fs.createWriteStream(archivepath);
      const archive = archiver('zip');
      const sourcePaths = {};

      output.on('close', () => {
        const count = files.length;
        if (verbose) {
          console.log(`Compressed ${count} files.`);
        }
        resolve(count);
      });

      archive.on('entry', file => {
        const sp = sourcePaths[path.normalize(file.name)] || 'unknown';
        if (verbose) {
          console.log(`Archived ${sp} ${archivepath}${path.sep}${path.normalize(file.name)}`);
        }
      });

      archive.on('error', err => {
        if (verbose) {
          console.log('Unable to write zip archive:', err);
        }
        throw new ZipFileError(err.message);
      });

      archive.pipe(output);

      files.forEach(function (file) {
        const fstat = fileStatSync(file);
        if (!fstat) {
          if (verbose) {
            console.log(`Unable to stat file ${file}`);
          }
          return;
        }
        if (!fstat.isFile()) {
          if (verbose) {
            console.log(`File ${file} should be a valid file.`);
          }
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
 * Return Preview URL including userVariant Parameter for current user.
 * @param {string} previewUrl
 * @param {number} userId
 * @return {string}
 */
const getPreviewUrlDevMode = (previewUrl, userId) => {
  let previewUrlDevMode;
  if (previewUrl) {
    previewUrlDevMode = previewUrl.replace(/\/$/, '');
    const userVariantParam = `userVariant=${userId}`;

    if (/userVariant=[0-9]*/.test(previewUrlDevMode)) {
      // replace existing userVariant parameter to ensure that correct user id is provided
      previewUrlDevMode = previewUrlDevMode.replace(/userVariant=[0-9]*/, userVariantParam);
    } else {
      // append userVariant parameter
      const delimiter = /\?/.test(previewUrlDevMode) ? '&' : '?';
      previewUrlDevMode += `${delimiter}${userVariantParam}`;
    }
  }
  return previewUrlDevMode;
};

/**
 * utils module
 * @module
 */
module.exports = {
  validateUrl,
  validateUsername,
  validatePassword,
  validateThemeName,
  validateFileList,
  validateFile,
  createApiKeyFile,
  removeApiKeyFile,
  getApiKey,
  createZipArchive,
  getPreviewUrlDevMode
};
