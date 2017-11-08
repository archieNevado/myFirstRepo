const ExtractTextPlugin = require("extract-text-webpack-plugin");
const fs = require("fs");
const nodeSass = require("node-sass");
const path = require("path");
const deepMerge = require("./deepMerge");
const { getThemeConfig } = require("../common/workspace");
const { getDependencyCheckNodeSassImporter } = require("@coremedia/dependency-check");
const sassExcludeImport = require("../../sass/sassExcludeImport");
const sassImportOnce = require("../../sass/sassImportOnce");

const themeConfig = getThemeConfig();

// specifies the path to the css directory relative to the webpack output path
const CSS_PATH = "css";

// the ExtractTextPlugin does not transform the relative urls to files in css when using a different
// output path than the webpack output path, so we need to change the publicPath to be relative from
// the CSS_PATH to the webpack output path.
const EXTRACT_CSS_PUBLIC_PATH = CSS_PATH ? path.relative(CSS_PATH, "") + "/" : undefined;

const include = [
  path.resolve(".")
];

const exclude = [
  // All modules but CoreMedia specific modules
  /\/node_modules\/((?!@coremedia).)*$/,
  /\/legacy\//,
  /\/vendor\//,
];

const entry = {};
entry[themeConfig.name] = [
  path.resolve(process.cwd(), `src/sass/${themeConfig.name}.scss`)
];

const previewScssPath = path.resolve(process.cwd(), "src/sass/preview.scss");
if (fs.existsSync(previewScssPath)) {
  entry["preview"] = [
    previewScssPath
  ];
}

module.exports = () => config => deepMerge(config,
        {
          entry: entry,
          module: {
            rules: [
              // web resources
              {
                test: /\.(svg|png|gif)$/,
                loader: 'file-loader',
                options: {
                  name: "[name].[ext]",
                  outputPath: "img/"
                }
              },
              {
                test: /\.(woff|woff2|ttf|eot)$/,
                loader: 'file-loader',
                options: {
                  name: "[name].[ext]",
                  outputPath: "fonts/"
                }
              },
              // CSS
              {
                test: /\.scss$/,
                use: ExtractTextPlugin.extract({
                  use: [
                    {
                      loader: "css-loader",
                      options: {
                        sourceMap: true
                      }
                    },
                    {
                      loader: "postcss-loader",
                      options: {
                        sourceMap: true,
                        plugins: [
                          require('autoprefixer')({
                            // enable css-grid for IE
                            grid: true
                          })
                        ]
                      }
                    },
                    {
                      loader: "resolve-url-loader",
                      options: {
                        sourceMap: true,
                        keepQuery: true
                      }
                    },
                    {
                      loader: "sass-loader",
                      options: {
                        sourceMap: true,
                        precision: 10,
                        importer: [
                          sassExcludeImport,
                          getDependencyCheckNodeSassImporter(include, exclude),
                          sassImportOnce
                        ],
                        functions: {
                          'encodeBase64($string)': function($string) {
                            const buffer = new Buffer($string.getValue());
                            return nodeSass.types.String(buffer.toString('base64'));
                          }
                        }
                      }
                    }
                  ],
                  publicPath: EXTRACT_CSS_PUBLIC_PATH
                })
              }
            ]
          },
          plugins: [
            new ExtractTextPlugin({
              filename: path.join(CSS_PATH, "[name].css"),
              allChunks: true // prevents second compilation of sass (doubles build speed)
            })
          ]
        }
);
