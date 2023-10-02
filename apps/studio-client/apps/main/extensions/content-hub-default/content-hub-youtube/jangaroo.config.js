const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint.content-hub__content-hub-youtube",
    namespace: "com.coremedia.blueprint.studio.contenthub.youtube",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.contenthub.youtube.ContentHubStudioYoutubePlugin",
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
