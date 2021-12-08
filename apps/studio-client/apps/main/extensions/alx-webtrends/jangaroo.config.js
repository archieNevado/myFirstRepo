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
          name: "Webtrends",
          url: "",
          cmCategory: "External Services",
          icons: [
            {
              src: "packages/com.coremedia.blueprint__alx-webtrends-studio-plugin/appIcons/webtrends_24.svg",
              sizes: "24x24",
              type: "image/svg",
            },
            {
              src: "packages/com.coremedia.blueprint__alx-webtrends-studio-plugin/appIcons/webtrends_192.png",
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
});
