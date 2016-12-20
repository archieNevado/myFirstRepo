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

// --- DOCUMENT READY ---
coremedia.blueprint.$(function () {
  "use strict";

  var $ = coremedia.blueprint.$;

  /**
   * Initialize magnific popup for video popup.
   *
   * @name video popup
   * @summary Initialize magnific popup for video popup.
   * @example
   * ```html
   * <a href="..." data-cm-popup="" class="cm-claim__popup-opener"> ...</a>
   * ```
   * CoreMedia will automatically find and initialize a video popup opener for any element that contains the
   * data attribute. Auto-initialization is not supported for a video popup opener that is added to the DOM after
   * jQuery's ready event has fired.
   */
  $('[data-cm-popup]').magnificPopup({
    type: 'iframe',
    midClick: true,
    mainClass: 'cm-popup',
    removalDelay: 160,
    preloader: false,
    fixedContentPos: false,
    /* fix CMS-4839, html5 video as iframe not working in IE, switiching to inline popup */
    callbacks: {
      elementParse: function(item) {
        if(item.src.indexOf("resource\/blob") > -1) {
          var url = item.src;
          item.type = "inline";
          item.src = '<div class="cm-popup--scaler"><button title="Close (Esc)" type="button" class="mfp-close">×</button>' +
                  '<video src="'+url+'" class="cm-video cm-video--html5" poster="" autoplay="autoplay" controls="controls"></video>' +
                  '</div>';
        }
      }
    }
  });
});
