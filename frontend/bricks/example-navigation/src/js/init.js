import $ from "jquery";

import { addNodeDecoratorBySelector } from "@coremedia/brick-node-decoration-service";
import { getLastDevice } from "@coremedia/brick-device-detector";

const BLOCK = "cm-navigation";
const MODIFIER_HOVERED = BLOCK + "--hovered";

const ITEM_BLOCK = "cm-navigation-item";
const ITEM_ELEMENT_TITLE = `${ITEM_BLOCK}__title`;
const ITEM_ELEMENT_TOGGLE = `${ITEM_BLOCK}__toggle`;
const ITEM_ELEMENT_MENU = `${ITEM_BLOCK}__menu`;
const ITEM_MODIFIER_DEPTH_0 = `${ITEM_BLOCK}--depth-0`;
const ITEM_MODIFIER_DEPTH_1 = `${ITEM_BLOCK}--depth-1`;
const ITEM_MODIFIER_OPEN = `${ITEM_BLOCK}--open`;

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
  return getLastDevice().type !== "desktop";
}

addNodeDecoratorBySelector(
  `.${ITEM_MODIFIER_DEPTH_0}`,
  ($navigationRoot) => {
    const $navigationRootList = $navigationRoot.find(`.${ITEM_ELEMENT_MENU}`);
    let $navigationEntries = $navigationRootList.find(`.${ITEM_MODIFIER_DEPTH_1}`);

    // Previously hovered menus could still be visible since they won't disappear until the end of their transition.
    // To make sure that only one menu is visible, we need to set the opacity of all other menus to 0.
    $navigationEntries.mouseover(function() {
      const $currentNavigationEntry = $(this);
      $navigationRootList.addClass(MODIFIER_HOVERED);
      $navigationEntries.not(this).each(function() {
        const $this = $(this);
        $this.find(`ul.${ITEM_ELEMENT_MENU}`).css("opacity", 0);
        $this.css("border-bottom-width", 0);
      });

      $currentNavigationEntry.css("border-bottom-width", 4);
      $currentNavigationEntry
        .find(`ul.${ITEM_ELEMENT_MENU}`)
        .css("opacity", 1);
    });

    $navigationEntries.mouseout(() => {
      $navigationRootList.removeClass(MODIFIER_HOVERED);
    });

    $navigationEntries.on("click", function(e) {
      // prevent further code from beeing executed if a sublist of the list is clicked
      if (e.target.parentNode !== this) return;
      // ignore click on touch devices. we don't want to trigger the link, just display the subnavigation
      if (isTouchDevice && !isMobileOrTablet()) {
        e.preventDefault();
      }
    });
  }
);

addNodeDecoratorBySelector(
  ".cm-header__mobile-navigation-button.cm-hamburger-icon",
  ($hamburgerIcon) => {
    const $body = $("body");
    $hamburgerIcon.on("click touch", () => {
      const toBeOpened = !$hamburgerIcon.hasClass("cm-hamburger-icon--toggled");
      if (toBeOpened) {
        $body.addClass("cm-body--navigation-active");
        $hamburgerIcon.addClass("cm-hamburger-icon--toggled");
      } else {
        $hamburgerIcon.removeClass("cm-hamburger-icon--toggled");
        $body.removeClass("cm-body--navigation-active");
      }
    });
    // activate button as soon as functionality is applied
    $hamburgerIcon.removeAttr("disabled");
  }
);

addNodeDecoratorBySelector(
  ".cm-navigation-item",
  ($navigationItem) => {
    const $toggle = $navigationItem.find(`> .${ITEM_ELEMENT_TOGGLE}`);
    const $title = $navigationItem.find(`> .${ITEM_ELEMENT_TITLE}`);
    const $menu = $navigationItem.find(`> .${ITEM_ELEMENT_MENU}`);
    if ($menu.length > 0) {
      $toggle.on("click touch", () => {
        const toBeOpened = !$navigationItem.hasClass(ITEM_MODIFIER_OPEN);
        $(`.${ITEM_BLOCK}`).removeClass(ITEM_MODIFIER_OPEN);
        if (toBeOpened) {
          $navigationItem.addClass(ITEM_MODIFIER_OPEN);
        }
      });
      // only make title clickable if not a link
      if (!$title.is("a[href]")) {
        $title.on("click touch", () => {
          const toBeOpened = !$navigationItem.hasClass(ITEM_MODIFIER_OPEN);
          $(`.${ITEM_BLOCK}`).removeClass(ITEM_MODIFIER_OPEN);
          if (toBeOpened) {
            $navigationItem.addClass(ITEM_MODIFIER_OPEN);
          }
        });
      }
      // activate button as soon as functionality is applied
      $toggle.removeAttr("disabled");
    }
  }
);
