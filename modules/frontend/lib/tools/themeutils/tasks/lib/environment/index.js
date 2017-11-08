'use strict';

const path = require('path');
const fs = require('fs');

const WORKING_DIRECTORY = process.cwd();
const CONFIG_DIRECTORY = path.resolve(WORKING_DIRECTORY, '../../config');
const ENV_FILE = path.join(CONFIG_DIRECTORY, 'env.json');

/**
 * Extended Error Class for errors handling env.json or apikey.txt files.
 */
class ConfigFileError extends Error {
    constructor(...args) {
        super(...args);
        Error.captureStackTrace(this, ConfigFileError);
        this.code = 'ECONFIGFILE';
    }
}

/**
 * Write variables into env.json
 * @param {Object} vars
 */
const createEnvFile = vars => {
	try {
		let env = {};
    if (!fs.existsSync(CONFIG_DIRECTORY)) {
      fs.mkdirSync(CONFIG_DIRECTORY);
    } else if (fs.existsSync(ENV_FILE)) {
			env = JSON.parse(
				fs.readFileSync(ENV_FILE, 'utf8')
			);
		}
		env = Object.assign(env, vars);
		const data = JSON.stringify(env, null, 2);
		fs.writeFileSync(
			ENV_FILE,
			data,
			{
				encoding: 'utf8',
				mode: 0o600
			}
		);
	} catch (e) {
		throw new ConfigFileError(`An error occured while trying to store the environment variables: ${e.message}`);
	}
};

/**
 * Returns content of env.json parsed as JSON
 * @return {Object}
 */
const getEnv = () => {
	if (!fs.existsSync(ENV_FILE)) {
		throw new ConfigFileError('No environment file found. Please login.');
	}
	try {
		const env = JSON.parse(
			fs.readFileSync(ENV_FILE, 'utf8')
		);
		return env;
	} catch (e) {
		throw new ConfigFileError('The environment file couldn´t be read.')
	}
};

/**
 * environment module
 * @module
 */
module.exports = {
  createEnvFile,
  getEnv
};
