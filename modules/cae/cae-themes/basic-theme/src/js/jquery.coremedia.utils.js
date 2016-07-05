(function ($) {

  /**
   * Same as jQuery.find but also includes the target(s) on which findAndSelf was called.
   *
   * @param selector
   * @returns {jQuery}
   */
  $.fn.findAndSelf = function (selector) {
    return this.filter(selector).add(this.find(selector));
  };

  /**
   * Delays the callback execution for at least 200ms. If during this period another event
   * of the same type occurs then the execution is delayed for another 200ms.
   * @param eventType {string} the type of the event, e.g. "resize"
   * @param data {*} data passed to the event object of the callback
   * @param callback {function(Event)} the function being executed when the event occurs
   */
  $.fn.onDelayed = function(eventType, data, callback) {

    // debouncing function from John Hann
    // http://unscriptable.com/index.php/2009/03/20/debouncing-javascript-methods/
    var debounce = function (func, threshold, execAsap) {
      var timeout;

      return function debounced() {
        var obj = this, args = arguments;

        function delayed() {
          if (!execAsap) {
            func.apply(obj, args);
          }
          timeout = null;
        }

        if (timeout) {
          clearTimeout(timeout);
        }
        else if (execAsap) {
          func.apply(obj, args);
        }

        timeout = setTimeout(delayed, threshold || 200);
      };
    };

    $(this).on(eventType, data, debounce(callback));
  };

  /**
   * Finds relative to $parent or in whole DOM based on existence of ">" in selector.
   *
   * @param selector the selector
   * @returns {jQuery}
   */
  $.fn.findRelativeOrAbsolute = function (selector) {
    if (typeof selector === typeof "string" && (/^\s*[>|\+|~]/).test(selector)) {
      return this.find(selector);
    }
    return $(selector);
  };

  /**
   * Add pseudo for selection by data attribute managed by jQuery (not equal to search for [data-...])
   */
  $.extend($.expr[":"], {
    data: $.expr.createPseudo ?
            $.expr.createPseudo(function( dataName ) {
              return function( elem ) {
                return !!$.data( elem, dataName );
              };
            }) :
      // support: jQuery <1.8
            function( elem, i, match ) {
              return !!$.data( elem, match[ 3 ] );
            }
  });

  /*** BEM helpers */

  var getBEMElementClassName = function (bemBlock, bemElement) {
    return bemBlock + "__" + bemElement;
  };

  var getBEMModifierClassName = function (bemBlock, bemModifier) {
    return bemBlock + "--" + bemModifier;
  };

  $.fn.findBEMElement = function (bemBlock, bemElement) {
    return this.find("." + getBEMElementClassName(bemBlock, bemElement));
  };

  $.fn.addBEMModifier = function (bemBlock, bemModifier) {
    return this.addClass(getBEMModifierClassName(bemBlock, bemModifier));
  };

  $.fn.removeBEMModifier = function (bemBlock, bemModifier) {
    return this.removeClass(getBEMModifierClassName(bemBlock, bemModifier));
  };

})(coremedia.blueprint.$);
