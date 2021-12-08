const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "app-overlay",
  appName: "studio-client.main",
  additionalPackagesDirs: [
    "./build/additional-packages",
  ],
  command: {
    run: {
      proxyTargetUri: "http://localhost:41080",
      proxyPathSpec: "/rest/",
    },
  },
  sencha: {
    name: "com.coremedia.blueprint__studio-app",
  },
});
