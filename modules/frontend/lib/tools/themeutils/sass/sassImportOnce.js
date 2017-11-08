const fs = require("fs");
const path = require("path");
const nodeSass = require("node-sass");
const SassNameExpander = require("./SassNameExpander");

function resolveScss(url) {
  const nameExpander = new SassNameExpander(path.basename(url));
  nameExpander.addLocation(path.dirname(url));
  const possibleFiles = nameExpander.files.values();
  let result;

  while (result = possibleFiles.next().value) {
    if (fs.existsSync(result)) {
      break;
    }
  }

  return result;
}

module.exports = function(url, prev, done) {
  const prefixPattern = /^~/;
  const importedFromPath = path.dirname(prev);

  // Create an import cache if it doesn't exist
  if (!this._importOnceCache) {
    this._importOnceCache = {};
  }

  if (prefixPattern.test(url)) {
    const relativePathToNpmRoot = path.relative(importedFromPath, process.cwd());
    const relativePathToNodeModules = path.join(relativePathToNpmRoot, "node_modules");
    url = path.join(relativePathToNodeModules, url.replace(prefixPattern, ""));
  }

  const absolutePath = path.resolve(importedFromPath, url);
  const resolvedAbsolutePath = resolveScss(absolutePath);
  if (resolvedAbsolutePath && resolvedAbsolutePath in this._importOnceCache) {
    done({
      'contents': '',
      'filename': 'already-imported:' + resolvedAbsolutePath
    });
  } else {
    this._importOnceCache[resolvedAbsolutePath] = true;
    done(nodeSass.types.NULL);
  }
};
