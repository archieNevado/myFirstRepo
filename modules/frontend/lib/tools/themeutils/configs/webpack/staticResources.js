const deepMerge = require("./deepMerge");
const CopyWebpackPlugin = require('copy-webpack-plugin');
const closestPackage = require("closest-package");
const path = require('path');
const { getFlattenedDependencies } = require("../common/dependencies");
const { isBrickDependency } = require("../common/workspace");

function configureCopyWebpackPluginForBricks(relativeTemplatesTarget, relativeWebresourcesTarget) {
  const brickDependencies = getFlattenedDependencies(closestPackage.sync(process.cwd()), isBrickDependency);

  const patterns = brickDependencies.map(
          brickDependency => path.dirname(brickDependency.getPkgPath())
  ).map(
          brickDependencyPkgPath => [
            // templates
            {
              context: path.join(brickDependencyPkgPath, "src", "templates"),
              from: "**",
              to: relativeTemplatesTarget,
              force: true
            },
            // l10n
            {
              context: path.join(brickDependencyPkgPath, 'src'),
              from: "l10n/**",
              to: relativeWebresourcesTarget
            }
          ]
  ).reduce(
          (dependencyPatternsA, dependencyPatternsB) => dependencyPatternsA.concat(dependencyPatternsB), []
  );

  return new CopyWebpackPlugin(
          patterns
  );
}


/**
 * @module contains the webpack configuration for static resources like templates and resource bundles
 */
module.exports = () => config => deepMerge(config,
        {
          plugins: [
            configureCopyWebpackPluginForBricks(
                    "../../WEB-INF/templates/bricks",
                    "" // means the output path
            )
          ]
        }
);
