import * as quickInfo from "./quickInfo";
import $ from "jquery";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";

$(function () {
  // handle quickinfos
  nodeDecorationService.addNodeDecoratorBySelector(".cm-quickinfo", function ($target) {
    quickInfo.closeQuickInfo($target);
  });

  // close quickinfo on Esc key
  $('body').on("keydown", function (event) {
    if (event.keyCode === 27) {
      quickInfo.hide($('.cm-quickinfo--active'));
    }
  });

  // handle quickinfo buttons
  nodeDecorationService.addNodeDecoratorByData({
    target: undefined,
    group: undefined
  }, "cm-button--quickinfo", function ($button, config) {
    quickInfo.toggleQuickInfo($button, config);
  });
});
