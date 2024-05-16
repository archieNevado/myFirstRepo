const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__lc-es-studio",
    namespace: "com.coremedia.livecontext.elastic.social.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.livecontext.elastic.social.studio.LcElasticSocialStudioPlugin",
        name: "Livecontext Elastic Social Studio Extension",
      },
    ],
  },
});
