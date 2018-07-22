const closestPackage = require("closest-package");
const fs = require("fs");
const glob = require("glob");
const path = require("path");

class ViewRepositoryMapping {
  constructor(name, acceptResource) {
    this.name = name;
    this.acceptResource = acceptResource || (() => true);
  }
}

class ViewRepositoryPlugin {
  constructor(config) {
    this._templateGlobPattern = config.templateGlobPattern || "**/*.*";
    this._targetPath = config.targetPath;
    this._mappings = config.mappings || [];
    if (
      !this._mappings.every(mapping => mapping instanceof ViewRepositoryMapping)
    ) {
      throw new Error(
        "All mappings have to be an instance of ViewRepositoryMapping!"
      );
    }
  }

  getEntry() {
    return (
      require.resolve("./loader") + `?pattern=${this._templateGlobPattern}!`
    );
  }

  apply(compiler) {
    this._outputPath = compiler.options.output.path;
    compiler.plugin("emit", (compilation, callback) => {
      const assets = compilation.assets;
      const newlyCreatedAssets = Object.keys(assets).map(
        key => assets[key].existsAt
      );

      glob
        .sync("**/*.*", {
          cwd: this._targetPath,
          nodir: true,
        })
        .map(globResult => path.join(this._targetPath, globResult))
        .filter(path => !newlyCreatedAssets.includes(path))
        .forEach(fs.unlinkSync);
      callback();
    });
  }

  getLoaderConfig() {
    return {
      loader: "file-loader",
      options: {
        name: resourcePath => {
          const packageJsonPath = closestPackage.sync(resourcePath);
          const packageJson = require(packageJsonPath);

          /**
           * @type {ViewRepositoryMapping}
           */
          const mapping = this._mappings.find(mapping =>
            mapping.acceptResource(resourcePath, packageJsonPath)
          );
          if (!mapping) {
            throw new Error(
              `No view repository found for template "${resourcePath}.`
            );
          }
          // freemarker lib or not
          let middleSegment;
          const relativePathFromPackage = path.relative(
            path.dirname(packageJsonPath),
            resourcePath
          );
          const relativeTemplatesPath = path.join("src", "templates");
          if (relativePathFromPackage.indexOf(relativeTemplatesPath) === 0) {
            middleSegment = relativePathFromPackage.substr(
              relativeTemplatesPath.length
            );
          } else {
            middleSegment = path.join(
              "freemarkerLibs",
              packageJson.name,
              path.basename(relativePathFromPackage)
            );
          }

          return path.join(mapping.name, middleSegment);
        },
        outputPath: url =>
          path.join(path.relative(this._outputPath, this._targetPath), url),
      },
    };
  }
}

module.exports = { ViewRepositoryPlugin, ViewRepositoryMapping };
