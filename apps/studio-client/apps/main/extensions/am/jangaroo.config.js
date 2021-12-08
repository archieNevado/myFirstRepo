const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__am-studio",
    namespace: "com.coremedia.blueprint.assets.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.assets.studio.AMStudioPlugin",
        name: "Asset Management Extensions",
      },
    ],
  },
});
