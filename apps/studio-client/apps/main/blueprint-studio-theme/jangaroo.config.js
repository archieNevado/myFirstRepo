const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  theme: "@coremedia/studio-client.ext.studio-theme",
  sencha: {
    name: "com.coremedia.blueprint__blueprint-studio-theme",
    type: "theme",
    namespace: "",
  },
});
