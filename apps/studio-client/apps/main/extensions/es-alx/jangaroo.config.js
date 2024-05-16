const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__es-alx-studio-plugin",
    namespace: "com.coremedia.blueprint.studio.esanalytics",
    css: [
      {
        path: "resources/joo/resources/css/morris.css",
      },
    ],
    js: [
      {
        path: "resources/joo/resources/js/raphael.js",
      },
      {
        path: "resources/joo/resources/js/jquery-3.5.0.min.js",
      },
      {
        path: "resources/joo/resources/js/morris.js",
      },
      {
        path: "resources/joo/resources/js/morris.esalx-plugin.js",
      },
    ],
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.esanalytics.EsAnalyticsStudioPlugin",
        name: "EsAnalytics",
      },
    ],
  },
  command: {
    joounit: {
      testSuite: "./joounit/TestSuite",
      testExecutionTimeout: 90000,
    },
  },
});
