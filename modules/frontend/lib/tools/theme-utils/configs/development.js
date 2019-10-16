const deepMerge = require("./utils/deepMerge");
const { CoreMediaWatchPlugin } = require("../plugins/CoreMediaWatchPlugin");
const {
  getThemeConfig,
  getMonitorConfig,
} = require("@coremedia/tool-utils/workspace");

const themeConfig = getThemeConfig();
let monitorConfig = getMonitorConfig();

// eslint-disable-next-line no-extra-boolean-cast
if (!!process.env.target) {
  monitorConfig.target = process.env.target;
}

/**
 * @module contains the webpack configuration for the development environment
 */
module.exports = () => config =>
  deepMerge(config, {
    // provide an inline-source-map
    devtool: "inline-source-map",
    // make sure sourcemaps provided by other modules are also bundled
    module: {
      rules: [
        {
          test: /\.js$/,
          use: ["source-map-loader"],
          enforce: "pre",
        },
      ],
    },
    plugins: [
      new CoreMediaWatchPlugin({
        themeConfig,
        monitorConfig,
        logLevel: "info",
      }),
    ],
  });
