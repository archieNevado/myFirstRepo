;(function ($) {
  "use strict";

  $.fn.cmCarousel = function () {

    return this.each(function () {

      var $carousel = $(this);

      var data = $carousel.data('cm-carousel');
      // pause the carousel form sliding if needed.
      var pause = Boolean(data.pause) || false;

      $carousel.carousel({
        interval: Number(data.interval) || 5000
      });

      if (pause) {
        $carousel.carousel('pause');
      }

      // EVENT BOOTSTRAP CAROUSEL, see http://getbootstrap.com/javascript/#carousel-events
      $carousel.on('slid.bs.carousel', function () {
        var $theCarousel = $(this);
        var $slides = $theCarousel.find('.item');
        var $activeSlide = $theCarousel.find('.item.active');
        var index = $slides.index($activeSlide);
        var $pagination = $theCarousel.find(".cm-carousel__pagination-index");
        //set pagination
        $pagination.text(String(index + 1));
        //reload responsive image. hidden slides had no image because of height/width=0
        $theCarousel.find(".carousel-inner .cm-image--responsive").responsiveImages();
      });

    });
  };


  $(document).ready(function () {
    $('[data-cm-carousel]').cmCarousel();
  });


})(jQuery || coremedia.blueprint.$);
