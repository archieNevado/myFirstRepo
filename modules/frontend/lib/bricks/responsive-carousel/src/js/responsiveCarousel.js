import $ from "jquery";
import { log } from "@coremedia/js-logger";
import "slick-carousel-no-font-no-png/slick/slick.js";

/**
 * Generates a responsive carousel for the given carousel and a given config.
 * If the config is not set, defaults will be used.
 *
 * @param carousel
 * @param config, e.g. { "slidesToShowDesktop": 5, "slidesToShowMobile": 2 }
 */
function responsiveCarousel(carousel, config) {
  const $carousel = $(carousel);
  log("Initialize responsiveCarousel", $carousel);

  $carousel.slick({
    slidesToShow: config.slidesToShowDesktop || 5,
    slidesToScroll: config.slidesToShowDesktop || 5,
    responsive: [
      {
        // use same breakpoint as scss variable $cm-responsive-carousel-tablet-breakpoint
        breakpoint: 768,
        settings: {
          arrows: true,
          slidesToScroll: config.slidesToShowMobile || 2,
          slidesToShow: config.slidesToShowMobile || 2,
        },
      },
      {
        // use same breakpoint as scss variable $cm-responsive-carousel-mobile-breakpoint
        breakpoint: 420,
        settings: {
          arrows: false,
          centerMode: true,
          centerPadding: "0px",
          variableWidth: true,
        },
      },
    ],
  });
}

/**
 * default wrapper function to handle dom elements or jQuery selectors
 * @param domElementOrJQueryResult
 * @param config
 */
export default function(domElementOrJQueryResult, config) {
  if (domElementOrJQueryResult instanceof $) {
    $.each(domElementOrJQueryResult, function(index, item) {
      responsiveCarousel(item, config);
    });
  } else {
    responsiveCarousel(domElementOrJQueryResult, config);
  }
}
