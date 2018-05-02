const CopyWebpackPlugin = require("copy-webpack-plugin");
const closestPackage = require("closest-package");
const fs = require("fs");
const path = require("path");

const deepMerge = require("./utils/deepMerge");
const {
  dependencies: { getFlattenedDependencies, NodeModule },
  workspace: { isBrickModule, getThemeConfig },
} = require("@coremedia/tool-utils");
const JoinWebpackPlugin = require("../plugins/JoinWebpackPlugin");
const { ZipperWebpackPlugin } = require("../plugins/ZipperWebpackPlugin");

const PROPERTIES_REG_EXP = /.+_(.+).properties$/;
const PROPERTIES_GLOB = "*_*.properties";

const themeConfig = getThemeConfig();

const buildConfig = themeConfig.buildConfig;

// inline all images that are smaller than 10000 bytes if not specified differently
const imageEmbedThreshold =
  buildConfig["imageEmbedThreshold"] !== undefined
    ? buildConfig["imageEmbedThreshold"]
    : 10000;

/**
 * Creates a CopyWebpackPlugin instance for the given node modules that will copy the templates to the target
 * folder.
 *
 * Important: We cannot use a single instance as the order the templates are copied is not guaranteed in that case as
 * all patterns are executed simultaneously.
 *
 * @param relativeTemplatesSrc the relative source folder of the templates (must be the same for all node modules)
 * @param relativeTemplatesTarget the relative target folder for the templates
 * @param nodeModules the node modules
 * @return {Array<CopyWebpackPlugin>} an plugin instance for every node module
 */
function configureCopyWebpackPluginsForTemplates(
  relativeTemplatesSrc,
  relativeTemplatesTarget,
  nodeModules
) {
  const patterns = nodeModules
    // build path
    .map(nodeModule =>
      path.resolve(path.dirname(nodeModule.getPkgPath()), relativeTemplatesSrc)
    )

    // check for existence (otherwise the CopyWebpackPlugin will fail)
    .filter(templatePath => fs.existsSync(templatePath))

    // transform into configuration
    .map(templatePath =>
      // templates
      ({
        from: templatePath,
        to: relativeTemplatesTarget,
        force: true,
        cache: true,
      })
    );
  return patterns.map(pattern => new CopyWebpackPlugin([pattern]));
}

/**
 * Creates a single JoinWebpackPlugin instance for the given node modules to join the resource bundles into a
 * single resource bundle (one property file for every language) which is stored in the target folder.
 *
 * @param prefix the prefix to use
 * @param relativeResourceBundleSrc the relative source folder of the resource bundles (must be the same in all node modules)
 * @param relativeResourceBundleTarget the relative target folder for the single bundles
 * @param nodeModules the node modules
 * @returns {JoinWebpackPlugin} the instance of the plugin
 */
function configureJoinWebpackPluginForResourceBundles(
  prefix,
  relativeResourceBundleSrc,
  relativeResourceBundleTarget,
  nodeModules
) {
  // add search patterns for resource bundles, already filtered by actual dependencies to reduce overhead
  const searchPatterns = nodeModules
    .map(nodeModule => path.dirname(nodeModule.getPkgPath()))
    .map(nodeModulePkgPath =>
      path.resolve(
        nodeModulePkgPath,
        relativeResourceBundleSrc,
        PROPERTIES_GLOB
      )
    );

  return new JoinWebpackPlugin({
    name: path.join(relativeResourceBundleTarget, `${prefix}_[1].properties`),
    search: searchPatterns,
    join: function(common, addition, filename) {
      // gather resource bundles using a map (with package name as key), so they can be ordered before being stored
      common = common || {};
      const pkgPath = closestPackage.sync(filename);
      const pkg = require(pkgPath);

      // add source of the keys as comment
      const relativeFilePath = path.relative(themeConfig.path, filename);
      addition =
        `# Resource Bundle from: ${pkg.name}\n# File: ${relativeFilePath}\n\n` +
        addition;

      common[pkg.name] = addition;
      return common;
    },
    save: function(common) {
      // join the map by iterating of the flattened dependency order
      const orderedContent = nodeModules.map(
        nodeModule => common[nodeModule.getName()] || ""
      );
      // join resource bundles, omit empty / non-existing entries
      return orderedContent.filter(content => !!content).join("\n");
    },
    group: "[1]",
    regExp: PROPERTIES_REG_EXP,
  });
}

/**
 * Create patterns for the given relative theme paths to be used in the CopyWebpackPlugin. If a path does not exists,
 * no pattern will be generated for the path.
 *
 * @param themePaths {Array} The paths to configure
 */
function createPatternsCopyOverThemePaths(themePaths) {
  return themePaths
    .map(relativeThemePath => path.join(themeConfig.srcPath, relativeThemePath))
    .filter(fs.existsSync)
    .map(themePath => ({
      from: themePath,
      to: path.relative(themeConfig.srcPath, themePath),
      force: true,
      cache: true,
    }));
}

/**
 * @module contains the webpack configuration for static resources like templates and resource bundles
 */
module.exports = () => config => {
  const themeModule = new NodeModule(
    themeConfig.name,
    themeConfig.version,
    themeConfig.pkgPath
  );
  const brickDependencies = getFlattenedDependencies(
    themeModule.getPkgPath(),
    isBrickModule
  );

  const copyWebpackPluginsForTemplates = [].concat(
    configureCopyWebpackPluginsForTemplates(
      path.join("src", "templates"),
      path.relative(
        themeConfig.themeTargetPath,
        themeConfig.brickTemplatesTargetPath
      ),
      brickDependencies
    ),
    configureCopyWebpackPluginsForTemplates(
      path.join("src", "templates"),
      path.relative(
        themeConfig.themeTargetPath,
        themeConfig.themeTemplatesTargetPath
      ),
      [themeModule]
    )
  );
  const joinWebpackPlugin = configureJoinWebpackPluginForResourceBundles(
    "Bricks",
    path.join("src", "l10n"),
    "l10n",
    brickDependencies
  );

  return deepMerge(config, {
    module: {
      rules: [
        // let svgParamLoader process the svg files to inject parameters if needed
        {
          test: /\.param\.svg$/,
          loader: require.resolve("../loaders/SvgParamLoader"),
          options: {
            name: "[name].[ext]",
            limit: 0, // always inline svg with parameters
            outputPath: "svg/", // if for whatever reasons limit 0 does not work
          },
        },
        {
          test: /\.(svg|jpg|jpeg|png|gif)$/,
          loader: "url-loader",
          exclude: /\.param\.svg$/, // do not double load svg files with injected parameters
          options: {
            name: "[name].[ext]",
            limit: imageEmbedThreshold,
            outputPath: "img/",
          },
        },
        {
          test: /\.(woff|woff2|ttf|eot)$/,
          loader: "file-loader",
          options: {
            name: "[name].[ext]",
            outputPath: "fonts/",
          },
        },
        {
          test: /\.swf$/,
          loader: "file-loader",
          options: {
            name: "[name].[ext]",
            outputPath: "swf/",
          },
        },
        {
          test: PROPERTIES_REG_EXP,
          use: [joinWebpackPlugin.loader()],
        },
      ],
    },
    plugins: [
      ...copyWebpackPluginsForTemplates,
      joinWebpackPlugin,
      // configure for themes
      new CopyWebpackPlugin([
        ...createPatternsCopyOverThemePaths([
          "css",
          "fonts",
          "img",
          "images",
          "l10n",
          "vendor",
        ]),
        {
          context: themeConfig.path,
          from: path.basename(themeConfig.descriptorTargetPath),
          to: path.relative(
            themeConfig.themeTargetPath,
            path.dirname(themeConfig.descriptorTargetPath)
          ),
          force: true,
          cache: true,
        },
      ]),
      new ZipperWebpackPlugin(
        [
          {
            source: path.relative(
              themeConfig.resourcesTargetPath,
              themeConfig.themeTemplatesTargetPath
            ),
            prefix: path.normalize("META-INF/resources/"),
            context: path.relative(
              themeConfig.themeTargetPath,
              themeConfig.resourcesTargetPath
            ),
          },
        ],
        {
          filepath: path.relative(
            themeConfig.themeTargetPath,
            themeConfig.themeTemplatesJarTargetPath
          ),
          compilerEvent: "after-emit",
        }
      ),
      new ZipperWebpackPlugin(
        [
          {
            source: path.relative(
              themeConfig.resourcesTargetPath,
              themeConfig.brickTemplatesTargetPath
            ),
            prefix: path.normalize("META-INF/resources/"),
            context: path.relative(
              themeConfig.themeTargetPath,
              themeConfig.resourcesTargetPath
            ),
          },
        ],
        {
          filepath: path.relative(
            themeConfig.themeTargetPath,
            themeConfig.brickTemplatesJarTargetPath
          ),
          compilerEvent: "after-emit",
        }
      ),
    ],
  });
};
