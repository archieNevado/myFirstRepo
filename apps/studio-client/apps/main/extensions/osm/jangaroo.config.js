const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.blueprint__osm-studio",
    namespace: "com.coremedia.blueprint.studio.osm",
    css: [
      {
        path: "resources/css/osm-ui.css",
      },
    ],
    js: [
      {
        path: "resources/osm/OpenLayers.js",
      },
      {
        path: "resources/osm/OpenStreetMap.js",
      },
    ],
    studioPlugins: [
      {
        mainClass: "com.coremedia.blueprint.studio.osm.OSMStudioPlugin",
        name: "Open Streetmap",
      },
    ],
  },
});
