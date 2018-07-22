const fs = require("fs");
const resolveFrom = require("resolve-from");
const path = require("path");
const escapeStringRegexp = require("escape-string-regexp");
const { optimize: { CommonsChunkPlugin } } = require("webpack");
const { DependencyCheckWebpackPlugin } = require("@coremedia/dependency-check");
const {
  workspace: {
    DEFAULT_VARIANT,
    getInitJs,
    getShims,
    getThemeConfig,
    getIsSmartImportModuleFor,
    isBrickModule,
  },
  dependencies: { getFlattenedDependencies },
} = require("@coremedia/tool-utils");
const deepMerge = require("./utils/deepMerge");

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

const DEFAULT_ENTRY_NAME = themeConfig.name;
const PREVIEW_ENTRY_NAME = "preview";

function getMainJs(packageName) {
  let mainJs = null;
  try {
    mainJs = resolveFrom(themeConfig.path, packageName);
  } catch (e) {
    // ignore, just a check
  }
  return mainJs;
}

/**
 * Builds an entry point with the given name and collects smart import dependencies using the given variant.
 *
 * @param name the name of the entry point
 * @param variant the variant to use
 * @param path the path to the file entry
 * @returns {object} the entry point
 */
function buildEntryPoint(name, variant, path) {
  // if the entry point provides no entry point for JavaScript, use an empty index.js (otherwise webpack will not run)
  if (!fs.existsSync(path)) {
    path = require.resolve("./emptyIndex");
  }
  const autoActivateDependencies = getFlattenedDependencies(
    themeConfig.pkgPath,
    getIsSmartImportModuleFor(variant)
  );
  const autoActivateEntryPoints = autoActivateDependencies
    .map(getInitJs)
    .filter(dependency => !!dependency);
  const entry = {};
  entry[name] = [...autoActivateEntryPoints, path];
  return entry;
}

function getLoaderParams(obj) {
  return Object.keys(obj)
    .map(key => {
      const value = obj[key];
      if (key) {
        return `${encodeURIComponent(key)}=${encodeURIComponent(value)}`;
      }
      return encodeURIComponent(value);
    })
    .join("&");
}

function getShimLoaderConfig() {
  return getFlattenedDependencies(themeConfig.pkgPath, isBrickModule)
    .map(getShims)
    .reduce((aggregator, next) => aggregator.concat(next), [])
    .map(shim => {
      const loaders = [];
      const imports = shim.getImports();
      const exports = shim.getExports();
      if (Object.keys(imports).length) {
        loaders.push("imports-loader?" + getLoaderParams(imports));
      }
      if (Object.keys(exports).length) {
        loaders.push("exports-loader?" + getLoaderParams(exports));
      }
      return {
        test: require.resolve(shim.getTarget()),
        use: loaders,
      };
    })
    .reduce((aggregator, next) => aggregator.concat(next), []);
}

const mainJsPath = getMainJs(".");
const previewJsPath = mainJsPath
  ? path.resolve(path.dirname(mainJsPath), "preview.js")
  : path.resolve(themeConfig.srcPath, "js/preview.js");

module.exports = () => config =>
  deepMerge(config, {
    entry: {
      ...buildEntryPoint(DEFAULT_ENTRY_NAME, DEFAULT_VARIANT, mainJsPath),
      ...buildEntryPoint(PREVIEW_ENTRY_NAME, "preview", previewJsPath),
    },
    output: {
      filename: path.join("js", "[name].js"),
    },
    module: {
      rules: [
        {
          test: /\.js$/,
          loader: "eslint-loader",
          options: {
            cache: true,
          },
          enforce: "pre",
          include: include,
          exclude: exclude,
        },
        {
          test: /\.js$/,
          use: [
            {
              loader: "babel-loader",
              // babel < 7 does not support the .babelrc.js yet, so import it explicitly here
              // remove this explicit merge when we are upgrading to babel 7
              options: deepMerge(require("../.babelrc"), {
                // babel-loader specific options
                cacheDirectory: true,
              }),
            },
          ],
          exclude: exclude,
        },
        ...getShimLoaderConfig(),
      ],
    },
    plugins: [
      new DependencyCheckWebpackPlugin({
        exclude: exclude,
      }),
      // preview entry is meant to be loaded after the default entry has been loaded
      // so common chunks can be moved to the default entry
      new CommonsChunkPlugin({
        name: DEFAULT_ENTRY_NAME,
        chunks: [DEFAULT_ENTRY_NAME, PREVIEW_ENTRY_NAME],
      }),
    ],
  });
