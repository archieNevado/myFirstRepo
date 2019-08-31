import $ from "jquery";
import { addNodeDecorator } from "@coremedia/brick-node-decoration-service";
import { findAndSelf } from "@coremedia/brick-utils";
import * as popup from "./popup";

addNodeDecorator(function($target) {
  const identifier = "cm-popup-control";
  const classButtonActive = "cm-popup-button--active";
  const baseConfig = {
    button: undefined,
    popup: undefined,
  };

  const selector = "[data-" + identifier + "]";
  findAndSelf($target, selector).each(function() {
    const $this = $(this);
    const config = $.extend(baseConfig, $this.data(identifier));

    if (config.button !== undefined && config.popup !== undefined) {
      const $button = $this.find(config.button);
      const $popup = $this.find(config.popup);

      // bind button state to popup state
      $popup.on(popup.EVENT_POPUP_CHANGED, function(event, opened) {
        if (opened) {
          $button.addClass(classButtonActive);
        } else {
          $button.removeClass(classButtonActive);
        }
      });
      $button.on("click", function() {
        // check if popup control is not disabled
        if (!$.extend({ disabled: false }, $this.data(identifier)).disabled) {
          // Toggle popup state
          popup.toggle($popup);
          return false;
        }
      });
    }
  });
});

// close all popups if clicked outside popup or ESC is pressed
addNodeDecorator(function($target) {
  const identifierPopup = ".cm-popup";
  const $body = findAndSelf($target, "body");

  //outside
  $body.on("click", function(event) {
    if ($(event.target).closest(identifierPopup).length === 0) {
      popup.close($body.find(identifierPopup));
    }
  });
  // ESC
  $body.on("keydown", function(event) {
    if (event.keyCode === 27) {
      popup.close($body.find(identifierPopup));
    }
  });
});
