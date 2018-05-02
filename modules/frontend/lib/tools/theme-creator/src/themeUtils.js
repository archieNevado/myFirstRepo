"use strict";

const fs = require("fs");
const path = require("path");
const mkdirp = require("mkdirp");
const semver = require("semver");

const themeData = require("./themeData");

/**
 * converts the theme name to match restrictions.
 * returns an empty string, if given name was not valid.
 * @param  {string} themeName
 * @return {string}
 */
const convertThemeName = themeName => {
  if (typeof themeName !== "string") {
    return "";
  }
  return themeName
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9-]/g, "");
};

/**
 * verifies, if the given theme path is already in use
 * @param  {string}  themePath
 * @return {Boolean}
 */
const isThemeNameInUse = themePath => fs.existsSync(themePath);

/**
 * Converts a given version to a caret version if it is valid according to semver.valid
 *
 * @param version the version to change to a caret version
 * @returns {string} if a valid version was given the caret version otherwise the given version
 */
const convertVersionToCaretVersion = (version) => {
  if (!version || !semver.valid(version)) {
    return version;
  }
  version = semver.clean(version);
  return version.indexOf("^") === 0 ? version : `^${version}`;
};

/**
 * Object containing a mapping of dependencyName => dependencyVersion
 * @param dependencies
 */
const convertDependencyVersionsToCaretVersions = (dependencies) => {
  return Object.keys(dependencies).reduce(function(aggregator, newValue) {
    aggregator[newValue] = convertVersionToCaretVersion(dependencies[newValue]);
    return aggregator;
  }, {});
};

/**
 * creates folder structure of a new theme
 * @param  {object} wsConfig the workspace configuration
 * @param  {string}  themePath
 * @param  {cmLogger} log cm-logger instance
 */
const createFolderStructure = (wsConfig, themePath, log) => {
  const directories = [
    path.join(themePath, "src", "js"),
    path.join(themePath, "src", "sass"),
    path.join(themePath, "src", "img"),
    path.join(themePath, "src", "fonts"),
    path.join(themePath, "src", "l10n"),
    path.join(
      themePath,
      "src",
      "templates",
      "com.coremedia.blueprint.common.contentbeans"
    ),
  ];
  directories.forEach(dir => {
    log.debug(
      `Create directory "themes/${path.relative(wsConfig.themesPath, dir)}"`
    );
    mkdirp.sync(dir);
  });
};

/**
 * creates files of a new theme
 * @param  {string}  themePath
 * @param  {string}  themeName
 * @param  {Object} bricksToActivate
 * @param  {Object} bricksToCommentOut
 * @param  {cmLogger} log cm-logger instance
 */
const createFiles = (themePath, themeName, bricksToActivate, bricksToCommentOut, log) => {
  const files = [
    {
      path: "package.json",
      data: themeData.initPackageJson(themeName, "src/js/index.js", convertDependencyVersionsToCaretVersions(bricksToActivate), convertDependencyVersionsToCaretVersions(bricksToCommentOut)),
    },
    {
      path: "webpack.config.js",
      data: themeData.initWebpackConfigJs(),
    },
    {
      path: `${themeName}-theme.xml`,
      data: themeData.initThemedescriptorXml(themeName, Object.keys(bricksToActivate).length > 0),
    },
    {
      path: "src/js/index.js",
      data: themeData.initThemeIndexJs(themeName),
    },
    {
      path: `src/js/${themeName}.js`,
      data: themeData.initThemeJs(themeName),
    },
    {
      path: "src/js/preview.js",
      data: themeData.initPreviewJs(themeName),
    },
    {
      path: `src/sass/${themeName}.scss`,
      data: themeData.initThemeSass(themeName),
    },
    {
      path: "src/sass/preview.scss",
      data: themeData.initPreviewSass(themeName),
    },
    {
      path: `src/l10n/${themeData.titleCase(themeName)}_en.properties`,
      data: "",
    },
  ];
  files.forEach(file => {
    log.debug(`Create file "${file.path}"`);
    fs.writeFileSync(path.join(themePath, file.path), file.data);
  });
};

/**
 * creates folder structure and files of a new theme
 * @param  {Object}  wsConfig
 * @param  {string}  themePath
 * @param  {string}  themeName
 * @param  {Object} bricksToActivate
 * @param  {Object} bricksToCommentOut
 * @param  {cmLogger} log cm-logger instance
 */
const createTheme = (wsConfig, themePath, themeName, bricksToActivate, bricksToCommentOut, log) => {
  log.debug(`Creating theme "${path.basename(themePath)}"...`);
  createFolderStructure(wsConfig, themePath, log);
  createFiles(themePath, themeName, bricksToActivate, bricksToCommentOut, log);
};

module.exports = {
  convertThemeName,
  isThemeNameInUse,
  createFolderStructure,
  createFiles,
  createTheme,
};
