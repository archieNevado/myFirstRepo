const closestPackage = require("closest-package");
const fs = require("fs");
const glob = require("glob");
const path = require("path");

const FTL_REFERENCE_PATTERN = /<#(import|include)\s+"([^"]+)"([^>]*)>/g;

function resolveFreemarkerGlob(pattern, baseDir) {
  let currentDir = baseDir;
  while (currentDir) {
    const foundFiles = glob.sync(pattern, {
      cwd: currentDir,
    });
    if (foundFiles.length > 0) {
      return path.join(currentDir, foundFiles[0]);
    }
    const lastDir = currentDir;
    currentDir = path.join(currentDir, "../");
    if (lastDir === currentDir) {
      return pattern;
    }
  }
}

module.exports = function loader(content) {
  // cannot be cached because a plugin configuration in clean.js clears the templates that have not been provided by the last build
  this.cacheable(false);
  const sourcePath = this.resourcePath;
  const sourceDirectory = path.dirname(sourcePath);
  return content.replace(
    FTL_REFERENCE_PATTERN,
    (wholeExpression, directive, ftlPath, tail) => {
      // freemarker imports/includes may contain a single asterisk to indicate that any folder
      const hasAsteriskPattern = ftlPath.includes("*");
      const resolvedPathToLib = hasAsteriskPattern
        ? resolveFreemarkerGlob(ftlPath, sourceDirectory)
        : path.resolve(sourceDirectory, ftlPath);
      if (!fs.existsSync(resolvedPathToLib)) {
        throw new Error(
          `Could not resolve file reference in include/import directive: "${ftlPath}" in source file "${sourcePath}". Searched in "${resolvedPathToLib}"`
        );
      }
      this.loadModule(resolvedPathToLib, err => {
        if (err) {
          throw new Error(
            `Could not import freemarker library: "${resolvedPathToLib}" in source file "${sourcePath}".\n\n${err}`
          );
        }
      });

      // TODO: should be independent of workspace knowledge...
      // this could be achieved by evaluating the result of the prior loadModule call
      const packageJsonPath = closestPackage.sync(resolvedPathToLib);
      const packageJson = require(packageJsonPath);
      const coremediaConfig = packageJson.coremedia || {};
      const partOfTheme = coremediaConfig.type === "theme";
      const middleSegment = partOfTheme ? coremediaConfig.name : "bricks";
      const transformedPath = `*/${middleSegment}/freemarkerLibs/${
        packageJson.name
      }/${path.basename(resolvedPathToLib)}`;

      return `<#${directive} ${JSON.stringify(transformedPath)}${tail}>`;
    }
  );
};
