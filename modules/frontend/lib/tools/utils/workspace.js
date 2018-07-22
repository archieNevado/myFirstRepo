"use strict";

const path = require("path");
const fs = require("fs");
const glob = require("glob");
const closestPackage = require("closest-package");
const selfsigned = require("selfsigned");
const { getInstalledPathSync } = require("get-installed-path");

const cmLogger = require("@coremedia/cm-logger");

const PKG_NAME = "@coremedia/tool-utils";
const DEFAULT_CONFIG_PATH = "config";
const DEFAULT_THEMES_PATH = "themes";
const DEFAULT_TARGET_PATH = "target";
const DEFAULT_BRICKS_PATH = "bricks";

const DEFAULT_VARIANT = "default";

/**
 * Extended Error Class for errors handling env.json or apikey.txt files.
 */
class ConfigFileError extends Error {
  constructor(...args) {
    super(...args);
    Error.captureStackTrace(this, ConfigFileError);
    this.code = "ECONFIGFILE";
  }
}

/**
 * Extended Error Class for errors handling apikey.txt file.
 */
class ApiKeyFileError extends Error {
  constructor(...args) {
    super(...args);
    Error.captureStackTrace(this, ApiKeyFileError);
    this.code = "EAPIKEYFILE";
  }
}

// Common cm-logger instance for this module
const log = cmLogger.getLogger({
  name: PKG_NAME,
  level: "info",
});

/**
 * @var {Object} a cached the configuration to avoid running the same code multiple times
 */
let wsConfig;

/**
 * @var {Object} a cached the configuration to avoid running the same code multiple times
 */
let themeConfig;

/**
 * @type {Object} a cached mapping of node modules by name and their {@type CoreMediaEntry}. If a key is not added here,
 *                the module has not been checked yet otherwise the value specifies at least an empty object
 */
const cachedPackageJsonsByName = {};

/**
 * Represents a "coremedia"-Entry in the package.json
 * @class CoreMediaEntry
 */
class CoreMediaEntry {
  constructor(applyFrom) {
    applyFrom = applyFrom || {};
    /**
     * @member {String} indicates the type of the CoreMedia package
     */
    this.type = applyFrom.type || null;
    /**
     * @member {String} indicates the initialization script for the CoreMedia package
     */
    this.init = applyFrom.init || null;
    /**
     * @member {Array} indicates in which variants the smart import mechanism will apply
     */
    this.smartImport = applyFrom.smartImport || [DEFAULT_VARIANT];
    /**
     * @member {Object} indicates a mapping for modules to be shimmed
     */
    this.shim = applyFrom.shim || {};
  }
}

/**
 * Represents a module to shim.
 */
class Shim {
  /**
   * Creates a new Shim
   * @param {String} target module path
   */
  constructor(target) {
    this._target = target;
    this._imports = {};
    this._exports = {};
  }

  /**
   * The target module for the shim.
   * @returns {String} the target module for the shim.
   */
  getTarget() {
    return this._target;
  }

  /** Add a new import to the shim
   * @param variableName variable name to be imported from
   * @param moduleName module name to import from
   */
  addImport(variableName, moduleName) {
    this._imports[variableName] = moduleName;
  }

  /**
   * Mapping of all imported variable names to a module path
   * @returns {Object}
   */
  getImports() {
    // immutable
    return { ...this._imports };
  }

  /**
   * Add a new export to the shim
   * @param variableName variable name to be exported into
   * @param targetVariableName variable name of the target module
   */
  addExport(variableName, targetVariableName) {
    this._exports[variableName] = targetVariableName;
  }

  /**
   * Mapping of all exports variable names to a variable names of the target module
   * @returns {Object}
   */
  getExports() {
    // immutable
    return { ...this._exports };
  }
}

/**
 * Returns the root of the CoreMedia Frontend Workspace
 * @return {[type]} [description]
 */
const getWSPackageJson = () => {
  let cwd = process.cwd();

  const check = () => {
    const packageJsonPath = closestPackage.sync(cwd);

    // closestPackage.sync(cwd) returns null, if no frontend package.json has been foundby searchng upwards from current directory.
    if (!packageJsonPath) {
      throw new Error(`No CoreMedia Frontend Workspace found!`);
    }

    const packageJson = require(packageJsonPath);
    if (
      packageJson.coremedia &&
      packageJson.coremedia.type &&
      packageJson.coremedia.type === "workspace"
    ) {
      return packageJsonPath;
    }
    return next();
  };

  const next = () => {
    cwd = path.join(cwd, "..");
    return check();
  };

  return check();
};

/**
 * Returns the config of the frontend workspace.
 *
 * @return {Object} the workspace configuration
 */
const getWorkspaceConfig = () => {
  if (!wsConfig) {
    const packageJsonPath = getWSPackageJson();
    const wsPath = path.dirname(packageJsonPath);
    const configPath = path.join(wsPath, DEFAULT_CONFIG_PATH);
    const envFile = path.join(configPath, "env.json");
    const certFile = path.join(configPath, "livereload.pem");
    const apiKeyFile = path.join(configPath, "apikey.txt");
    const themesPath = path.join(wsPath, DEFAULT_THEMES_PATH);
    const bricksPath = path.join(wsPath, DEFAULT_BRICKS_PATH);
    const targetPath = path.join(wsPath, DEFAULT_TARGET_PATH);

    wsConfig = {
      path: wsPath,
      pkgPath: packageJsonPath,
      configPath,
      envFile,
      certFile,
      apiKeyFile,
      themesPath,
      bricksPath,
      targetPath,
    };
  }
  return wsConfig;
};

/**
 * Gives the config of the theme the process has been started from.
 *
 * @return {Object} the theme configuration
 */
function getThemeConfig() {
  if (!themeConfig) {
    const wsConfig = getWorkspaceConfig();

    const cwd = process.cwd();
    const packageJsonPath = closestPackage.sync(cwd);

    const packageJson = require(packageJsonPath);
    // packageJson.theme as fallback for backward compatibility
    const themeConfigFromPackageJson =
      packageJson.coremedia || packageJson.theme;
    if (!themeConfigFromPackageJson) {
      throw new Error(
        `No theme config found in package.json '${packageJsonPath}'`
      );
    }
    if (
      !themeConfigFromPackageJson.name ||
      typeof themeConfigFromPackageJson.name !== "string"
    ) {
      throw new Error(
        `Invalid 'name' provided in theme configuration of package.json '${packageJsonPath}'`
      );
    }
    if (
      themeConfigFromPackageJson.targetPath &&
      typeof themeConfigFromPackageJson.targetPath !== "string"
    ) {
      throw new Error(
        `Invalid 'targetPath' provided in theme configuration of package.json '${packageJsonPath}'`
      );
    }

    const srcPath = path.resolve(path.dirname(packageJsonPath), "src");

    const targetPath = themeConfigFromPackageJson.targetPath
      ? path.resolve(
          path.dirname(packageJsonPath),
          themeConfigFromPackageJson.targetPath
        )
      : wsConfig.targetPath;

    const resourcesTargetPath = path.resolve(targetPath, "resources");

    const descriptorTargetPath = path.join(
      resourcesTargetPath,
      `THEME-METADATA/${themeConfigFromPackageJson.name}-theme.xml`
    );

    const themeTargetPath = path.join(
      resourcesTargetPath,
      "themes",
      themeConfigFromPackageJson.name
    );

    const templatesTargetPath = path.join(
      resourcesTargetPath,
      "WEB-INF/templates"
    );

    const brickTemplatesTargetPath = path.join(templatesTargetPath, "bricks");

    const themeTemplatesTargetPath = path.join(
      templatesTargetPath,
      themeConfigFromPackageJson.name
    );

    const themeTemplatesJarTargetPath = path.join(
      themeTargetPath,
      `templates/${themeConfigFromPackageJson.name}-templates.jar`
    );

    const brickTemplatesJarTargetPath = path.join(
      themeTargetPath,
      "templates/bricks-templates.jar"
    );

    const themeContentTargetPath = path.resolve(
      targetPath,
      "content/Themes",
      themeConfigFromPackageJson.name
    );

    const themeArchiveTargetPath = path.resolve(
      targetPath,
      `themes/${themeConfigFromPackageJson.name}-theme.zip`
    );

    const themeUpdateArchiveTargetPath = path.resolve(
      targetPath,
      `themes/${themeConfigFromPackageJson.name}-update.zip`
    );

    themeConfig = {
      name: themeConfigFromPackageJson.name,
      packageName: packageJson.name,
      version: themeConfigFromPackageJson.version,
      buildConfig: themeConfigFromPackageJson.buildConfig || {},
      path: cwd,
      srcPath,
      pkgPath: packageJsonPath,
      targetPath,
      resourcesTargetPath,
      descriptorTargetPath,
      themeTargetPath,
      templatesTargetPath,
      brickTemplatesTargetPath,
      themeTemplatesTargetPath,
      themeTemplatesJarTargetPath,
      brickTemplatesJarTargetPath,
      themeContentTargetPath,
      themeArchiveTargetPath,
      themeUpdateArchiveTargetPath,
    };
  }
  return themeConfig;
}

function getCoreMediaEntryFromPackageJson(nodeModule) {
  const packageJson = require(nodeModule.getPkgPath());
  return new CoreMediaEntry(packageJson.coremedia);
}

/**
 * @param nodeModule {NodeModule}
 * @returns {CoreMediaEntry}
 */
function getCoreMediaEntry(nodeModule) {
  const moduleName = nodeModule.getName();
  if (!(moduleName in cachedPackageJsonsByName)) {
    cachedPackageJsonsByName[moduleName] = getCoreMediaEntryFromPackageJson(
      nodeModule
    );
  }
  return cachedPackageJsonsByName[moduleName];
}

function getPackageType(nodeModule) {
  return getCoreMediaEntry(nodeModule).type;
}

function getSmartImportType(nodeModule) {
  return getCoreMediaEntry(nodeModule).smartImport;
}

/**
 * Returns an absolute path to the initialization script of the package if defined.
 *
 * @param nodeModule The module
 * @returns {String} if an JavaScript for initialization is given, return absolute path, otherwise NULL
 */
function getInitJs(nodeModule) {
  const init = getCoreMediaEntry(nodeModule).init;
  if (!init) {
    return null;
  }
  const pkgPath = nodeModule.getPkgPath();
  const pkgDir = path.dirname(pkgPath);
  return path.resolve(pkgDir, init);
}

function absoluteRequire(require, context) {
  // check if relative path is provided
  if (require.length > 0 && require[0] === ".") {
    return path.join(context, require);
  }
  // otherwise just return the require statement as module requires are already absolute
  return require;
}

/**
 * Parses a {@link Shim} from a given config {@link Object}.
 * @param {String} target the target
 * @param {Object} config the config to parse from
 * @param context the context to resolved from
 */
function parseShim(target, config, context) {
  const shim = new Shim(absoluteRequire(target, context));

  Object.keys(config.imports || {}).forEach(name => {
    const module = config.imports[name];
    shim.addImport(name, absoluteRequire(module, context));
  });

  Object.keys(config.exports || {}).forEach(name => {
    shim.addExport(name, config.exports[name]);
  });

  return shim;
}

/**
 * Returns the defined shims of a given module.
 *
 * @param {NodeModule} nodeModule The module
 * @returns {Array<Shim>} the defined shims
 */
function getShims(nodeModule) {
  const shimConfigByTarget = getCoreMediaEntry(nodeModule).shim;
  return Object.keys(shimConfigByTarget).map(target =>
    parseShim(
      target,
      shimConfigByTarget[target],
      path.dirname(nodeModule.getPkgPath())
    )
  );
}

/**
 * Checks if the given node module is a brick module
 * @param nodeModule {NodeModule} the module
 * @return {boolean}
 */
function isBrickModule(nodeModule) {
  return getPackageType(nodeModule) === "brick";
}

/**
 * Checks if the given node module is a lib module
 * @param nodeModule {NodeModule} the module
 * @return {boolean}
 */
function isLibraryModule(nodeModule) {
  return getPackageType(nodeModule) === "lib";
}

/**
 * Checks if the given node module is meant to be smart imported from this variant. If variant is set to null it will
 * ignore the variant check.
 *
 * @param {String} variant the variant to check (if null variant check will be ignored, defaults to {@link DEFAULT_VARIANT}).
 * @returns {function(NodeModule=): boolean}
 */
function getIsSmartImportModuleFor(variant = DEFAULT_VARIANT) {
  return nodeModule =>
    (isBrickModule(nodeModule) || isLibraryModule(nodeModule)) &&
    (!variant || getSmartImportType(nodeModule).includes(variant));
}

/**
 * Finds the installation path of the given module name. Optionally a file path can be provided to indicate where to
 * start look from.
 *
 * @param moduleName
 * @param relativeFrom
 * @throws Error in case the installation path could not be found
 */
function getInstallationPath(moduleName, relativeFrom) {
  let nodeModulePaths = process.mainModule.paths;
  if (relativeFrom) {
    nodeModulePaths = [
      path.join(
        path.dirname(closestPackage.sync(relativeFrom)),
        "node_modules"
      ),
    ].concat(nodeModulePaths);
  }
  try {
    return getInstalledPathSync(moduleName, { paths: nodeModulePaths });
  } catch (e) {
    // could not find module
    throw new Error(
      `Could not find installation folder for module '${moduleName}', searched in ${nodeModulePaths}`
    );
  }
}

/**
 * Collects all available bricks in the frontend workspace.
 *
 * @returns {Object} an object containing the name of the package as key and the version as value
 */
function getAvailableBricks() {
  const wsPatterns = require(wsConfig.pkgPath).workspaces || [];
  const wsDirectories = wsPatterns
    .map(wsPattern =>
      glob.sync(wsPattern, {
        cwd: wsConfig.path,
      })
    )
    .reduce((all, newValue) => all.concat(newValue), []);

  const packageJsonPaths = wsDirectories
    .map(directory => path.join(wsConfig.path, directory, "package.json"))
    .filter(fs.existsSync)
    .filter(packageJsonPath => {
      const packageJson = require(packageJsonPath);
      return packageJson.coremedia && packageJson.coremedia.type === "brick";
    });

  return packageJsonPaths
    .map(packageJsonPath => {
      const packageJson = require(packageJsonPath);
      return {
        [packageJson.name]: `^${packageJson.version}`,
      };
    })
    .reduce(
      (aggregator, newValue) => ({
        ...aggregator,
        ...newValue,
      }),
      {}
    );
}

/**
 * Return config for monitor script.
 * @returns {Object}
 * @private
 */
const getMonitorConfig = () => {
  const monitorConfig = {
    livereload: {},
  };
  const defaultMonitorConfig = {
    target: "remote",
    livereload: {
      host: "localhost",
      port: 35729,
    },
  };

  let customMonitorConfig;
  try {
    customMonitorConfig = getEnv().monitor;
  } catch (e) {
    // no custom monitor config, use default config
  }

  if (typeof customMonitorConfig !== "object") {
    Object.assign(monitorConfig, defaultMonitorConfig);
  } else {
    // Check property target
    if (typeof customMonitorConfig.target !== "string") {
      Object.assign(monitorConfig, {
        target: defaultMonitorConfig.target,
      });
    } else if (
      customMonitorConfig.target !== "local" &&
      customMonitorConfig.target !== "remote"
    ) {
      throw new Error(
        'Property monitor.target must be either "remote" or "local". Please check env.json in config directory in root of frontend workspace.'
      );
    } else {
      Object.assign(monitorConfig, {
        target: customMonitorConfig.target,
      });
    }

    // Check property livereload
    if (typeof customMonitorConfig.livereload !== "object") {
      Object.assign(monitorConfig, {
        livereload: defaultMonitorConfig.livereload,
      });
    } else {
      // Check property livereload.host
      if (typeof customMonitorConfig.livereload.host !== "string") {
        Object.assign(monitorConfig.livereload, {
          host: defaultMonitorConfig.livereload.host,
        });
      } else {
        Object.assign(monitorConfig.livereload, {
          host: customMonitorConfig.livereload.host,
        });
      }

      // Check property livereload.port
      if (typeof customMonitorConfig.livereload.port !== "number") {
        Object.assign(monitorConfig.livereload, {
          port: defaultMonitorConfig.livereload.port,
        });
      } else if (
        customMonitorConfig.livereload.port < 1024 ||
        customMonitorConfig.livereload.port > 49151
      ) {
        throw new Error(
          "Property monitor.livereload.port must be a number between 1024 and 49151. Please check env.json in config directory in root of frontend workspace."
        );
      } else {
        Object.assign(monitorConfig.livereload, {
          port: customMonitorConfig.livereload.port,
        });
      }
    }

    // Add certificate to property livereload
    Object.assign(monitorConfig.livereload, {
      key: defaultMonitorConfig.livereload.key,
      cert: defaultMonitorConfig.livereload.cert,
    });
  }
  return monitorConfig;
};

/**
 * Write variables into env.json
 * @param {Object} vars
 */
const createEnvFile = vars => {
  try {
    const wsConfig = getWorkspaceConfig();
    let env = {};
    if (!fs.existsSync(wsConfig.configPath)) {
      fs.mkdirSync(wsConfig.configPath);
    } else if (fs.existsSync(wsConfig.envFile)) {
      env = JSON.parse(fs.readFileSync(wsConfig.envFile, "utf8"));
    }
    env = Object.assign(env, vars);
    const data = JSON.stringify(env, null, 2);
    fs.writeFileSync(wsConfig.envFile, data, {
      encoding: "utf8",
      mode: 0o600,
    });
  } catch (e) {
    throw new ConfigFileError(
      `An error occured while trying to store the environment variables: ${
        e.message
      }`
    );
  }
};

/**
 * Returns content of env.json parsed as JSON
 * @return {Object}
 */
const getEnv = () => {
  const wsConfig = getWorkspaceConfig();
  if (!fs.existsSync(wsConfig.envFile)) {
    throw new ConfigFileError("No environment file found. Please login.");
  }
  try {
    return JSON.parse(fs.readFileSync(wsConfig.envFile, "utf8"));
  } catch (e) {
    throw new ConfigFileError("The environment file couldn´t be read.");
  }
};

/**
 * Checks, if certificate for the LiveReload server is expired
 * @returns {Boolean}
 * @private
 */
const isCertExpired = certFile => {
  const certStat = new Date(fs.statSync(certFile).mtime);
  const certExpiration = 1000 * 60 * 60 * 24 * 30;
  const now = new Date();

  // if certificate is older than 30 days, delete it to create a new one.
  if (certStat < now - certExpiration) {
    fs.unlinkSync(certFile);
    return true;
  }
  return false;
};

/**
 * Creates certificate for the LiveReload server
 * @private
 */
const createCertFile = (configPath, certFile) => {
  try {
    const attributes = [{ name: "commonName", value: "localhost" }];
    const pems = selfsigned.generate(attributes, {
      algorithm: "sha256",
      days: 30,
      keySize: 2048,
      extensions: [
        { name: "subjectAltName", altNames: [{ type: 6, value: "localhost" }] },
      ],
    });

    if (!fs.existsSync(configPath)) {
      fs.mkdirSync(configPath);
    }
    fs.writeFileSync(certFile, pems.private + pems.cert, {
      encoding: "utf-8",
    });
  } catch (e) {
    log.error(
      `An error occured while trying to store the certificate for the LiveReload server: ${
        e.message
      }`
    );
  }
};

/**
 * Return certificate for LiveReload server
 * @returns {string}
 * @private
 */
const getCert = () => {
  const wsConfig = getWorkspaceConfig();

  if (!fs.existsSync(wsConfig.certFile) || isCertExpired(wsConfig.certFile)) {
    createCertFile(wsConfig.configPath, wsConfig.certFile);
  }
  let cert;
  try {
    cert = fs.readFileSync(wsConfig.certFile);
  } catch (error) {
    // cert file does not exist
  }
  return cert;
};

/**
 * Write file apikey.txt
 * @param {string} apiKey
 */
const createApiKeyFile = apiKey => {
  try {
    const wsConfig = getWorkspaceConfig();

    if (!fs.existsSync(wsConfig.configPath)) {
      fs.mkdirSync(wsConfig.configPath);
    }
    fs.writeFileSync(wsConfig.apiKeyFile, apiKey, {
      encoding: "utf8",
      mode: 0o600,
    });
  } catch (e) {
    throw new ApiKeyFileError(
      `An error occured while trying to store the API key: ${e.message}`
    );
  }
};

/**
 * remove file apikey.txt
 */
const removeApiKeyFile = () => {
  try {
    const wsConfig = getWorkspaceConfig();

    fs.unlinkSync(wsConfig.apiKeyFile);
  } catch (e) {
    // apikey.txt couldn´t be deleted
  }
};

/**
 * Returns content of apikey.txt
 * @return {string} apiKey
 */
const getApiKey = () => {
  const wsConfig = getWorkspaceConfig();

  if (!fs.existsSync(wsConfig.apiKeyFile)) {
    throw new ApiKeyFileError("No API key found. Please login.");
  }
  const apiKey = fs.readFileSync(wsConfig.apiKeyFile, "utf8");
  if (typeof apiKey !== "string" || apiKey.length === 0) {
    throw new ApiKeyFileError("No API key found. Please login.");
  }
  return apiKey;
};

module.exports = {
  DEFAULT_VARIANT,
  getWorkspaceConfig,
  getThemeConfig,
  isBrickModule,
  getInitJs,
  getShims,
  getIsSmartImportModuleFor,
  getInstallationPath,
  getAvailableBricks,
  getMonitorConfig,
  createEnvFile,
  getEnv,
  getCert,
  createApiKeyFile,
  removeApiKeyFile,
  getApiKey,
};
