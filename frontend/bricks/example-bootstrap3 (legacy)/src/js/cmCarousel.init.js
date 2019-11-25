import { bcSwipe } from "./bcSwipe.shim";
import { addNodeDecoratorByData } from "@coremedia/js-node-decoration-service";
import { cmCarousel } from "./cmCarousel";

// @deprecated: use the slick carousel in "@coremedia/slick-carousel" instead.
addNodeDecoratorByData({}, "cm-carousel", $carousel => {
  cmCarousel($carousel);
  bcSwipe($carousel, { threshold: 50 });
});
