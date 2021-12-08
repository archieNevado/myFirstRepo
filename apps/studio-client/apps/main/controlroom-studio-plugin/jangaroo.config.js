const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__controlroom-studio-plugin",
    namespace: "com.coremedia.blueprint.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.ControlRoomStudioPlugin",
        name: "Control Room",
      },
    ],
  },
});
