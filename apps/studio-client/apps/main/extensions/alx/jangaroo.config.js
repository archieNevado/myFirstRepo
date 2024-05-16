const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__alx-studio-plugin",
    namespace: "com.coremedia.blueprint.studio.analytics",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.analytics.AnalyticsStudioPlugin",
        name: "Analytics",
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
