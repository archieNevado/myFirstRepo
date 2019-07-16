import $ from "jquery";

/**
 * @type {number} stores the last navigation width in order die identify if mega-menu width adjustments need to be recalculated.
 */
let lastNavWidth = 0;

/**
 * @type {string} specifies the selector for mega menu for adjustment of its children's widths to fit the whole space
 */
const megaMenuSelector = ".mega-menu > li > ul";

/**
 * @type {string} defines the internal name for the mobile device
 */
export const DEVICE_MOBILE = "mobile";

/**
 * @type {string} defines the internal name of the tablet device
 */
export const DEVICE_TABLET = "tablet";

/**
 * @type {string} defines the internal name of the desktop device
 */
export const DEVICE_DESKTOP = "desktop";

/**
 *
 * @type {string} defines the event type that signals that the pdp's image carousel is fully initialized
 */
export const PDP_ASSET_READY_EVENT = "carouselReady";

/**
 * set width of each item in the mega-menu depending to screen-size
 */
export function setMegaMenuItemsWidth() {
  const $navMegaMenu = $(megaMenuSelector);
  // detect width of navigation
  const currentNavWidth = $navMegaMenu.width();
  // set width only if changed
  if (currentNavWidth != lastNavWidth) {
    // detect how much space each navigation item has keeping taking border of 1px for each element into account
    const $children = $navMegaMenu.children("li");
    const $childrenExceptLast = $children.not(":last");
    const $lastChild = $children.last();
    const numChildren = $children.length;
    const singleWidth = Math.floor(currentNavWidth / numChildren - 1);
    // calculate how much width is left for the last element after rounding the base width down
    const restWidth = Math.floor(currentNavWidth - numChildren * singleWidth);
    // adjust width for all elements exept the last element
    $childrenExceptLast.css(
      "width",
      Math.floor(currentNavWidth / numChildren - 1)
    );
    // last element gets rest space
    $lastChild.css("width", singleWidth + restWidth);
    // sub-menus with at least same width (but can wider)
    $children.children("ul").css("min-width", "100%");

    // save currentNavWidth as lastNavWidth
    lastNavWidth = currentNavWidth;
  }
}

/**
 * unset width of each item in the mega-menu
 */
export function unsetMegaMenuItemsWidth() {
  const $navMegaMenu = $(megaMenuSelector);
  const $children = $navMegaMenu.children("li");
  // reset mega-menu-items widths
  $children.css("width", "");
  // reset mega-menu-items sub-menu widths
  $children.children("ul").css("min-width", "");

  // reset lastNavWidth so setMegaMenuItemsWidth recalculates if used again
  lastNavWidth = 0;
}

/**
 * updates a control element for a shopping cart
 * @param {String} control the control element
 */
export function updateCartControl(control) {
  const $control = $(control);
  const config = $.extend(
    { symbol: undefined, badge: undefined, cart: undefined },
    $control.data("cm-cart-control")
  );

  // only apply cart control if configuration is sufficient
  if (
    config.symbol !== undefined &&
    config.badge !== undefined &&
    config.cart !== undefined
  ) {
    // read config of the attached cart
    const cartConfig = $.extend(
      { itemCount: 0 },
      $control.find(config.cart).data("cm-cart")
    );

    // find dom element representing the cart symbol
    const $symbol = $control.find(config.symbol);

    // update cart symbol based on item count
    if (cartConfig.itemCount > 0) {
      $symbol.removeClass("icon-cart-empty");
      $symbol.addClass("icon-cart-with-items");
    } else {
      $symbol.addClass("icon-cart-empty");
      $symbol.removeClass("icon-cart-with-items");
    }
    // add item count to cart badge
    $control.find(config.badge).html(cartConfig.itemCount);
  }
}
