import $ from "jquery";
import objectFitImages from "object-fit-images";
import objectFitVideos from "object-fit-videos";
import { debounce } from "@coremedia/js-utils";
import * as logger from "@coremedia/js-logger";
import { EVENT_LAYOUT_CHANGED } from "./basic";

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function() {
  const $window = $(window);
  const $document = $(document);

  // trigger polyfill for object fit on images and videos (will automatically detect newly attached DOM elements)
  objectFitImages();
  objectFitVideos();

  // trigger layout changed event if the size of the window changes using smartresize plugin
  $window.on(
    "resize",
    {},
    debounce(function() {
      logger.log("Window resized");
      $document.trigger(EVENT_LAYOUT_CHANGED);
    })
  );
});
