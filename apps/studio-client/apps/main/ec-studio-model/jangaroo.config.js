const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  autoLoad: [
    "./src/init",
  ],
  command: {
    joounit: {
      testSuite: "./joounit/TestSuite",
      testExecutionTimeout: 120000,
    },
  },
  sencha: {
    name: "com.coremedia.blueprint__ec-studio-model",
    namespace: "com.coremedia.ecommerce.studio",
  },
});
