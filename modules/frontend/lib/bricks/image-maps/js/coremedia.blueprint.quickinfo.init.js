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
 * @namespace coremedia.blueprint
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

  // handle quickinfos
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-quickinfo", function ($target) {
    coremedia.blueprint.quickInfo.closeQuickInfo($target);
  });

  // close quickinfo on Esc key
  coremedia.blueprint.$('body').on("keydown", function (event) {
    if (event.keyCode === 27) {
      coremedia.blueprint.quickInfo.hide(coremedia.blueprint.$('.cm-quickinfo--active'));
    }
  });

  // handle quickinfo buttons
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({
    target: undefined,
    group: undefined
  }, "cm-button--quickinfo", function ($button, config) {
    coremedia.blueprint.quickInfo.toggleQuickInfo($button, config);
  });
});
