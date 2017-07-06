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

/**
 * Equal Heights Plugin
 *
 * Equalize the heights of elements. Great for columns or any elements
 * that need to be the same size (floats, etc).
 *
 * Version 1.1
 * Copyright (c) Rob Glazebrook (cssnewbie.com) and CoreMedia AG
 *
 * Usage:
 * $(object).equalHeights();
 *
 */

/*! Equal Heights Plugin | Copyright (c) CoreMedia AG */
(function ($) {
  "use strict";

  $.fn.equalHeights = function () {
    this.each(function () {
      $(this).height('auto');
    });

    var tallest = 0;
    this.each(function () {
      if ($(this).height() > tallest) {
        tallest = $(this).height();
      }
    });
    return this.each(function () {
      $(this).height(tallest);
    });
  }
})(jQuery || coremedia.blueprint.$);
