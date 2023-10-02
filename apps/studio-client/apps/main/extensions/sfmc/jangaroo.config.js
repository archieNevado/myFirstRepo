const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__sfmc-studio",
    namespace: "com.coremedia.blueprint.sfmc.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.sfmc.studio.SFMCStudioPlugin",
        name: "SFMC Extensions",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true,
    },
  },
});
