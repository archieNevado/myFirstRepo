const closestPackage = require("closest-package");
const fs = require("fs");
const path = require("path");
const nodeSass = require("node-sass");

const {
  workspace: { DEFAULT_VARIANT, getThemeConfig, getIsSmartImportModuleFor },
  packages,
  dependencies: {
    getDependencies,
    getDependentsFirstLoadOrder,
    getFlattenedDependencies,
  },
} = require("@coremedia/tool-utils");
const { resolveScss } = require("./utils");

const REGEX = /(~(.+))?\?smart-import-(variables|partials)/;

const SASS_PATHS = ["src/sass", "src"];

const themeConfig = getThemeConfig();

function getSmartImportPath(dependency, varsOrPartials, prev) {
  const candidates = SASS_PATHS.map(sassPath =>
    path.join(dependency.getName(), sassPath, varsOrPartials)
  )
    .map(url => `~${url.replace(/\\/g, "/")}`)
    .filter(url => fs.existsSync(resolveScss(url, prev)));
  return candidates.length > 0 ? candidates[0] : null;
}

function getVariant(prev) {
  const basename = path.basename(prev, path.extname(prev));
  const isEntryScss =
    path.resolve(path.dirname(prev)) ===
    path.resolve(themeConfig.srcPath, "sass");
  const isPartial = basename.indexOf("_") === 0;
  // only entry scss which are not partials can be variants
  if (!isEntryScss || isPartial) {
    return null;
  }
  return basename === getThemeConfig().name ? DEFAULT_VARIANT : basename;
}

module.exports = function(url, prev, done) {
  const match = REGEX.exec(url);
  if (match && match.length > 0) {
    const [, , providedPackageName, varsOrPartials] = match;

    // either package name is provided explicitly or we need to evaluate it from prev
    let packageName;
    let pkgJson;
    if (providedPackageName) {
      packageName = providedPackageName;
      pkgJson = packages.getFilePathByPackageName(providedPackageName);
    } else {
      pkgJson = closestPackage.sync(prev);
      packageName = packages.getJsonByFilePath(pkgJson).name;
    }

    const variant = getVariant(prev);

    const dependencies = getDependencies(
      pkgJson,
      getIsSmartImportModuleFor(null)
    ).filter(getIsSmartImportModuleFor(variant));

    let dependenciesToImport;

    if (varsOrPartials === "variables") {
      // variables need dependents first load order
      const dependentsFirstLoadOrder = getDependentsFirstLoadOrder(
        dependencies,
        packageName
      );
      dependenciesToImport = dependentsFirstLoadOrder[packageName] || [];
    } else {
      // for partials the direct load order specified through the dependency hierarchy is sufficient (which are our dependencies, we just don't flatten them)
      dependenciesToImport = dependencies;

      // filter partials (=> css generating code) that would be included twice.
      // we always assume that the variant is loaded in addition to the default.
      if (variant && variant !== DEFAULT_VARIANT) {
        this._whiteList = getFlattenedDependencies(
          pkgJson,
          getIsSmartImportModuleFor(null)
        )
          .filter(getIsSmartImportModuleFor(variant))
          .map(dependency => dependency.getName());
      }

      // filter by white list (only applies for partials inside a variant)
      if (this._whiteList) {
        dependenciesToImport = dependenciesToImport.filter(dependency =>
          this._whiteList.includes(dependency.getName())
        );
      }
    }

    // Get scss paths of the dependent bricks
    const sassPaths = dependenciesToImport.map(dependency => {
      const existingFile = getSmartImportPath(
        dependency,
        varsOrPartials,
        pkgJson
      );
      return existingFile
        ? existingFile
        : `~${dependency.getName()}?smart-import-${varsOrPartials}`;
    });

    const scssContent = sassPaths
      .map(sassPath => `@import "${sassPath}";`)
      .join("\n");

    done({
      contents: scssContent,
      // also rewrite filename otherwise the loop protection might abort the scss compilation
      file: path.resolve(
        path.dirname(pkgJson),
        "smart-import-" + varsOrPartials + ".scss"
      ),
    });
  } else {
    done(nodeSass.types.NULL);
  }
};
