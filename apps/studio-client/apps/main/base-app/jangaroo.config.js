const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "app",
  applicationClass: "@coremedia/studio-client.main.editor-components/StudioApplication",
  theme: "@coremedia-blueprint/studio-client.main.blueprint-studio-theme",
  sencha: {
    name: "com.coremedia.blueprint__studio-base-app",
    namespace: "",
    loader: {
      cache: "${build.timestamp},",
      cacheParam: "_ts",
    },
    appStudioPlugins: [
      {
        mainClass: "com.coremedia.cms.editor.sdk.sites.LocalizationManagerStudioPlugin",
        name: "LocalizationManagerStudioPlugin",
      },
    ],
  },
  appManifests: {
    en: {
      cmKey: "cmMainApp",
      cmOrder: 10,
      name: "CoreMedia Studio",
      short_name: "Content",
      icons: [
        {
          src: "appIcons/content-app_24.svg",
          sizes: "24x24",
          type: "image/svg",
        },
        {
          src: "appIcons/content-app_192.png",
          sizes: "192x192",
          type: "image/png",
        },
      ],
      start_url: ".",
      theme_color: "#b3b1b1",
      background_color: "#b3b1b1",
      display: "standalone",
      categories: [
        "Content",
        "External Services",
      ],
      cmCategoryIcons: {
        "External Services": [
          {
            src: "appIcons/external-services_24.svg",
            sizes: "24x24",
            type: "image/svg",
          },
          {
            src: "appIcons/external-services_192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "appIcons/external-services_512.png",
            sizes: "512x512",
            type: "image/png",
          },
        ],
      },
      cmServiceShortcuts: [
        {
          cmKey: "cmLocalizationManager",
          cmOrder: 60,
          cmCategory: "Content",
          name: "Sites",
          url: "",
          cmService: {
            name: "launchSubAppService",
            method: "launchSubApp",
          },
        },
      ],
      cmServices: [
        {
          name: "workAreaService",
        },
        {
          name: "libraryService",
        },
      ],
    },
  },
  additionalLocales: [
    "de",
    "ja",
  ],
  command: {
    run: {
      proxyTargetUri: "http://localhost:41080",
      proxyPathSpec: "/rest/",
    },
  },
});
