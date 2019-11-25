import { addNodeDecoratorByData } from "@coremedia/js-node-decoration-service";
import slickCarousel from "./index";

addNodeDecoratorByData({}, "cm-slick-carousel", ($container, config) => {
  slickCarousel($container, config);
});
