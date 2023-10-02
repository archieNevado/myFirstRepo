const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "app",
  applicationClass: "@coremedia/studio-client.main.editor-components/StudioApplication",
  theme: "@coremedia-blueprint/studio-client.main.blueprint-studio-theme",
  sencha: {
    name: "com.coremedia.blueprint__studio-base-app",
    namespace: "",
    loader: {
      cache: "${build.timestamp}",
      cacheParam: "_ts",
    },
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
      start_url: ".",
      theme_color: "#b3b1b1",
      background_color: "#b3b1b1",
      display: "standalone",
      categories: [
        "Studio",
      ],
      cmKey: "cmMainApp",
      shortcuts: [
        {
          cmKey: "cmContent",
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
