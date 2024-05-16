const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__create-from-template-studio-plugin",
    namespace: "com.coremedia.blueprint.studio.template",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.template.CreateFromTemplateStudioPlugin",
        name: "Page Template Studio Plugin",
      },
    ],
  },
});
