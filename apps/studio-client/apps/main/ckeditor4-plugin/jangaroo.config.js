const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  autoLoad: [
    "./src/init"
  ],
  sencha: {
    studioPlugins: [
      {
        mainClass: "__coremedia_blueprint.studio_client_main_ckeditor4_plugin.CKE4StudioPlugin",
        name: "CKEditor 4 Studio Plugin",
      },
    ],
  },
});
