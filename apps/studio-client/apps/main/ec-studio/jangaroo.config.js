const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__ec-studio",
    namespace: "com.coremedia.ecommerce.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.ecommerce.studio.ECommerceStudioPlugin",
        name: "ECommerce Extensions",
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
