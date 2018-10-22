/**
 * Bootstrap Carousel Swipe v1.1
 *
 * jQuery plugin to enable swipe gestures on Bootstrap 3 carousels.
 * Examples and documentation: https://github.com/maaaaark/bcSwipe
 *
 * Licensed under the MIT license.
 */
(function($) {
  "use strict";

  $.fn.bcSwipe = function(settings) {
    let config = { threshold: 50 };
    if (settings) {
      $.extend(config, settings);
    }

    let supportsTouch = false;

    if ('ontouchstart' in window) // iOS & android
      supportsTouch = true;
    else if(window.navigator.msPointerEnabled) // Win8
      supportsTouch = true;
    else if ('ontouchstart' in document.documentElement) // Controversal way to check touch support
      supportsTouch = true;

    this.each(function() {
      let stillMoving = false;
      let start;

      if (supportsTouch){
        this.addEventListener('touchstart', onTouchStart, false);
      }

      function onTouchStart(e) {
        if (e.touches.length == 1) {
          start = e.touches[0].pageX;
          stillMoving = true;
          this.addEventListener('touchmove', onTouchMove, false);
        }
      }

      function onTouchMove(e) {
        if (stillMoving) {
          let x = e.touches[0].pageX;
          let difference = start - x;
          if (Math.abs(difference) >= config.threshold) {
            cancelTouch($(this));
            if (difference > 0) {
              $(this).carousel('next');
            }
            else {
              $(this).carousel('prev');
            }
          }
        }
      }

      function cancelTouch($this) {
        $this.off('touchmove');
        start = null;
        stillMoving = false;
      }
    });

    return this;
  };
})(jQuery);
