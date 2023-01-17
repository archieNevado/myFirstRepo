const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  autoLoad: [
    "./src/init",
  ],
  sencha: {
    name: "com.coremedia.blueprint__blueprint-ckeditor5-plugin",
    namespace: "com.coremedia.cms.studio.ckeditor5plugin",
    studioPlugins: [
      {
        mainClass: "com.coremedia.cms.studio.ckeditor5plugin.CKEditor5StudioPlugin",
        name: "CKEditor 5 Studio Plugin",
      },
    ],
  },
});
