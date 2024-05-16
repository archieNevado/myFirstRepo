const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__alx-google-studio-plugin",
    namespace: "com.coremedia.blueprint.studio.googleanalytics",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.googleanalytics.GoogleAnalyticsStudioPlugin",
        name: "GoogleAnalytics",
      },
    ],
  },
  appManifests: {
    en: {
      categories: [
        "External Services",
      ],
      cmServiceShortcuts: [
        {
          cmKey: "cmGoogleAnalytics",
          cmOrder: 10,
          name: "Google",
          url: "",
          cmCategory: "External Services",
          cmService: {
            name: "launchSubAppService",
            method: "launchSubApp",
          },
        },
      ],
    },
  },
  command: {
    joounit: {
      testSuite: "./joounit/TestSuite",
    },
  },
});
