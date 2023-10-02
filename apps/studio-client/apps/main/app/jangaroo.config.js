const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "app-overlay",
  command: {
    run: {
      proxyTargetUri: "http://localhost:41080",
      proxyPathSpec: "/rest/",
    },
  },
});
