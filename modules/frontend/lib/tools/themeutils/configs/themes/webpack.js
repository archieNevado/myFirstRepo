/**
 * Register grunt task for generating grunt configs for webpack.
 */
'use strict';

const webpackConfig = require("../../webpack.config");

module.exports = {
    theme: webpackConfig
};
