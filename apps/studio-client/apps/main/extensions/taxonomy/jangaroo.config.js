const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__taxonomy-studio",
    namespace: "com.coremedia.blueprint.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.TaxonomyStudioPlugin",
        name: "Taxonomy",
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
          cmKey: "cmTaxonomy",
          cmOrder: 30,
          cmCategory: "Content",
          name: "Tags",
          url: "",
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
