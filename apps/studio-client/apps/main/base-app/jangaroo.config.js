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
      name: "CoreMedia Studio",
      short_name: "Studio",
      icons: [
        {
          src: "appIcons/android-chrome-192x192.png",
          sizes: "192x192",
          type: "image/png",
        },
        {
          src: "appIcons/android-chrome-512x512.png",
          sizes: "512x512",
          type: "image/png",
        },
        {
          src: "appIcons/coremedia_24.svg",
          sizes: "24x24",
          type: "image/svg",
        },
      ],
      start_url: "index.html",
      theme_color: "#b3b1b1",
      background_color: "#b3b1b1",
      display: "standalone",
      categories: [
        "Content",
        "Taxonomy Manager",
        "Sites",
        "External Services",
      ],
      cmCategoryIcons: {
        "Taxonomy Manager": [
          {
            src: "appIcons/taxonomy_manager_24.svg",
            sizes: "24x24",
            type: "image/svg",
          },
          {
            src: "appIcons/taxonomy_manager_192.png",
            sizes: "192x192",
            type: "image/png",
          },
        ],
        Sites: [
          {
            src: "packages/com.coremedia.ui.sdk__editor-components/appIcons/sites_24.svg",
            sizes: "24x24",
            type: "image/svg",
          },
          {
            src: "packages/com.coremedia.ui.sdk__editor-components/appIcons/sites_192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "packages/com.coremedia.ui.sdk__editor-components/appIcons/sites_512.png",
            sizes: "512x512",
            type: "image/png",
          },
        ],
        "External Services": [
          {
            src: "appIcons/external-preview-link_24.svg",
            sizes: "24x24",
            type: "image/svg",
          },
          {
            src: "appIcons/external-preview-link_192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "appIcons/external-preview-link_512.png",
            sizes: "512x512",
            type: "image/png",
          },
        ],
      },
      cmKey: "cmMainApp",
      shortcuts: [
        {
          cmKey: "cmContent",
          cmCategory: "Content",
          name: "Content",
          url: "",
          icons: [
            {
              src: "appIcons/type-asset-document_24.svg",
              sizes: "24x24",
              type: "image/svg",
            },
            {
              src: "appIcons/type-asset-document_192.png",
              sizes: "192x192",
              type: "image/png",
            },
          ],
        },
      ],
      cmServiceShortcuts: [
        {
          cmKey: "cmLocalizationManager",
          cmCategory: "Sites",
          name: "Sites",
          url: "",
          icons: [
            {
              src: "packages/com.coremedia.ui.sdk__editor-components/appIcons/sites_24.svg",
              sizes: "24x24",
              type: "image/svg",
            },
            {
              src: "packages/com.coremedia.ui.sdk__editor-components/appIcons/sites_192.png",
              sizes: "192x192",
              type: "image/png",
            },
            {
              src: "packages/com.coremedia.ui.sdk__editor-components/appIcons/sites_512.png",
              sizes: "512x512",
              type: "image/png",
            },
          ],
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
