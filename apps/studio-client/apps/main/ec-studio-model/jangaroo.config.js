const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__ec-studio-model",
    namespace: "com.coremedia.ecommerce.studio",
  },
  autoLoad: [
    "./src/init",
  ],
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
