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
        "Taxonomy Manager",
      ],
      cmServiceShortcuts: [
        {
          cmKey: "cmTaxonomy",
          cmCategory: "Taxonomy Manager",
          name: "Tags",
          url: "",
          icons: [
            {
              src: "packages/com.coremedia.blueprint__taxonomy-studio/appIcons/taxonomy_24.svg",
              sizes: "24x24",
              type: "image/svg",
            },
            {
              src: "packages/com.coremedia.blueprint__taxonomy-studio/appIcons/taxonomy_192.png",
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
