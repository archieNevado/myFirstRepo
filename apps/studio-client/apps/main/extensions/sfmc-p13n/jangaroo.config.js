const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__sfmc-p13n-studio",
    namespace: "com.coremedia.blueprint.sfmc.p13n.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.sfmc.p13n.studio.SFMCP13NStudioPlugin",
        name: "SFMC P13N Extensions",
      },
    ],
  },
  autoLoad: [
    "./src/init",
  ],
  command: {
    build: {
      ignoreTypeErrors: true,
    },
  },
});
