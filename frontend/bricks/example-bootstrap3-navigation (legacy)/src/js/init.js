import $ from "jquery";
import "bootstrap-sass/assets/javascripts/bootstrap/transition";
import "bootstrap-sass/assets/javascripts/bootstrap/collapse";
import "bootstrap-sass/assets/javascripts/bootstrap/dropdown";

import * as nodeDecorationService from "@coremedia/brick-node-decoration-service";
import * as deviceDetector from "@coremedia/brick-device-detector";

const NAVIGATION_ID = "navbar";
const NAVIGATION_ID_SELECTOR = "#";
const NAVIGATION_PREFIX = "cm-navigation";
const NAVIGATION_SELECTOR = ".";

const NAVIGATION_ROOT_BLOCK = NAVIGATION_PREFIX + "-item-depth-0";
const NAVIGATION_ROOT_ELEMENT =
  NAVIGATION_SELECTOR + NAVIGATION_ROOT_BLOCK + "__list";
const NAVIGATION_ROOT_MODIFIER = NAVIGATION_PREFIX + "--hovered";

const NAVIGATION_ENTRY_SELECTOR_CLASS =
  NAVIGATION_SELECTOR + NAVIGATION_PREFIX + "-item-depth-1";

$(function() {
  // touch device detection
  const deviceAgent = navigator.userAgent.toLowerCase();
  let isTouchDevice =
    deviceAgent.match(/(iphone|ipod|ipad)/) ||
    deviceAgent.match(/(android)/) ||
    deviceAgent.match(/(iemobile)/) ||
    deviceAgent.match(/iphone/i) ||
    deviceAgent.match(/ipad/i) ||
    deviceAgent.match(/ipod/i) ||
    deviceAgent.match(/blackberry/i) ||
    deviceAgent.match(/bada/i);

  function isMobileOrTablet() {
    return deviceDetector.getLastDevice().type !== "desktop";
  }

  nodeDecorationService.addNodeDecoratorBySelector(
    NAVIGATION_ROOT_ELEMENT,
    function($target) {
      let $navigationEntryBlock = $target.find(NAVIGATION_ENTRY_SELECTOR_CLASS);

      $navigationEntryBlock.mouseover(function() {
        $target.addClass(NAVIGATION_ROOT_MODIFIER);
      });
      $navigationEntryBlock.mouseout(function() {
        $target.removeClass(NAVIGATION_ROOT_MODIFIER);
      });

      // Previously hovered menus could still be visible since they won't disappear until the end of their transition.
      // To make sure that only one menu is visible, we need to set the opacity of all other menus to 0.
      $navigationEntryBlock.mouseover(function() {
        $navigationEntryBlock.not(this).each(function() {
          const $this = $(this);
          $this.find("ul.cm-navigation-item__list").css("opacity", 0);
          $this.css("border-bottom-width", 0);
        });

        $(this)
          .find("ul.cm-navigation-item__list")
          .css("opacity", 1);
        $(this).css("border-bottom-width", 4);
      });

      $navigationEntryBlock.on("click", function(e) {
        // prevent further code from beeing executed if a sublist of the list is clicked
        if (e.target.parentNode !== this) return;
        // ignore click on touch devices. we don't want to trigger the link, just display the subnavigation
        if (isTouchDevice && !isMobileOrTablet()) {
          e.preventDefault();
        }
      });

      // fixing navbar
      let $navbar = $(NAVIGATION_ID_SELECTOR + NAVIGATION_ID);

      $navbar.on("show.bs.collapse", function() {
        $("body").addClass("fixed");
      });

      $navbar.on("hidden.bs.collapse", function() {
        $("body").removeClass("fixed");
      });
    }
  );
});
