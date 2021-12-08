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
          name: "Google",
          url: "",
          cmCategory: "External Services",
          icons: [
            {
              src: "packages/com.coremedia.blueprint__alx-google-studio-plugin/appIcons/analytics_24.svg",
              sizes: "24x24",
              type: "image/svg",
            },
            {
              src: "packages/com.coremedia.blueprint__alx-google-studio-plugin/appIcons/analytics_192.png",
              sizes: "192x192",
              type: "image/png",
            },
          ],
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
