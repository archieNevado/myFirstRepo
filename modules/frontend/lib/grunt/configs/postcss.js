/* default task for browser prefixes (just to own css) */
module.exports = function(grunt, options) {
  'use strict';

  return {
    options: {
      map: true,
      processors: [
        require('autoprefixer')({
          //see https://github.com/ai/browserslist#major-browsers
          // CoreMedia supports latest major browsers and IE9
          browsers: options.themeConfig.supportedBrowsers || [
            "last 2 versions",
            "Firefox ESR",
            "Explorer >= 9"
          ]
        })
      ]
    },
    dist: {
      src: [
        '../../target/resources/themes/<%= themeConfig.name %>/css/*.css'
      ]
    }
  };
};
