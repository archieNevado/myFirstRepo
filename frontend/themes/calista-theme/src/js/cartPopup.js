import $ from "jquery";
import { getLastDevice } from "@coremedia/brick-device-detector";
import { refreshFragment } from "@coremedia/brick-dynamic-include";
import { addNodeDecoratorBySelector } from "@coremedia/brick-node-decoration-service";
import { EVENT_CART_UPDATED } from "@coremedia-examples/brick-cart/src/js";

const $document = $(document);
const MOBILE_DEVICE_TYPE = "mobile";

addNodeDecoratorBySelector("[data-cm-cart-control]", $target => {
  const $button = $target.find(".cm-cart-icon");
  const $cartPopup = $target.find(".cm-cart-popup");
  $button.on("click", event => {
    if (getLastDevice().type !== MOBILE_DEVICE_TYPE) {
      event.preventDefault();
      $cartPopup.toggleClass("cm-cart-popup--active");
    }
  });
});

$document.on(EVENT_CART_UPDATED, () => {
  $("[data-cm-cart-control][data-cm-refreshable-fragment]").each(function() {
    refreshFragment($(this));
  });
});
