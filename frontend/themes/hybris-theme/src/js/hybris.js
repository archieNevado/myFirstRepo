import $ from "jquery";
import * as utils from "@coremedia/brick-utils";

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function() {
  const $document = $(document);

  // to load initially hidden images in tabs
  $(".tabs-list a").on("click", function() {
    $document.trigger(utils.EVENT_LAYOUT_CHANGED);
  });

  utils.log("Welcome to CoreMedia Hybris Integration");
});
