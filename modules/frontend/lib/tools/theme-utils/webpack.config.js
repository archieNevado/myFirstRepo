const escapeStringRegexp = require("escape-string-regexp");
const path = require("path");
const flow = require("lodash/fp/flow");
const { DependencyCheckWebpackPlugin } = require("@coremedia/dependency-check");
const {
  workspace: { getThemeConfig },
} = require("@coremedia/tool-utils");

const clean = require("./configs/clean");
const themeZip = require("./configs/themeZip");
const production = require("./configs/production");
const development = require("./configs/development");
const javascript = require("./configs/javascript");
const exposeModules = require("./configs/exposeModules");
const sass = require("./configs/sass");
const staticResources = require("./configs/staticResources");

const themeConfig = getThemeConfig();

const include = [path.resolve(".")];

const exclude = [
  // All modules but CoreMedia specific modules
  new RegExp(
    escapeStringRegexp(path.sep + "node_modules" + path.sep) +
      "((?!@coremedia).)*$"
  ),
  new RegExp(escapeStringRegexp(path.sep + "legacy" + path.sep)),
  new RegExp(escapeStringRegexp(path.sep + "vendor" + path.sep)),
];

const dependencyCheckPlugin = new DependencyCheckWebpackPlugin({
  // do not pass include here, this is only for es-lint
  exclude: exclude,
});

// merge different webpack configurations (order matters!)
const base = flow(
  clean(),
  staticResources(),
  sass({ dependencyCheckPlugin }),
  javascript({ include, exclude, dependencyCheckPlugin }),
  exposeModules(),
  process.env.NODE_ENV === "production" ? production() : development(),
  themeZip()
);

const entry = {};
entry[themeConfig.name] = [];

module.exports = base({
  entry: entry,
  context: themeConfig.path,
  output: {
    path: themeConfig.themeTargetPath,
  },
  // see https://webpack.js.org/configuration/stats/
  stats: "minimal",
});
