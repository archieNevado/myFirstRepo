const deepMerge = require("./deepMerge");

/**
 * @module contains the webpack configuration for the development environment
 */
module.exports = () => config => deepMerge(config,
        {
          // provide an inline-source-map
          devtool: "inline-source-map",
          // make sure sourcemaps provided by other modules are also bundled
          module: {
            rules: [
              {
                test: /\.js$/,
                use: ["source-map-loader"],
                enforce: "pre"
              }
            ]
          }
        }
);
