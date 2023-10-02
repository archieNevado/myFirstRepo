const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__lc-ecommerce-ibm-studio",
    namespace: "com.coremedia.livecontext.ibm.studio",
    studioPlugins: [
      {
        mainClass: "com.coremedia.livecontext.ibm.studio.EcommerceIbmStudioPlugin",
        name: "ecommerce-ibm extension",
      },
    ],
  },
  appManifests: {
    en: {
      cmServiceShortcuts: [
        {
          cmKey: "cmWcsManagementCenter",
          name: "WCS Management",
          url: "",
          cmCategory: "External",
          icons: [
            {
              src: "packages/com.coremedia.blueprint__lc-ecommerce-ibm-studio/appIcons/wcs-management_24.svg",
              sizes: "24x24",
              type: "image/svg",
            },
            {
              src: "packages/com.coremedia.blueprint__lc-ecommerce-ibm-studio/appIcons/wcs-management_192.png",
              sizes: "192x192",
              type: "image/png",
            },
          ],
          cmService: {
            name: "launchSubAppService",
            method: "launchSubApp",
          },
        },
      ],
    },
  },
  command: {
    build: {
      ignoreTypeErrors: true,
    },
    joounit: {
      testSuite: "./joounit/TestSuite",
      testExecutionTimeout: 120000,
    },
  },
});
