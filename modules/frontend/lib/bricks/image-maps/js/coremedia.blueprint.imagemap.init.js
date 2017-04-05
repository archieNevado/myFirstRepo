/**
 * CoreMedia namespace
 * @namespace coremedia
 * @ignore
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));

/**
 * CoreMedia Blueprint namespace
 * @namespace "coremedia.blueprint"
 * @ignore
 */
coremedia.blueprint = (function (module) {
  /*global jQuery*/
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

/**
 * --- DOCUMENT READY ---
 */
coremedia.blueprint.$(function () {
  "use strict";

  var $ = coremedia.blueprint.$;

  // initializes imagemaps
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    $target.findAndSelf(".cm-imagemap").each(function () {
      coremedia.blueprint.imagemap.init($(this));
    });
  });
});
