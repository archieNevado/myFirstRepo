'use strict';

const closestPackage = require("closest-package");
const path = require("path");
const { resolveFileDependencies } = require("../common/dependencies");

const files = [];

const pkgPath = closestPackage.sync(process.cwd());
const nodeModulesDirectory = path.resolve(path.dirname(pkgPath), "node_modules");
const fileDependencies = resolveFileDependencies(pkgPath);

for (const fileDependency of fileDependencies) {
  files.push({
    expand: true,
    cwd: path.relative(
      process.cwd(),
      path.resolve(path.dirname(fileDependency.getPkgPath()), "src")
    ),
    src: ["**"],
    dest: path.resolve(nodeModulesDirectory, fileDependency.getName(), "src"),
    dot: true
  });
}

/* default task for syncing the node_modules in installed theme directories */
module.exports = {
  localPackages: {
    files: files,
    verbose: true,
    compareUsing: "md5",
    updateAndDelete: true,
    ignoreInDest: ["package.json", "node_modules/**", "npm-debug.log"]
  }
};
