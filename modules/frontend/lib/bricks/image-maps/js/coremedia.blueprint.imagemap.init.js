/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  module.$ = module.$ || jQuery;
  return module;
}(coremedia.blueprint || {}));

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
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
