const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__es-studio",
    namespace: "com.coremedia.blueprint.elastic.social.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.elastic.social.studio.ElasticSocialStudioPlugin",
        name: "Elastic Social Extensions",
      },
    ],
  },
});
