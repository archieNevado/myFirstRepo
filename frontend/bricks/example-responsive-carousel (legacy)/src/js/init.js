import { addNodeDecoratorByData } from "@coremedia/js-node-decoration-service";
import responsiveCarousel from "./responsiveCarousel";

// all elements with the attribute "data-cm-responsive-carousel" will be used
const identifier = "cm-responsive-carousel";
// default config in responsiveCarousel is used
const defaultConfig = {};
// and finally add it to the nodeDecorator (which handles dom ready, )
addNodeDecoratorByData(defaultConfig, identifier, responsiveCarousel);
