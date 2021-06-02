const path = require("path");
const { RawSource } = require("webpack-sources");

const NAME = "CoreMedia Chunk Mapping Plugin";
const GET_CHUNK_PATH_FUNCTION_NAME = "__cmGetChunkPath";

class CoreMediaChunkMappingPlugin {
  constructor(options) {
    this.options = Object.assign(
      {
        chunkMappingPath: undefined,
      },
      options
    );
    if (!this.options.chunkMappingPath) {
      throw new Error("No path for the chunkMapping was specified!");
    }
  }

  /**
   * When upgrading to Webpack 5: Replace this with https://webpack.js.org/configuration/output/#outputchunkfilename
   */
  _applyMainTemplate(mainTemplate) {
    // tapable/lib/Hook.js
    // use stage 1 to ensure this executes after webpack/lib/web/JsonpMainTemplatePlugin.js
    mainTemplate.hooks.localVars.tap({ name: NAME, stage: 1 }, (source) => {
      if (!source.includes("function jsonpScriptSrc")) {
        // no chunks in use
        return source;
      }

      const modSource = source.replace(
        "function jsonpScriptSrc",
        "function webpackJsonpScriptSrc"
      );
      return `${modSource}

function jsonpScriptSrc(chunkId) {
  var userGetScriptSrc = window["${GET_CHUNK_PATH_FUNCTION_NAME}"];
  var src = webpackJsonpScriptSrc(chunkId);
  return (userGetScriptSrc && userGetScriptSrc(chunkId, ${mainTemplate.requireFn}.p, src)) || src;
}
`;
    });
  }

  apply(compiler) {
    compiler.hooks.thisCompilation.tap(NAME, (compilation) => {
      this._applyMainTemplate(compilation.mainTemplate);
    });
    compiler.hooks.afterCompile.tap(NAME, (compilation) => {
      const chunkPathById = compilation.chunks
        .filter(
          (chunk) =>
            chunk.entryModule === undefined && !chunk.preventIntegration
        )
        .reduce(
          (chunkPathById, chunk) =>
            Object.assign(chunkPathById, {
              [chunk.id]: chunk.files[0],
            }),
          {}
        );

      // do not generate chunk mapping if no chunks are being used...
      if (Object.keys(chunkPathById).length === 0) {
        return;
      }

      const chunkMappingDirPath = path.dirname(this.options.chunkMappingPath);

      // the theme import will replace url statements with the correct path, path must be relative to the js file
      // containing the url.
      compilation.assets[
        this.options.chunkMappingPath
      ] = new RawSource(`(function () {
  function getOrigin(anchor) {
    return anchor.protocol + "//" + anchor.host;
  }

  function getAbsoluteUrl(url, baseUrl) {
    // catch error if URL API is not supported (URL still exists in that case)
    try {
      return new URL(url, baseUrl);
    } catch (e) {
      // ignore
    }
    var anchor = document.createElement("a");
    anchor.href = url;
    var anchorBase = document.createElement("a");
    anchorBase.href = baseUrl;
    if (getOrigin(anchor) === getOrigin(anchorBase)) {
      return anchor.href;
    }
    var absoluteUrl = anchorBase.protocol + "//" + anchorBase.host + "/" + anchor.pathname;
    if (anchor.search) {
      absoluteUrl += "?" + anchor.search;
    }
    if (anchor.hash) {
      absoluteUrl += "#" + anchor.hash;
    }
    return absoluteUrl;
  }

  // attempt to find the path of the currently executed script (=this file)
  var currentScript = document.currentScript || Array.prototype.slice.call(document.getElementsByTagName('script'), -1)[0];
  var baseUrl = currentScript ? currentScript.src : window.location.href;
  var parse = function (url) {
    return getAbsoluteUrl(url.substr(4, url.length - 5), baseUrl).toString();
  };

  var chunkPathById = {
${Object.entries(chunkPathById)
  .map(([chunkId, chunkPath]) => {
    return `    ${JSON.stringify(chunkId)}: parse("url(${path.relative(
      chunkMappingDirPath,
      chunkPath
    )})")`;
  })
  .join(",\n")}
  };

  window["${GET_CHUNK_PATH_FUNCTION_NAME}"] = function (chunkId, publicPath, originalSrc) {
    return chunkPathById[chunkId] || originalSrc;
  }
})();
`);
    });
  }
}

module.exports = {
  CoreMediaChunkMappingPlugin,
};
