const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__alx-webtrends-studio-plugin",
    namespace: "com.coremedia.blueprint.studio.webtrends",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.webtrends.WebtrendsStudioPlugin",
        name: "Webtrends Analytics Integration",
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
          cmKey: "cmWebtrendsAnalytics",
          cmOrder: 20,
          name: "Webtrends",
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
});
