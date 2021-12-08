const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__p13n-studio",
    namespace: "com.coremedia.blueprint.personalization.editorplugin",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.personalization.editorplugin.P13NStudioPlugin",
        name: "Personalization",
      },
    ],
  },
});
