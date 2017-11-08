const fs = require("fs");
const resolveFrom = require("resolve-from");
const path = require("path");
const escapeStringRegexp = require("escape-string-regexp");
const { DependencyCheckWebpackPlugin } = require("@coremedia/dependency-check");
const deepMerge = require("./deepMerge");
const { getThemeConfig } = require("../common/workspace");

const themeConfig = getThemeConfig();

const include = [
  path.resolve(".")
];

const exclude = [
  // All modules but CoreMedia specific modules
  new RegExp(escapeStringRegexp(path.sep + "node_modules" + path.sep) + "((?!@coremedia).)*$"),
  new RegExp(escapeStringRegexp(path.sep + "legacy" + path.sep)),
  new RegExp(escapeStringRegexp(path.sep + "vendor" + path.sep)),
];

const entry = {};

// check if a javascript entry point exists for the module
let mainJsPath = null;
let previewJsPath = null;
try {
  mainJsPath = resolveFrom(process.cwd(), ".");
  previewJsPath = path.resolve(path.dirname(mainJsPath), "preview.js");
  if (!fs.existsSync(previewJsPath)) {
    previewJsPath = null;
  }
} catch (e) {
  // the theme provides no entry point for JavaScript, so use an empty index.js (otherwise webpack will not run)
  mainJsPath = require.resolve("./emptyIndex");
}

entry[themeConfig.name] = [ mainJsPath ];
if (previewJsPath) {
  entry["preview"] = [ previewJsPath ];
}

module.exports = () => config => deepMerge(config,
        {
          entry: entry,
          output: {
            filename: path.join("js", "[name].js")
          },
          module: {
            rules: [
              {
                test: /\.js$/,
                loader: 'eslint-loader',
                enforce: "pre",
                include: include,
                exclude: exclude
              },
              {
                test: /\.js$/,
                loader: 'babel-loader',
                include: include,
                exclude: exclude,
                // babel < 7 does not support the .babelrc.js yet, so import it explicitly here
                // remove this explicit merge when we are upgrading to babel 7
                options: deepMerge(require("../../.babelrc"), {
                  // babel-loader specific options
                  cacheDirectory: true
                })
              },
              {
                test: /\.(swf)$/,
                loader: 'file-loader',
                options: {
                  name: "[name].[ext]",
                  outputPath: "swf/"
                }
              }
            ]
          },
          plugins: [
            new DependencyCheckWebpackPlugin({
              include: include,
              exclude: exclude
            })
          ]
        }
);
