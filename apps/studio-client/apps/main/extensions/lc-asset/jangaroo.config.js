const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__lc-asset-studio",
    namespace: "com.coremedia.livecontext.asset.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.livecontext.asset.studio.LivecontextAssetStudioPlugin",
        name: "LiveContext Product Asset Management",
      },
    ],
  },
  command: {
    joounit: {
      testSuite: "./joounit/TestSuite",
      testExecutionTimeout: 120000,
    },
  },
});
