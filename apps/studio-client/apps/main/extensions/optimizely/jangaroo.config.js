const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__optimizely-studio",
    namespace: "com.coremedia.blueprint.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.OptimizelyStudioPlugin",
        name: "Optimizely Plugin",
      },
    ],
  },
  appManifests: {
    en: {
      cmServiceShortcuts: [
        {
          cmKey: "cmOptimizelyAnalytics",
          name: "Optimizely",
          url: "",
          cmCategory: "External",
          icons: [
            {
              src: "packages/com.coremedia.blueprint__optimizely-studio/appIcons/ab-testing-tool_24.svg",
              sizes: "24x24",
              type: "image/svg",
            },
            {
              src: "packages/com.coremedia.blueprint__optimizely-studio/appIcons/ab-testing-tool_192.png",
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
    build: {
      ignoreTypeErrors: true,
    },
  },
});
