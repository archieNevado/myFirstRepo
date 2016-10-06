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
