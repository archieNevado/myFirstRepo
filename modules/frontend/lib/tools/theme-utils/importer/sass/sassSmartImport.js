const closestPackage = require("closest-package");
const fs = require("fs");
const path = require("path");
const nodeSass = require("node-sass");

const {
  DEFAULT_VARIANT,
  getThemeConfig,
  getIsSmartImportModuleFor,
  getInstallationPath,
} = require("@coremedia/tool-utils/workspace");
const {
  getFlattenedDependencies,
} = require("@coremedia/tool-utils/dependencies");
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
      pkgJson = closestPackage.sync(getInstallationPath(packageName));
    } else {
      pkgJson = closestPackage.sync(prev);
      packageName = require(pkgJson).name;
    }

    const variant = getVariant(prev);

    // Get flattendDependencies
    let flattenedDirectDependencies = getFlattenedDependencies(
      pkgJson,
      (dependency, parent) =>
        parent.getName() === packageName &&
        getIsSmartImportModuleFor(variant)(dependency)
    );

    // variables need to be imports in reverse order due to !default
    if (varsOrPartials === "variables") {
      flattenedDirectDependencies = flattenedDirectDependencies.reverse();
    }

    // filter partials (=> css generating code) that would be included twice.
    // we always assume that the variant is loaded in addition to the default.
    if (
      varsOrPartials === "partials" &&
      variant &&
      variant !== DEFAULT_VARIANT
    ) {
      this._whiteList = getFlattenedDependencies(
        pkgJson,
        getIsSmartImportModuleFor(variant)
      ).map(dependency => dependency.getName());
    }

    // filter by white list (only applies for partials inside a variant)
    if (this._whiteList) {
      flattenedDirectDependencies = flattenedDirectDependencies.filter(
        dependency => this._whiteList.includes(dependency.getName())
      );
    }

    // Get scss paths of the dependent bricks
    const sassPaths = flattenedDirectDependencies.map(dependency => {
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
