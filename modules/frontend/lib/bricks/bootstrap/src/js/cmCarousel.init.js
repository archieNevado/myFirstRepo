import { bcSwipe } from "./bcSwipe.shim";
import { addNodeDecoratorByData } from "@coremedia/js-node-decoration-service";
import { cmCarousel } from "./cmCarousel";

addNodeDecoratorByData({}, "cm-carousel", $carousel => {
  cmCarousel($carousel);
  bcSwipe($carousel, { threshold: 50 });
});
