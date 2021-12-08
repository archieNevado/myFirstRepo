const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__validators-studio",
    namespace: "com.coremedia.blueprint.validators.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.validators.studio.ValidatorsStudioPlugin",
        name: "Validators Studio Plugin",
      },
    ],
  },
});
