const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "apps",
  appPaths: {
    "@coremedia-blueprint/studio-client.main.app": "",
  },
  command: {
    run: {
      proxyTargetUri: "http://localhost:41080",
      proxyPathSpec: "/rest/",
    },
  },
});
