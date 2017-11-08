const path = require('path');
const compose = require("lodash/fp/compose");

const { getThemeConfig } = require("./configs/common/workspace");
const production = require("./configs/webpack/production");
const development = require("./configs/webpack/development");
const javascript = require("./configs/webpack/javascript");
const exposeModules = require("./configs/webpack/exposeModules");
const sass = require("./configs/webpack/sass");
const staticResources = require("./configs/webpack/staticResources");

const themeConfig = getThemeConfig();

// target paths
const resourcesTarget = path.resolve(themeConfig.targetPath, "resources");
const themeTarget = path.join(resourcesTarget, "themes", themeConfig.name);

const base = compose(
  process.env.NODE_ENV === 'production' ? production() : development(),
  javascript(),
  exposeModules(),
  sass(),
  staticResources()
);

const entry = {};
entry[themeConfig.name] = [];

module.exports = base({
  entry: entry,
  progress: false,
  context: process.cwd(),
  output: {
    path: themeTarget
  },
  // see https://webpack.js.org/configuration/stats/
  stats: "minimal"
});
