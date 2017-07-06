/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  /*global jQuery*/
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {
  "use strict";

  var $ = coremedia.blueprint.$;

  // to load initially hidden images in tabs
  $(".tabs-list a").on("click", function () {
    $(".js-tabs .cm-image--responsive").responsiveImages();
  });

  coremedia.blueprint.logger.log("Welcome to CoreMedia Hybris Integration");
});
