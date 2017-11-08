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

module.exports = function (url, prev, done) {
  const prefixPattern = /^~/;
  const excludePattern = /\?exclude$/;
  const importedFromPath = path.dirname(prev);
  const originalUrl = url;

  let result = nodeSass.types.NULL;
  // Create an exclude list if it doesn't exist
  if (!this._excludeList) {
    this._excludeList = {};
  }

  const containsExclude = excludePattern.test(url);
  if (containsExclude) {
    url = url.replace(excludePattern, "");
    result = {
      "filename": url
    };
  }

  if (prefixPattern.test(url)) {
    const relativePathToNpmRoot = path.relative(importedFromPath, process.cwd());
    const relativePathToNodeModules = path.join(relativePathToNpmRoot, "node_modules");
    url = path.join(relativePathToNodeModules, url.replace(prefixPattern, ""));
  }

  const absolutePath = path.resolve(importedFromPath, url);
  const resolvedAbsolutePath = resolveScss(absolutePath);

  if (containsExclude) {
    // check if exclude could be resolved, otherwise trigger error:
    if (!resolvedAbsolutePath) {
      done(new Error(`Could not resolve url: "${originalUrl}" which was marked for exclude.`));
      return;
    }
    this._excludeList[resolvedAbsolutePath] = true;
  }

  if (resolvedAbsolutePath && resolvedAbsolutePath in this._excludeList) {
    done({
      'contents': '',
      'filename': 'excluding:' + resolvedAbsolutePath
    });
  } else {
    done(result);
  }
};
