"use strict";

const closestPackage = require("closest-package");

const DEFAULT_TARGET_PATH = "../../target/";
const BRICK_PREFIX = "@coremedia/brick-";

/**
 * Gives the config of the theme the process has been started from.
 *
 * @return {{name: string, targetPath: string}} the theme configuration
 */
function getThemeConfig() {
  const packageJsonPath = closestPackage.sync(process.cwd());
  const packageJson = require(packageJsonPath);
  let result = packageJson.theme;
  if (!result) {
    throw new Error(`No theme config found in package.json '${packageJsonPath}'`);
  }
  if (!result.name || typeof result.name !== "string") {
    throw new Error(`Invalid 'name' provided in theme configuration of package.json '${packageJsonPath}'`);
  }
  result.targetPath = result.targetPath !== undefined ? result.targetPath : DEFAULT_TARGET_PATH;
  if (!result.targetPath || typeof result.targetPath !== "string") {
    throw new Error(`Invalid 'targetPath' provided in theme configuration of package.json '${packageJsonPath}'`);
  }
  return result;
}

/**
 * Checks if the given dependency is a brick dependency
 * @param npmDependency {NpmDependency} the dependency
 * @return {boolean}
 */
function isBrickDependency(npmDependency) {
  return npmDependency && npmDependency.getName() && npmDependency.getName().indexOf(BRICK_PREFIX) === 0;
}

module.exports = {
  getThemeConfig,
  isBrickDependency
};
