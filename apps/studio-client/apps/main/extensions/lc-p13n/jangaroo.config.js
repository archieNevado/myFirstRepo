const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__lc-p13n-studio",
    namespace: "com.coremedia.livecontext.p13n.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.livecontext.p13n.studio.LivecontextP13NStudioPlugin",
        name: "Livecontext P13N Extensions",
      },
    ],
  },
});
