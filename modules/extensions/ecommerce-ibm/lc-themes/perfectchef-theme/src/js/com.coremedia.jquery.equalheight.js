/**
 * Equal Heights Plugin
 * Equalize the heights of elements. Great for columns or any elements
 * that need to be the same size (floats, etc).
 *
 * Version 1.0.2
 * Updated 06/13/2013
 *
 * Copyright (c) 2008 Rob Glazebrook (cssnewbie.com) and CoreMedia AG
 *
 * Usage: $(object).equalHeights();
 *
 */
(function($) {
  $.fn.equalHeights = function() {
    this.each(function() {
      $(this).height('auto');
    });

    var tallest = 0;
    this.each(function() {
      if($(this).height() > tallest) {
        tallest = $(this).height();
      }
    });
    return this.each(function() {
      $(this).height(tallest);
    });
  }
})(coremedia.blueprint.$ || jQuery);
