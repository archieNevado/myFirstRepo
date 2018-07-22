const fs = require("fs");
const loaderUtils = require("loader-utils");
const path = require("path");
const glob = require("glob");
const closestPackage = require("closest-package");
const {
  dependencies: { getDependencies, getDependentsFirstLoadOrder },
  workspace: { isBrickModule },
} = require("@coremedia/tool-utils");

function moduleLoadedCallback(err) {
  if (err) {
    throw new Error("Could not load module: " + err);
  }
}

const importedViews = [];
let dependenciesToLoadByPackageName = {};

module.exports = function() {
  const callback = this.async();
  // cannot be cached because of 2 reasons:
  // 1) loader is not stateless due to needing to track the "importedViews"
  // 2) a plugin configuration in clean.js clears the templates that have not been provided by the last build
  this.cacheable(false);

  // parse query params
  const queryParams = loaderUtils.getOptions(this) || {};
  const pattern = queryParams.pattern || "**/*.*";
  const directory =
    queryParams.directory || path.join(process.cwd(), "src", "templates");
  const subtask = queryParams.subtask === true;

  const pkgPath = closestPackage.sync(directory);
  const packageName = require(pkgPath).name;

  if (!subtask) {
    // clear array of imported views as new run was started
    importedViews.length = 0;

    const dependencies = getDependencies(pkgPath, isBrickModule);
    dependenciesToLoadByPackageName = getDependentsFirstLoadOrder(
      dependencies,
      packageName
    );
  }

  if (directory && fs.existsSync(directory)) {
    // track context dependency of directly registered template folders
    this.addContextDependency(directory);
    const foundSubdirectories = glob.sync("**/*/", { cwd: directory });
    // track all sub folders
    foundSubdirectories.forEach(foundSubdirectory =>
      this.addContextDependency(
        path.join(directory, path.basename(foundSubdirectory))
      )
    );

    // find all views
    const globResult = glob
      .sync(pattern, {
        cwd: directory,
        nodir: true,
      })
      .filter(file => !importedViews.includes(file));

    // only track imported views for subtasks = bricks
    if (subtask) {
      importedViews.push(...globResult);
    }

    const foundModules = globResult
      .map(file => fs.realpathSync(path.join(directory, file)))
      .map(file =>
        loaderUtils.urlToRequest(
          path.relative(process.cwd(), file),
          this.context
        )
      );

    foundModules.forEach(module =>
      this.loadModule(module, moduleLoadedCallback)
    );
  }

  // modules need to be loaded synchronously, so wait until a module is fully loaded
  const modulesToLoad = dependenciesToLoadByPackageName[packageName] || [];
  const load = () => {
    if (modulesToLoad.length === 0) {
      callback(null, "");
    } else {
      // trigger next module:
      const nextModule = modulesToLoad.shift();
      const modulePath = path.join(
        path.dirname(nextModule.getPkgPath()),
        "src",
        "templates"
      );
      this.loadModule(
        `${__filename}?pattern=${encodeURIComponent(
          pattern
        )}&subtask=true&directory=${encodeURIComponent(modulePath)}!`,
        err => {
          moduleLoadedCallback(err);
          load();
        }
      );
    }
  };

  load();
};
