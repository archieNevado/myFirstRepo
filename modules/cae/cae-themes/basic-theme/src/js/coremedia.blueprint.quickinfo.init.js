/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
/**
 *  CoreMedia Blueprint Namespace
 */
coremedia.blueprint = (function (module) {
  return module;
}(coremedia.blueprint || {}));

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {

  // handle quickinfos
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-quickinfo", function ($target) {
    coremedia.blueprint.quickInfo.closeQuickInfo($target);
  });


  // handle quickinfo buttons
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({
    target: undefined,
    group: undefined
  }, "cm-button--quickinfo", function ($button, config) {
    coremedia.blueprint.quickInfo.toggleQuickInfo($button, config);
  });
});