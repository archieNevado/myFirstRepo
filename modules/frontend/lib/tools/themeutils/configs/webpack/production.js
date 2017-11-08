const { optimize: { UglifyJsPlugin } } = require("webpack");
const deepMerge = require("./deepMerge");

/**
 * @module contains the webpack configuration for the production environment
 */
module.exports = () => config => deepMerge(config,
        {
          plugins: [
            new UglifyJsPlugin({})
          ]
        }
);
