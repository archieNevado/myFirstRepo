const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint.content-hub__content-hub-rss",
    namespace: "com.coremedia.blueprint.studio.contenthub.rss",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.contenthub.rss.ContentHubStudioRssPlugin",
        name: "Content Hub",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true,
    },
  },
});
