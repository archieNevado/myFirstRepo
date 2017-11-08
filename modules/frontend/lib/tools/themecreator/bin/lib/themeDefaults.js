'use strict';

module.exports = {
  packageJson: function (name, mainJs) {
    return `{
  "name": "@coremedia/${name}",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "build": "grunt build",
    "production": "grunt build",
    "start": "grunt monitor"
  },
  "__comment__dependencies__": {
    "__comment__": "List of all brick dependencies. In order to add a dependency, move the entry to 'dependencies'",
    "@coremedia/brick-bootstrap": "file:../../lib/bricks/bootstrap",
    "@coremedia/brick-cta": "file:../../lib/bricks/cta",
    "@coremedia/brick-default-teaser": "file:../../lib/bricks/default-teaser",
    "@coremedia/brick-download-portal": "file:../../lib/bricks/download-portal",
    "@coremedia/brick-dynamic-include": "file:../../lib/bricks/dynamic-include",
    "@coremedia/brick-elastic-social": "file:../../lib/bricks/elastic-social",
    "@coremedia/brick-fragment-scenario": "file:../../lib/bricks/fragment-scenario",
    "@coremedia/brick-generic-templates": "file:../../lib/bricks/generic-templates",
    "@coremedia/brick-hero-teaser": "file:../../lib/bricks/hero-teaser",
    "@coremedia/brick-image-maps": "file:../../lib/bricks/image-maps",
    "@coremedia/brick-livecontext": "file:../../lib/bricks/livecontext",
    "@coremedia/brick-pdp-augmentation": "file:../../lib/bricks/pdp-augmentation",
    "@coremedia/brick-preview": "file:../../lib/bricks/preview",
    "@coremedia/brick-quick-info": "file:../../lib/bricks/quick-info",
    "@coremedia/brick-responsive-images": "file:../../lib/bricks/responsive-images",
    "@coremedia/brick-shoppable-video": "file:../../lib/bricks/shoppable-video",
    "@coremedia/brick-video": "file:../../lib/bricks/video",
    "@coremedia/js-logger": "file:../../lib/js/logger",
    "@coremedia/sass-utils": "file:../../lib/sass/utils",
    "jquery": "3.2.1"
  },
  "dependencies": {
    "grunt": "1.0.1"
  },
  "main": "${mainJs}",
  "theme": {
    "name": "${name}"
  },
  "browserslist": [
    "last 1 Chrome version",
    "last 1 Firefox version",
    "last 1 Edge version",
    "Explorer >= 11"
  ]
}
`;
  },
  gruntfileJs: function () {
    return `'use strict';
    
const themeutils = require('@coremedia/themeutils');

module.exports = function (grunt) {

  // load CoreMedia initialization
  themeutils(grunt);

  // --- theme tasks ---------------------------------------------------------------------------------------------------

  // use task "monitor" for development. will be created by @coremedia/themeutils

  // build the theme. theme will be stored as zip file in <%= themeConfig.targetPath %>/themes
  grunt.registerTask('build', ['clean', 'copy', 'sync', 'webpack', 'compress']);

  // Default task = build
  grunt.registerTask('default', ['build']);
};
`;
  },
  themedescriptorXml: function (name) {
    return `<?xml version="1.0" encoding="UTF-8"?>
<themeDefinition modelVersion="1">

  <name>${name}</name>
  <!-- <description>a short description</description> -->
  <!-- <thumbnail>img/theme-preview.jpg</thumbnail> -->

  <javaScriptLibraries>
    <!-- add thirdparty js files that are not bundled via webpack here -->
    <!-- <javaScript>vendor/some-vendor-script.js</javaScript> -->
  </javaScriptLibraries>

  <javaScripts>
    <!-- add own js files that are not bundled via webpack here -->
    <!-- <javaScript>js/your-code.js</javaScript> -->
    <javaScript disableCompress="true">js/${name}.js</javaScript>
  </javaScripts>

  <styleSheets>
    <css disableCompress="true">css/${name}.css</css>
  </styleSheets>

  <templateSets>
    <templateSet>templates/${name}-templates.jar</templateSet>
    <templateSet>templates/bricks-templates.jar</templateSet>
  </templateSets>

  <resourceBundles>
    <!-- add brick resource bundles here -->
    <resourceBundle>l10n/${name}_en.properties</resourceBundle>
  </resourceBundles>

</themeDefinition>
`;
  },
  themeSass: function (name) {
    return `/*! Theme ${name} */

// ### VARIABLES ###

// Own variables (need to be loaded first, so default values can be overridden)
// @see http://sass-lang.com/documentation/file.SASS_REFERENCE.html#Variable_Defaults___default

//@import "variables/...";

// Dependency variables

//@import "~@coremedia/brick-bootstrap/src/sass/variables";
//@import "~@coremedia/brick-cta/src/sass/variables";
//@import "~@coremedia/brick-default-teaser/src/sass/variables";
//@import "~@coremedia/brick-download-portal/src/sass/variables";
//@import "~@coremedia/brick-dynamic-include/src/sass/variables";
//@import "~@coremedia/brick-elastic-social/src/sass/variables";
//@import "~@coremedia/brick-fragment-scenario/src/sass/variables";
//@import "~@coremedia/brick-generic-templates/src/sass/variables";
//@import "~@coremedia/brick-hero-teaser/src/sass/variables";
//@import "~@coremedia/brick-image-maps/src/sass/variables";
//@import "~@coremedia/brick-livecontext/src/sass/variables";
//@import "~@coremedia/brick-pdp-augmentation/src/sass/variables";
//@import "~@coremedia/brick-preview/src/sass/variables";
//@import "~@coremedia/brick-quick-info/src/sass/variables";
//@import "~@coremedia/brick-responsive-images/src/sass/variables";
//@import "~@coremedia/brick-shoppable-video/src/sass/variables";
//@import "~@coremedia/brick-video/src/sass/variables";
//@import "~@coremedia/js-logger/src/sass/variables";
//@import "~@coremedia/sass-utils/src/sass/variables";

// ### PARTIALS ###

// Dependency partials

//@import "~@coremedia/brick-bootstrap/src/sass/partials";
//@import "~@coremedia/brick-cta/src/sass/partials";
//@import "~@coremedia/brick-default-teaser/src/sass/partials";
//@import "~@coremedia/brick-download-portal/src/sass/partials";
//@import "~@coremedia/brick-dynamic-include/src/sass/partials";
//@import "~@coremedia/brick-elastic-social/src/sass/partials";
//@import "~@coremedia/brick-fragment-scenario/src/sass/partials";
//@import "~@coremedia/brick-generic-templates/src/sass/partials";
//@import "~@coremedia/brick-hero-teaser/src/sass/partials";
//@import "~@coremedia/brick-image-maps/src/sass/partials";
//@import "~@coremedia/brick-livecontext/src/sass/partials";
//@import "~@coremedia/brick-pdp-augmentation/src/sass/partials";
//@import "~@coremedia/brick-preview/src/sass/partials";
//@import "~@coremedia/brick-quick-info/src/sass/partials";
//@import "~@coremedia/brick-responsive-images/src/sass/partials";
//@import "~@coremedia/brick-shoppable-video/src/sass/partials";
//@import "~@coremedia/brick-video/src/sass/partials";
//@import "~@coremedia/js-logger/src/sass/partials";
//@import "~@coremedia/sass-utils/src/sass/partials";

// Own partials

//@import "partials/...";
`;
  },
  previewSass: function (name) {
    return `/*! Theme ${name}: Preview Styles */

// ### VARIABLES ###

// Own variables (need to be loaded first, so default values can be overridden)
// @see http://sass-lang.com/documentation/file.SASS_REFERENCE.html#Variable_Defaults___default

//@import "variables/...";

// Dependency variables

//@import "~@coremedia/brick-preview/src/sass/variables";
//@import "~@coremedia/sass-utils/src/variables";

// ### PARTIALS ###

// Dependency partials

//@import "~@coremedia/sass-utils/src/partials";
//@import "~@coremedia/brick-preview/src/sass/partials";

// Own partials

//@import "partials/preview";
`;
  },
  themeJsIndex: function (name) {
    return `/*! Theme ${name} */

// activate brick js functionality
//import "@coremedia/brick-bootstrap";
//import "@coremedia/brick-cta";
//import "@coremedia/brick-download-portal";
//import "@coremedia/brick-dynamic-include";
//import "@coremedia/brick-elastic-social";
//import "@coremedia/brick-fragment-scenario";
//import "@coremedia/brick-generic-templates";
//import "@coremedia/brick-image-maps";
//import "@coremedia/brick-livecontext";
//import "@coremedia/brick-pdp-augmentation";
//import "@coremedia/brick-preview";
//import "@coremedia/brick-quick-info";
//import "@coremedia/brick-responsive-images";
//import "@coremedia/brick-shoppable-video";
//import "@coremedia/brick-video";

import "./${name}.js";
`;
  },
  themeJs: function (name) {
    return `//import $ from "jquery";
//import logger from "@coremedia/js-logger";

// --- JQUERY DOCUMENT READY -------------------------------------------------------------------------------------------
//$(function () {
//  logger.log("Theme ${name} is used.");
//});
`;
  }
};
