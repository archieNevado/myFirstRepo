const autoprefixer = require("autoprefixer");
const postcssObjectFitImages = require("postcss-object-fit-images");
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const fs = require("fs");
const nodeSass = require("node-sass");
const path = require("path");

const { getThemeConfig } = require("@coremedia/tool-utils/workspace");
const {
  getDependencyCheckNodeSassImporter,
} = require("@coremedia/dependency-check");
const deepMerge = require("./utils/deepMerge");
const {
  sassSmartImport,
  sassExcludeImport,
  sassImportOnce,
} = require("../importer/sass");

const themeConfig = getThemeConfig();

// specifies the path to the css directory relative to the webpack output path
const CSS_PATH = "css";

// the ExtractTextPlugin does not transform the relative urls to files in css when using a different
// output path than the webpack output path, so we need to change the publicPath to be relative from
// the CSS_PATH to the webpack output path.
const EXTRACT_CSS_PUBLIC_PATH = CSS_PATH
  ? path.relative(CSS_PATH, "") + "/"
  : undefined;

const include = [path.resolve(".")];

const exclude = [
  // All modules but CoreMedia specific modules
  /\/node_modules\/((?!@coremedia).)*$/,
  /\/legacy\//,
  /\/vendor\//,
];

// Create entry point(s)

const entry = {};

// 1) Theme entry point

entry[themeConfig.name] = [
  path.resolve(themeConfig.path, `src/sass/${themeConfig.name}.scss`),
];

// 2) Preview entry point (special handling)

const previewScssPath = path.resolve(themeConfig.path, "src/sass/preview.scss");
if (fs.existsSync(previewScssPath)) {
  entry["preview"] = [previewScssPath];
}

const extractTextPluginForCss = new ExtractTextPlugin({
  filename: path.join(CSS_PATH, "[name].css"),
  allChunks: true, // prevents second compilation of sass (doubles build speed)
});

module.exports = () => config =>
  deepMerge(config, {
    entry: entry,
    module: {
      rules: [
        // CSS
        {
          test: /\.scss$/,
          use: extractTextPluginForCss.extract({
            use: [
              {
                loader: "css-loader",
                options: {
                  sourceMap: process.env.NODE_ENV !== "production",
                  minimize: process.env.NODE_ENV === "production",
                },
              },
              {
                loader: "postcss-loader",
                options: {
                  sourceMap: process.env.NODE_ENV !== "production",
                  plugins: [
                    autoprefixer({
                      // enable css-grid for IE
                      grid: true,
                    }),
                    postcssObjectFitImages,
                  ],
                },
              },
              {
                loader: "resolve-url-loader",
                options: {
                  sourceMap: process.env.NODE_ENV !== "production",
                  keepQuery: true,
                },
              },
              {
                loader: "sass-loader",
                options: {
                  sourceMap: true,
                  precision: 10,
                  importer: [
                    sassSmartImport,
                    sassExcludeImport,
                    getDependencyCheckNodeSassImporter(include, exclude),
                    sassImportOnce,
                  ],
                  functions: {
                    "encodeBase64($string)": function($string) {
                      const buffer = new Buffer($string.getValue());
                      return nodeSass.types.String(buffer.toString("base64"));
                    },
                    "encodeURIComponent($string)": function($string) {
                      return nodeSass.types.String(
                        encodeURIComponent($string.getValue())
                      );
                    },
                  },
                },
              },
            ],
            publicPath: EXTRACT_CSS_PUBLIC_PATH,
          }),
        },
      ],
    },
    plugins: [extractTextPluginForCss],
  });
