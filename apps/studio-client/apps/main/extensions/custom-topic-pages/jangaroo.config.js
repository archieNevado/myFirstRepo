const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__custom-topic-pages-studio-plugin",
    namespace: "com.coremedia.blueprint.studio.topicpages",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.topicpages.TopicPagesStudioPlugin",
        name: "Topic Pages Editor",
      },
    ],
  },
  appManifests: {
    en: {
      categories: [
        "Content",
      ],
      cmServiceShortcuts: [
        {
          cmKey: "cmTopicPages",
          cmOrder: 40,
          cmCategory: "Content",
          name: "Topic Pages",
          url: "",
          cmAdministrative: true,
          cmGroups: ["global-manager", "taxonomy-manager", "topic-pages-manager", "developer"],
          cmService: {
            name: "launchSubAppService",
            method: "launchSubApp",
          },
        },
      ],
    },
  },
});
