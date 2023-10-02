const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__lc-studio",
    namespace: "com.coremedia.livecontext.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.livecontext.studio.LivecontextStudioPlugin",
        name: "Livecontext Extensions",
      },
      {
        mainClass: "com.coremedia.livecontext.studio.desktop.ClassicLCStudioPlugin",
        name: "ClassicLCStudioPlugin",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true,
    },
    joounit: {
      testSuite: "./joounit/TestSuite",
      testExecutionTimeout: 120000,
    },
  },
});
