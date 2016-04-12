/**
 * Smart Resize Plugin
 *
 * Fires the resize event only after a treshhold of 200ms
 * see: http://www.paulirish.com/2009/throttled-smartresize-jquery-event-handler/  *
 *
 * Version 1.2
 * Copyright (c) 2016 CoreMedia AG
 *
 * Usage:
 * $(window).smartresize(function(){
 *   // code that takes it easy...
 * });
 *
 */

/*! Smart Resize Plugin | Copyright (c) 2016 CoreMedia AG */
;(function ($) {
  "use strict";

  $.fn.smartresize = function(trigger) {
    // debouncing function from John Hann
    // http://unscriptable.com/index.php/2009/03/20/debouncing-javascript-methods/
    var debounce = function (func, threshold, execAsap) {
      var timeout;

      return function debounced() {
        var obj = this;

        function delayed() {
          if (!execAsap) {
            func.apply(obj, arguments);
          }
          timeout = null;
        }

        if (timeout) {
          clearTimeout(timeout);
        }
        else if (execAsap) {
          func.apply(obj, arguments);
        }

        timeout = setTimeout(delayed, threshold || 200);
      };
    };
    return trigger ? this.bind('resize', debounce(trigger)) : this.trigger('smartresize');
  };
})(coremedia.blueprint.$ || jQuery);
