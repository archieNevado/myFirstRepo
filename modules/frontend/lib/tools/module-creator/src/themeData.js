"use strict";

const sharedData = require("./sharedData");

/**
 * returns content for package.json
 * @param  {string} themeName the name of the theme
 * @param  {string} mainJSFile relative path to the main JavaScript file of the theme
 * @param  {Object} bricksToActivate
 * @param  {Object} bricksToCommentOut
 * @return {string}
 */
const initPackageJson = (themeName, mainJSFile, bricksToActivate, bricksToCommentOut) => {
  const content = {
    name: `@coremedia/${themeName}-theme`,
    version: "1.0.0",
    private: true,
    scripts: {
      build: "webpack",
      start: "cm monitor",
      "prettier": "prettier \"**/*\" --write",
      "theme-importer": "cm theme-importer",
    },
    __comment__dependencies__: {
      __comment__:
        "List of all brick dependencies. In order to add a dependency, move the entry to 'dependencies'",
      ...bricksToCommentOut,
      "@coremedia/js-logger": "^1.0.0",
      "@coremedia/sass-utils": "^1.0.0",
      jquery: "3.2.1",
    },
    dependencies: {
      ...bricksToActivate,
      "@coremedia/cm-cli": "^2.0.0",
      "@coremedia/theme-utils": "^3.0.0",
      webpack: "3.10.0",
    },
    devDependencies: {
      "prettier": "1.11.1"
    },
    main: mainJSFile,
    coremedia: {
      type: "theme",
      name: themeName,
    },
    browserslist: [
      "last 1 Chrome version",
      "last 1 Firefox version",
      "last 1 Edge version",
      "Explorer >= 11",
    ],
  };
  return JSON.stringify(content, null, 2);
};

/**
 * returns content for webpack.config.js
 * @return {string}
 */
const initWebpackConfigJs = () => `const webpackTheme = require("@coremedia/theme-utils/webpack.config.js");
module.exports = webpackTheme;
`;

/**
 * returns content for theme descriptor
 * @param  {string} themeName
 * @param  {boolean} usingBricks
 * @return {string}
 */
const initThemedescriptorXml = (themeName, usingBricks) => `<?xml version="1.0" encoding="UTF-8"?>
<themeDefinition modelVersion="1">
<name>${themeName}</name>
<!-- <description>a short description</description> -->
<!-- <thumbnail>img/theme-preview.jpg</thumbnail> -->
<javaScriptLibraries>
  <!-- add thirdparty js files that are not bundled via webpack here -->
  <!-- <javaScript>vendor/some-vendor-script.js</javaScript> -->
</javaScriptLibraries>
<javaScripts>
  <!-- add own js files that are not bundled via webpack here -->
  <!-- <javaScript>js/your-code.js</javaScript> -->
  <javaScript disableCompress="true">js/${themeName}.js</javaScript>
</javaScripts>
<styleSheets>
  <css disableCompress="true">css/${themeName}.css</css>
</styleSheets>
<templateSets>
  <templateSet>templates/${themeName}-templates.jar</templateSet>
  <!-- aggregated templates of all bricks${!usingBricks ? ", activate this if you are using bricks" : ""} -->
  ${!usingBricks ? "<!-- " : ""}<templateSet>templates/bricks-templates.jar</templateSet>${!usingBricks ? " -->" : ""}
</templateSets>
<resourceBundles>
  <!-- add theme resource bundles here -->
  <resourceBundle>l10n/${sharedData.titleCase(themeName)}_en.properties</resourceBundle>
  <!-- merged resource bundles of all bricks${!usingBricks ? ", activate this if you are using bricks" : ""} -->
  ${!usingBricks ? "<!-- " : ""}<resourceBundle>l10n/Bricks_en.properties</resourceBundle>${!usingBricks ? " -->" : ""}
</resourceBundles>
</themeDefinition>
`;

/**
 * returns content for <themeName>.sass
 * @param  {string} themeName
 * @return {string}
 */
const initThemeSass = themeName => `/*! Theme ${themeName} */
// ### VARIABLES ###

// Own variables (need to be loaded first, so default values can be overridden)
// @see http://sass-lang.com/documentation/file.SASS_REFERENCE.html#Variable_Defaults___default

//@import "variables/...";

// Dependency variables

@import "?smart-import-variables";

// ... add third-party dependencies here (after smart-import-variables)

// ### PARTIALS ###

// Dependency partials

// ... add third-party dependencies here (before smart-import-partials)

@import "?smart-import-partials";

// Own partials

//@import "partials/...";
`;

/**
 * returns content for preview.sass
 * @param  {string} themeName
 * @return {string}
 */
const initPreviewSass = themeName => `/*! Theme ${themeName}: Preview Styles */
// ### VARIABLES ###

// Own variables (need to be loaded first, so default values can be overridden)
// @see http://sass-lang.com/documentation/file.SASS_REFERENCE.html#Variable_Defaults___default

//@import "variables/...";

// Dependency variables

@import "?smart-import-variables";

// ... add third-party dependencies here (after smart-import-variables)

// ### PARTIALS ###

// Dependency partials

// ... add third-party dependencies here (before smart-import-partials)

@import "?smart-import-partials";

// Own partials

//@import "partials/...";
`;

/**
 * returns content for index.js
 * @param  {string} themeName
 * @return {string}
 */
const initThemeIndexJs = themeName => `/*! Theme ${themeName} */
import "./${themeName}.js";
`;

/**
 * returns content for .prettierignore
 * @return {string}
 */
const initThemePrettierignore = () => `/*
/*
!/src
/src/*
!/src/js
`;

/**
 * returns content for <themeName>.js
 * @param  {string} themeName
 * @return {string}
 */
const initThemeJs = themeName => `//import $ from "jquery";
//import logger from "@coremedia/js-logger";
// --- JQUERY DOCUMENT READY -------------------------------------------------------------------------------------------
//$(function () {
//  logger.log("Theme ${themeName} is used.");
//});
`;

/**
 * returns content for preview.js
 * @param  {string} themeName
 * @return {string}
 */
const initPreviewJs = themeName => `/*! Theme ${themeName}: Preview JS */
// add preview specific code here...
`;

module.exports = {
  initPackageJson,
  initThemePrettierignore,
  initWebpackConfigJs,
  initThemedescriptorXml,
  initThemeSass,
  initPreviewSass,
  initThemeIndexJs,
  initThemeJs,
  initPreviewJs,
};
