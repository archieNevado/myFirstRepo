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
        "Taxonomy Manager",
      ],
      cmServiceShortcuts: [
        {
          cmKey: "cmTopicPages",
          cmCategory: "Taxonomy Manager",
          name: "Topic Pages",
          url: "",
          icons: [
            {
              src: "packages/com.coremedia.blueprint__custom-topic-pages-studio-plugin/appIcons/taxonomy_24.svg",
              sizes: "24x24",
              type: "image/svg",
            },
            {
              src: "packages/com.coremedia.blueprint__custom-topic-pages-studio-plugin/appIcons/taxonomy_192.png",
              sizes: "192x192",
              type: "image/png",
            },
          ],
          cmAdministrative: true,
          cmService: {
            name: "launchSubAppService",
            method: "launchSubApp",
          },
        },
      ],
    },
  },
});
