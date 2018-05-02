import $ from "jquery";
import * as deviceDetector from "@coremedia/js-device-detector";

$(function() {
  /* --- Touch detection --- */
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

  /* --- Navigation --- */
  const $navigationEntry = $(".cm-navigation-item-depth-1");
  const $navigationRoot = $(".cm-navigation-item-depth-0__list");

  function isMobileOrTablet() {
    return deviceDetector.getLastDevice().type !== "desktop";
  }

  $navigationEntry.mouseover(function() {
    $navigationRoot.addClass("cm-navigation--hovered");
  });
  $navigationEntry.mouseout(function() {
    $navigationRoot.removeClass("cm-navigation--hovered");
  });
  $navigationEntry.on("click", function(e) {
    // prevent further code from beeing executed if a sublist of the list is clicked
    if (e.target.parentNode !== this) return;
    // ignore click on touch devices. we don't want to trigger the link, just display the subnavigation
    if (isTouchDevice && !isMobileOrTablet()) {
      e.preventDefault();
    }
  });

  // Previously hovered menus could still be visible since they won't disappear until the end of their transition.
  // To make sure that only one menu is visible, we need to set the opacity of all other menus to 0.
  $navigationEntry.mouseover(function() {
    $navigationEntry.not(this).each(function() {
      const $this = $(this);
      $this.find("ul.cm-navigation-item__list").css("opacity", 0);
      $this.css("border-bottom-width", 0);
    });
    $(this)
      .find("ul.cm-navigation-item__list")
      .css("opacity", 1);
    $(this).css("border-bottom-width", 4);
  });
});
