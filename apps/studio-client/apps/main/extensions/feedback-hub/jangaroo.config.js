const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__feedback-hub-imagga-studio",
    namespace: "com.coremedia.blueprint.studio.feedbackhub.imagga",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.feedbackhub.imagga.ImaggaFeedbackHubStudioPlugin",
        name: "FeedbackHub for Imagga",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true,
    },
  },
});
