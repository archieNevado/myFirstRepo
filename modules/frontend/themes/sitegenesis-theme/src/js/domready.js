import $ from "jquery";
import * as logger from "@coremedia/js-logger";

$(function() {
  "use strict";

  if (document.querySelector("[data-cm-developer-mode]")) {
    logger.setLevel(logger.LEVEL.ALL);
  }
  logger.log("Welcome to CoreMedia Salesforce Commerce Cloud Integration");
});
