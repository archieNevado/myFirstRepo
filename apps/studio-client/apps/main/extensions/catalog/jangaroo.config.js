const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__catalog-studio-plugin",
    namespace: "com.coremedia.catalog.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.catalog.studio.CatalogStudioPlugin",
        name: "Catalog Extension",
      },
    ],
  },
});
