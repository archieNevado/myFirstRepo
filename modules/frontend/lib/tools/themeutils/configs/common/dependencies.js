"use strict";

const closestPackage = require("closest-package");
const fs = require("fs");
const path = require("path");
const resolve = require("resolve");
const semver = require("semver");

/**
 * Represents a dependency with sub dependencies.
 *
 * @param name the name of the dependency revolved from the pkg
 * @param version the version of the dependency resolved from the pkg
 * @param pkgPath the path to the package.json of the npm package the dependency points to
 * @param dependencies (optional) the subdependencies of the dependency
 * @constructor
 */
class NpmDependency {
  constructor (name, version, pkgPath, dependencies = null) {
    this._name = name;
    this._version = version;
    this._pkgPath = pkgPath;
    this._dependencies = dependencies || [];
  }

  /**
   * the name of the dependency revolved from the pkg
   * @return {string}
   */
  getName() {
    return this._name;
  }

  /**
   * the version of the dependency resolved from the pkg
   * @return {string}
   */
  getVersion() {
    return this._version;
  }

  /**
   * The path to the package.json of the npm package the dependency points to
   * @return {string}
   */
  getPkgPath() {
    return this._pkgPath;
  }

  /**
   * The direct dependencies of the dependency
   * @return {Array<NpmDependency>}
   */
  getDependencies() {
    return this._dependencies;
  }

  /**
   * Adds a given direct dependency
   * @param {NpmDependency} dependency
   */
  addDependency(dependency) {
    this.getDependencies().push(dependency);
  }
}

const ACCEPT_ALL = () => true;

/**
 * @callback resolveNpmDependenciesAcceptCallback
 * @param {NpmDependency} dependencyName the dependency to check
 * @param {string} requiredFrom the path to the pkg that depends on the given dependency
 * @return {boolean} if false the resolving will be stopped at that branch.
 */

/**
 *
 * @param pkgPath {String}
 * @param accept  {resolveNpmDependenciesAcceptCallback} (optional) checks if a dependency is included in the result
 * @param parentDependency
 * @param resolvedDependenciesByName {Array} a list of already resolved dependencies
 * @return {NpmDependency} the resolved dependency tree
 */
function resolveNpmDependencies(pkgPath, accept = ACCEPT_ALL, parentDependency = null, resolvedDependenciesByName = []) {
  const pkg = require(pkgPath);
  if (!pkg) {
    return null;
  }
  const me = new NpmDependency(pkg.name, pkg.version, pkgPath);
  if (parentDependency !== null && !accept(me, parentDependency)) {
    return null;
  }

  if (pkg.dependencies instanceof Object) {
    for (let dependencyName of Object.keys(pkg.dependencies)) {
      if (resolvedDependenciesByName.indexOf(dependencyName) > -1) {
        continue;
      }
      const basedir = path.dirname(path.resolve(pkgPath));
      const dependencyPkgPath = closestPackage.sync(resolve.sync(dependencyName, {basedir: basedir}));

      if (dependencyPkgPath) {
        resolvedDependenciesByName.push(dependencyName);
        const resolvedNpmDependency = resolveNpmDependencies(dependencyPkgPath, accept, me, resolvedDependenciesByName);
        if (resolvedNpmDependency) {
          me.addDependency(resolvedNpmDependency);
        }
      }
    }
  }
  return me;
}

/**
 * Get the transitive dependency tree for a given package.
 * The tree is sorted by the order the dependencies are arranged in the respective packages.
 *
 * @param pkgPath the path to the package.json
 * @param accept  {resolveNpmDependenciesAcceptCallback} (optional) checks if a dependency is included in the result
 * @return {Array<NpmDependency>}
 */
function getDependencies(pkgPath, accept = ACCEPT_ALL) {
  const npmDependency = resolveNpmDependencies(pkgPath, accept);
  return npmDependency ? npmDependency.getDependencies() : [];
}

/**
 * Get the transitive dependencies for a given package as flat list.
 * The direct dependencies are sorted by the order the dependencies are arranged in the respective packages.
 * All sub dependencies of a dependency are added before the respective dependency, e.g. if "package-a" depends on
 * "package-b" and "package-b" depends on "package-c" this results in [..."package-c", "package-b", "package-a"...]
 *
 * @param pkgPath the path to the package.json
 * @param accept  {resolveNpmDependenciesAcceptCallback} (optional) checks if a dependency is included in the result
 * @return {Array<NpmDependency>}
 */
function getFlattenedDependencies(pkgPath, accept = ACCEPT_ALL) {
  function collect(dependencies, target) {
    if (dependencies instanceof Array) {
      for (let dependency of dependencies) {
        collect(dependency.getDependencies(), target);
        target.push(dependency);
      }
    }
  }
  const dependencies = getDependencies(pkgPath, accept);
  const result = [];
  collect(dependencies, result);
  return result;
}

function isFileDependency(version) {
  return !semver.valid(version);
}

/**
 * Resolves a given file dependency by using the given "rootPkgPath" and {@link NpmDependency#getVersion} to resolve
 * the path of the file dependency.
 *
 * @param rootPkgPath the path to the pkg that contains the dependency
 * @param fileDependency the file dependency to resolve
 * @return {NpmDependency} The resolved file dependency, {@link NpmDependency#getPkgPath} points to the local npm
 *                         package (not the installation)
 */
function resolveFileDependency(rootPkgPath, fileDependency) {
  const absolutePath = path.resolve(
          path.dirname(rootPkgPath), fileDependency.getVersion().replace(/^file:/, "")
  );
  const pkgPath = path.join(absolutePath, "package.json");
  return new NpmDependency(fileDependency.getName(), "latest", pkgPath);
}

/**
 * Get the transitive file dependencies for a given package as flat list and resolves their absolute path.
 * The direct dependencies are sorted by the order the dependencies are arranged in the respective packages.
 * All sub dependencies of a dependency are added before the respective dependency, e.g. if "package-a" depends on
 * "package-b" and "package-b" depends on "package-c" this results in [..."package-c", "package-b", "package-a"...]
 *
 * @param pkgPath the path to the package.json
 * @param resolvedDependencies an array of already resolved dependencies, defaults to an empty array
 * @return {Array<NpmDependency>} The resolved file dependencies, {@link NpmDependency#getPkgPath}
 *                                points to the local npm package (not the installation)
 */
function resolveFileDependencies(pkgPath, resolvedDependencies = []) {
  const result = [];
  const pkg = require(pkgPath);
  if (pkg.dependencies instanceof Object) {
    const fileDependencies = Object.keys(pkg.dependencies)
            .map(
                    (dependencyName) => new NpmDependency(dependencyName, pkg.dependencies[dependencyName], null)
            ).filter(
                    (dependency) => isFileDependency(dependency.getVersion())
            ).map(
                    (dependency) => resolveFileDependency(pkgPath, dependency)
            ).filter(
                    (dependency) => fs.existsSync(dependency.getPkgPath())
            );

    fileDependencies.forEach(function (fileDependency) {
      if (resolvedDependencies.indexOf(fileDependency.getName()) === -1) {
        resolvedDependencies.push(fileDependency.getName());
        result.push(fileDependency);
        const transitiveFileDependencies = resolveFileDependencies(fileDependency.getPkgPath(), resolvedDependencies);
        transitiveFileDependencies.forEach(function (transitiveFileDependency) {
          result.push(transitiveFileDependency);
        });
      }
    });
  }
  return result;
}

module.exports = {
  NpmDependency,
  getDependencies,
  getFlattenedDependencies,
  resolveFileDependencies
};
